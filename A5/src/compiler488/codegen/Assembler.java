package compiler488.codegen;

import java.io.InputStream;
import java.util.List;

class Assembler {
    Assembler(InputStream input) {
        
    }
}

//
// Instructions and operands
//

class Instruction {
    String name;
    List<Operand> operands;
    int size;   
}

interface Operand {
    public boolean isInteger();
    public boolean isString();
}

class IntegerOperand implements Operand  {
    IntegerOperand(short value) { this.value = value; }
    public short getValue()  throws LabelNotResolvedError { return value; }
    public boolean isInteger() { return true; }
    public boolean isString()  { return false; }
    
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
    public boolean isInteger() { return false; }
    public boolean isString()  { return true; }
}

class LabelNotResolvedError extends Exception {
    private static final long serialVersionUID = 1L;
    LabelNotResolvedError(String message) {super(message);}
}
