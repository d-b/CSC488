package compiler488.codegen;

import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import compiler488.codegen.Operand.OperandType;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;
import compiler488.runtime.TextReader;

class Assembler {
    //
    // Instructions
    //
    
    @Processor(target="PUSH", operands={OperandType.OPERAND_INTEGER})
    void instructionPush(Instruction i) throws MemoryAddressException, LabelNotResolvedError {
        Machine.writeMemory(next(), Machine.PUSH);
        Machine.writeMemory(next(), i.val(0));
    }
    
    //
    // Helper routines
    //
            
    private short next() {
        return next((short) 1);
    }    
    
    private short next(short offset) {
        short old = codeAddress;
        codeAddress += offset;
        return old;
    }
    
    //
    // Assembler life cycle
    //
    
    // Instantiate an assembler
    Assembler() {
        // Instantiate internals
        instructionMap     = new HashMap<String, Method>();
        codeCurrent        = null;
        codeSections       = new LinkedList<Section>();
        patternInstruction = Pattern.compile("\\s*(\\w+)(\\s+.*)?");
        patternSection     = Pattern.compile("SECTION\\s+(\\.\\w+)", Pattern.CASE_INSENSITIVE);
        // Populate instruction processors 
        populateProcessors();
    }
   
    // Assemble an IR program
    public Boolean Assemble(InputStream input) {
        // Instantiate the text reader
        reader = new TextReader(input);
        
        // Pass 1: instantiate list of instructions
        for(;;) {
            String line; // Input line
            try { line = reader.readLine(); }
            // RuntimeException at EOF
            catch(RuntimeException e) { break; }
            
            // TODO: parse line via regular expressions
        }
        
        return true;
    }
    
    //
    // Instruction parsing
    //
    
    void processInstruction(Instruction instruction) {
        // Get instruction method
        Method m = instructionMap.get(instruction.name.toUpperCase());
        if(m == null)
            {System.err.println("Instruction '" + instruction.name + "' not implemented!"); return;}
        
        // Invoke the processor on  instruction
        try { m.invoke(this, instruction); }
        catch (IllegalAccessException e)    { e.printStackTrace(); }
        catch (IllegalArgumentException e)  { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    
    void populateProcessors() {
        Class<? extends Assembler> thisClass = this.getClass();
        for(Method method : thisClass.getDeclaredMethods()) {
            Processor procInfo = method.getAnnotation(Processor.class);
            if(procInfo != null) instructionMap.put(procInfo.target(), method);
        }        
    }
    
    // TODO: parse operands
    List<Operand> parseOperands(String operands) {
        return new LinkedList<Operand>();
    }    
    
    // Instruction map
    private Map<String, Method> instructionMap;
    
    // Input stream reader
    private TextReader reader;
    
    // Regular expression patterns
    private Pattern patternInstruction;   
    private Pattern patternSection;
    private Pattern patternComment;
    
    // Instantiated sections & instructions
    private short         codeAddress;
    private Section       codeCurrent;
    private List<Section> codeSections;
    
}

//
// Section
//

class Section {
    public String name;
    public List<Instruction> instructions;
    public short size;
    public short address;
}


//
// Instructions and operands
//

class Instruction {
    public String name;
    public List<Operand> operands;
    public int size;

    // Helpers
    public short val(int op) throws LabelNotResolvedError
        {return ((IntegerOperand) operands.get(op)).getValue();}
    public String str(int op)
        {return ((StringOperand) operands.get(op)).getValue();}
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
    LabelOperand() {super((short) 0);}
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
}
