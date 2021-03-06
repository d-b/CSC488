package compiler488.codegen.assembler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import compiler488.runtime.ExecutionException;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;
import compiler488.runtime.TextReader;
import compiler488.codegen.assembler.Operand.OperandType;
import compiler488.codegen.assembler.ir.AssemblerIREmitter;
import compiler488.codegen.assembler.ir.Emitter;
import compiler488.compiler.Main;

/**
 * Assembler core for IR to 488 machine code.
 *
 * @author Daniel Bloemendal
 */
public class Assembler {
    //
    // Configuration
    //

    private final static String    SECTION_CODE = ".code";
    private final static Character COMMENT_CHAR = ';';

    //
    // Assemble from command line
    //

    public static void main(String argv[]) throws UnsupportedEncodingException, ExecutionException, FileNotFoundException {
        // Check arguments
        if(argv.length <= 0) { System.err.println("Usage: <filename>"); return; }

        // Start the machine
        Main.traceStream = System.out;
        Machine.powerOn();

        // Assemble the file
        Assembler assembler = new Assembler();
        InputStream stream = new FileInputStream(argv[0]);
        try { assembler.Assemble(stream); }
        catch(InvalidInstructionException e)
            { System.err.println(e.getMessage()); return; }
        catch(LabelNotResolvedException e)
            { System.err.println(e.getMessage()); return; }
        catch(ProgramSizeException e)
            { System.err.println(e.getMessage()); return; }

        // Run the code
        Machine.setPC((short) 0);
        Machine.setMSP((short) assembler.getSize());
        Machine.setMLP((short) (Machine.memorySize - 1));
        Machine.run();
    }

    //
    // Assembler life cycle
    //

    // Instantiate an assembler
    public Assembler() {
        // Instantiate internals
        instructionMap     = new HashMap<String, Method>();
        instructionSpec    = new HashMap<String, Processor>();
        codeSectionList    = new LinkedList<Section>();
        // Regular expressions
        patternInstruction = Pattern.compile("\\s*(?:(\\w+):)?\\s*(?:(\\w+)(?:\\s+(.*))?)?");
        patternSection     = Pattern.compile("\\s*SECTION\\s+(\\.\\w+)\\s*", Pattern.CASE_INSENSITIVE);
        patternOpBoolean   = Pattern.compile("\\$true|\\$false", Pattern.CASE_INSENSITIVE);
        patternOpString    = Pattern.compile("\"[^\"]*\"");
        patternOpLabel     = Pattern.compile("[a-zA-Z_]\\w*");
        // Instantiate the code emitter
        codeEmitter        = new AssemblerIREmitter();
        // Populate instruction processors
        populateProcessors();
    }

    // Assemble an IR program
    public void Assemble(InputStream input) throws InvalidInstructionException, LabelNotResolvedException, ProgramSizeException {
        // Reset internal state
        codeSection = null;
        codeSectionList.clear();
        codeLine = 0;
        codeInstruction = new String();
        Emitter emitter = new Emitter();
        codeEmitter.setEmitter(emitter);
        // Instantiate the text reader
        reader = new TextReader(input);

        // REGEX matcher
        Matcher m = null;

        // Pass 1: instantiate list of instructions
        for(codeLine = 1;; codeLine++) {
            // Read single line from stream
            try { codeInstruction = reader.readLine(); }
            // RuntimeException at EOF
            catch(RuntimeException e) { break; }

            // Remove any comments
            int comment = codeInstruction.indexOf(COMMENT_CHAR);
            if(comment >= 0) codeInstruction = codeInstruction.substring(0, comment);

            // Try parsing it as a section
            m = patternSection.matcher(codeInstruction);
            if(m.matches()) { enterSection(m.group(1)); continue; }

            // Try parsing it as an instruction
            m = patternInstruction.matcher(codeInstruction);
            if(m.matches()) {
                // If there is no section bail out
                if(currentSection() == null && (m.group(1) != null || m.group(2) != null))
                    invalidInstruction("Instruction and/or label outside of a section!");

                // See if there is a label
                if(m.group(1) != null) addLabel(m.group(1));
                // See if there is an instruction
                if(m.group(2) != null) addInstruction(m.group(2), parseOperands(m.group(3)));
            }
        }

        // Add section for text constants
        Section textConst = enterSection(".textconst");
        emitter.setDataSection(textConst);

        // Pass 2: layout sections
        int baseAddress = 0;
        for(Section section : codeSectionList) {
            section.setAddress(baseAddress);
            baseAddress += section.getSize();
        }

        // Pass 3: resolve label operands
        for(Section section : codeSectionList)
            for(Instruction instr : section.getInstructions())
                for(Operand op : instr.getOperands())
                    if(op instanceof LabelOperand) {
                        LabelOperand lop = (LabelOperand) op;
                        Integer addr = getLabel(lop.getLabel());
                        if(addr != null) lop.setAddress(addr);
                    }

        // Pass 4: generate machine code
        for(Section section : codeSectionList)
            for(Instruction instr : section.getInstructions())
                processInstruction(instr);

        // Check final program size, we need at least one byte for the stack
        if(getSize() > Machine.memorySize - 1)
            throw new ProgramSizeException("size of program, " + getSize() + " words, exceeds machine maximum of " + (Machine.memorySize - 1));
    }

    public int getSize() {
        Section sec = currentSection();
        if(sec == null) return 0;
        return sec.getAddress() + sec.getSize();
    }

