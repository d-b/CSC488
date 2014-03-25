package compiler488.codegen.assembler;

import java.util.List;

/**
 * Assembly IR instruction
 * 
 * @author Daniel Bloemendal
 */
public class Instruction {   
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
