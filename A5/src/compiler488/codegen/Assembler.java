package compiler488.codegen;

import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import compiler488.runtime.TextReader;

class Assembler {   
    //
    // Configuration
    //

    private final static String    SECTION_CODE = ".code";
    private final static Character COMMENT_CHAR = ';';
    
    //
    // Helper routines
    //
            
    Section currentSection() {
        return codeCurrent; 
    }    
    
    void enterSection(String name) {
         Section sec = new Section(name);
         codeCurrent = sec;
         if(name == SECTION_CODE) codeSections.add(0, sec);
         else codeSections.add(sec);
    }
    
    void addLabel(String name) {
        currentSection().addLabel(name);
    }
    
    Short getLabel(String name) {
        for(Section sec : codeSections) {
            Short addr = sec.getLabel(name);
            if(addr != null) return addr;
        } return null;
    }
    
    void addInstruction(String name, List<Operand> operands) {
        // Get instruction specification
        name = name.toUpperCase();
        Processor s = instructionSpec.get(name);
        if(s == null) { System.err.println("Instruction '" + name + "' not implemented!"); return; }
        
        // Check number of arguments
        if(operands.size() != s.operands().length)
            {System.err.println("Instruction '" + s.target() + "' has " + s.operands().length + " operands!"); return;}
        
        // Check argument types
        for(int i = 0; i < operands.size(); i++)
            if(operands.get(i).getType() != s.operands()[i])
                {System.err.println("Invalid argument type for instruction '" + s.target() + "'"); return;}
        
        // Instantiate and add the instruction
        currentSection().addInstruction(new Instruction(name, operands, s.size()));
    }
    
    //
    // Assembler life cycle
    //
    
    // Instantiate an assembler
    Assembler() {
        // Instantiate internals
        instructionMap     = new HashMap<String, Method>();
        instructionSpec    = new HashMap<String, Processor>();
        codeSections       = new LinkedList<Section>();
        patternInstruction = Pattern.compile("\\s*(?:(\\w+):)?\\s*(?:(\\w+)(?:\\s+(.*))?)?");
        patternSection     = Pattern.compile("\\s*SECTION\\s+(\\.\\w+)\\s*", Pattern.CASE_INSENSITIVE);
        // Instantiate the code emitter
        codeEmitter        = new AssemblerIREmitter();
        // Populate instruction processors 
        populateProcessors();
    }
   
    // Assemble an IR program
    public Boolean Assemble(InputStream input) {
        // Reset internal state
        codeCurrent = null;
        codeSections.clear();
        codeEmitter.setEmitter(new Emitter());        
        // Instantiate the text reader
        reader = new TextReader(input);
        
        // REGEX matcher
        Matcher m = null;
        
        // Pass 1: instantiate list of instructions
        for(;;) {
            String line; // Input line
            try { line = reader.readLine(); }
            // RuntimeException at EOF
            catch(RuntimeException e) { break; }
            
            // Remove any comments
            int comment = line.indexOf(COMMENT_CHAR);
            if(comment >= 0) line = line.substring(0, comment);
                       
            // Try parsing it as a section
            m = patternSection.matcher(line);
            if(m.matches()) { enterSection(m.group(1)); continue; }
            
            // Try parsing it as an instruction
            m = patternInstruction.matcher(line);
            if(m.matches()) {
                // If there is no section bail out
                if(currentSection() == null)
                    {System.err.println("Instruction or label outside of a section!"); continue;}
                
                // See if there is a label
                if(m.group(1) != null) addLabel(m.group(1));
                // See if there is an instruction
                if(m.group(2) != null) addInstruction(m.group(2), parseOperands(m.group(3)));
            }
        }
        
        // Pass 2: layout sections
        short baseAddress = 0;
        for(Section section : codeSections) {
            section.setAddress(baseAddress);
            baseAddress = section.getSize();
        }
        
        // Pass 3: resolve label operands
        for(Section section : codeSections)
            for(Instruction instr : section.getInstructions())
                for(Operand op : instr.getOperands())
                    if(op instanceof LabelOperand) {
                        LabelOperand lop = (LabelOperand) op;
                        Short addr = getLabel(lop.getLabel());
                        if(addr != null) lop.setAddress(addr);
                    }
        
        // Pass 4: generate machine code
        for(Section section : codeSections)
            for(Instruction instr : section.getInstructions())
                processInstruction(instr);
        
        // Successful assembly
        return true;
    }
    