    //
    // Instruction parsing
    //

    void processInstruction(Instruction instruction) throws LabelNotResolvedException {
        // Get instruction method
        String name = instruction.getName().toUpperCase();
        Method m = instructionMap.get(name);
        m.setAccessible(true);

        // Invoke the processor on  instruction
        try { m.invoke(getProcessorManager(), instruction); }
        catch(IllegalAccessException e)    { throw new RuntimeException(e); }
        catch(IllegalArgumentException e)  { throw new RuntimeException(e); }
        catch(InvocationTargetException e) {
            // If it is a label error
            if(e.getCause() instanceof LabelNotResolvedException)
                throw (LabelNotResolvedException) e.getCause();
            // If it is a memory address exception, deal with it later during size check
            if(e.getCause() instanceof MemoryAddressException) return;
            // Otherwise it was something much more serious
            else throw new RuntimeException(e);
        }
    }

    void populateProcessors() {
        // Target class
        Class<?> target = getProcessorManager().getClass();
        // Get class tree
        Deque<Class<?>> classes = new LinkedList<Class<?>>();
        for(Class<?> cls = target; !cls.equals(Object.class); cls = cls.getSuperclass())
            classes.push(cls);
        // Loop over classes
        while(!classes.isEmpty()) {
            Class<?> cls = classes.pop();
            for(Method method : cls.getDeclaredMethods()) {
                Processor procInfo = method.getAnnotation(Processor.class);
                if(procInfo != null) {
                    instructionMap.put(procInfo.target(), method);
                    instructionSpec.put(procInfo.target(), procInfo);
                }
            }
        }
    }

    Object getProcessorManager() {
        return codeEmitter;
    }

    List<Operand> parseOperands(String operands) throws InvalidInstructionException {
        List<Operand> result = new LinkedList<Operand>();
        if(operands == null) return result; // No operands provided
        for(String part : operands.split("[ ]+(?=([^\"]*\"[^\"]*\")*[^\"]*$)")) {
            // If it is a label
            if(patternOpLabel.matcher(part).matches())
                result.add(new LabelOperand(part));
            // If it is a string
            else if(patternOpString.matcher(part).matches())
                result.add(new StringOperand(part.substring(1, part.length() - 1)));
            // If it is a boolean
            else if(patternOpBoolean.matcher(part).matches())
                result.add(new IntegerOperand(part.toLowerCase().equals("$true")
                        ? Machine.MACHINE_TRUE : Machine.MACHINE_FALSE));
            // If it is a number
            else try {
                // Check range of integer operand
                int index = result.size() + 1;
                int value = Integer.parseInt(part);
                if(value < Machine.MIN_INTEGER)
                    invalidInstruction("integer operand " + index + " is less than the minimum machine value " + Machine.MIN_INTEGER);
                if(value > Machine.MAX_INTEGER)
                    invalidInstruction("integer operand " + index + " is greater than the maximum machine value " + Machine.MAX_INTEGER);
                // Add the operand
                result.add(new IntegerOperand(value));
            } catch(NumberFormatException e) {
                throw new InvalidInstructionException("invalid operand", codeInstruction, codeLine);
            }
        } return result;
    }

    //
    // Helper routines
    //

    Section currentSection() {
        return codeSection;
    }

    Section enterSection(String name) {
        Section sec = new Section(name);
        codeSection = sec;
        if(name.equals(SECTION_CODE)) codeSectionList.add(0, sec);
        else codeSectionList.add(sec);
        return sec;
    }

    void addLabel(String name) {
        currentSection().addLabel(name);
    }

    Integer getLabel(String name) {
        for(Section sec : codeSectionList) {
            Integer addr = sec.getLabel(name);
            if(addr != null) return addr;
        } return null;
    }

    void invalidInstruction(String message) throws InvalidInstructionException {
        throw new InvalidInstructionException(message, codeInstruction, codeLine);
    }

    void addInstruction(String name, List<Operand> operands) throws InvalidInstructionException, ProgramSizeException {
        // Get instruction specification
        name = name.toUpperCase();
        Processor s = instructionSpec.get(name);
        if(s == null) invalidInstruction("Instruction unknown!");

        // Check number of arguments
        if(operands.size() != s.operands().length)
            invalidInstruction(s.operands().length + " operands are required, but " + operands.size() + " were provided! ");

        // Check argument types
        for(int i = 0; i < operands.size(); i++)
            if(operands.get(i).getType() != s.operands()[i])
                invalidInstruction("Operand " + (i + 1) + " must be "
                        + (s.operands()[i].equals(OperandType.OPERAND_INTEGER) ?
                        "an integer literal or label!" : "a string!"));

        // Instantiate and add the instruction
        currentSection().addInstruction(new Instruction(name, operands, s.size()));
    }

    //
    // Members
    //

    // Instruction maps
    private Map<String, Method>    instructionMap;
    private Map<String, Processor> instructionSpec;

    // Regular expression patterns
    private Pattern patternInstruction;
    private Pattern patternSection;
    private Pattern patternOpBoolean;
    private Pattern patternOpString;
    private Pattern patternOpLabel;

    // Input stream reader
    private TextReader reader;

    // Assembler state
    private Section            codeSection;
    private List<Section>      codeSectionList;
    private String             codeInstruction;
    private int                codeLine;
    private AssemblerIREmitter codeEmitter;
}