    //
    // Instruction parsing
    //
    
    void processInstruction(Instruction instruction) {
        // Get instruction method
        String name = instruction.getName().toUpperCase();
        Method m = instructionMap.get(name); 
        
        // Invoke the processor on  instruction
        try { m.invoke(getProcessorManager(), instruction); }
        catch (IllegalAccessException e)    { e.printStackTrace(); }
        catch (IllegalArgumentException e)  { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
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
    
    List<Operand> parseOperands(String operands) {
        List<Operand> result = new LinkedList<Operand>();
        for(String part : operands.split(" ")) {
            // If it is a string
            if(part.charAt(0) == '"')
                result.add(new StringOperand(part.substring(1, part.length() - 2)));
            // If it is a number
            else if(Character.isDigit(part.charAt(0)))
                result.add(new IntegerOperand((short) Integer.parseInt(part)));
            // If it is a label
            else if(Character.isAlphabetic(part.charAt(0)))
                result.add(new LabelOperand(part));
        } return result;
    }    
   
    //
    // Members
    //
    
    // Instruction maps
    private Map<String, Method> instructionMap;
    private Map<String, Processor> instructionSpec;
    
    // Input stream reader
    private TextReader reader;
    
    // Regular expression patterns
    private Pattern patternInstruction;   
    private Pattern patternSection;
    
    // Instantiated sections & instructions
    private List<Section> codeSections;
    private Section codeCurrent;
    
    // Machine code emitter
    AssemblerIREmitter codeEmitter;
    
}

//
// Section
//

class Section {
    // New section
    Section(String name) {
        this.name = name;
        this.instructions = new LinkedList<Instruction>();
        this.labels = new HashMap<String, Short>();
        this.size = 0;
        this.address = 0;
    }
    
    //
    // Labels & instructions
    //
    
    public void addLabel(String name) {
        labels.put(name, size);
    }
    
    public Short getLabel(String name) {
        Short offset = labels.get(name);
        if (offset == null) return null;
        return (short)(address + offset);
    }
    
    public void addInstruction(Instruction instruction) {
        this.size += instruction.getSize();
        instructions.add(instruction);
    }
    
    public List<Instruction> getInstructions() {
        return Collections.unmodifiableList(instructions);
    }
   
    // Getters/setters
    public String getName() { return name; }    
    public short getAddress() { return address; }
    public void setAddress(short address) { this.address = address; }
    public short getSize() { return size; }

    // Internal members
    private String name;
    private List<Instruction> instructions;
    private Map<String, Short> labels;
    private short size;
    private short address;
}

//
// Instructions and operands
//

class Instruction {   
    // New instruction
    Instruction(String name, List<Operand> operands, int size) {
        this.name = name;
        this.operands = operands;
        this.size = size;
    }
    
    // Getters
    public String getName() { return name; }
    public List<Operand> getOperands() { return operands; }
    public int getSize() { return size; }

    // Helpers
    public short val(int op) throws LabelNotResolvedError
        {return ((IntegerOperand) operands.get(op)).getValue();}
    public String str(int op)
        {return ((StringOperand) operands.get(op)).getValue();}
    
    // Internal members
    private String name;
    private List<Operand> operands;
    private int size;
}

interface Operand {
    enum OperandType { OPERAND_INTEGER, OPERAND_STRING }
    public OperandType getType();
}

class IntegerOperand implements Operand  {
    IntegerOperand(short value) { this.value = value; }
    public short getValue()  throws LabelNotResolvedError { return value; }
    public OperandType getType() { return OperandType.OPERAND_INTEGER; }
    
    // Internal members
    private short value;
}

class LabelOperand extends IntegerOperand {
    LabelOperand(String label) {super((short) 0); this.label = label;}
    public void setAddress(short address) { resolved = true; value = address; }
    public String getLabel() { return label; }
    
    // Get resolved value
    public short getValue() throws LabelNotResolvedError {
        if(!resolved) throw new LabelNotResolvedError("label '" + label + "' has not been resolved" );
        return value;
    }
    
    // Internal members
    private boolean resolved; 
    private String label;
    private short value;
}

class StringOperand implements Operand {
    StringOperand(String value) { this.value = value; }
    public String getValue() { return value; }
    public OperandType getType() { return OperandType.OPERAND_STRING; }
    
    // Internal members
    private String value;
}

//
// Instruction processor
//

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Processor {
    String target();
    Operand.OperandType[] operands();
    int size();
}
