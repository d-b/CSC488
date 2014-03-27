package compiler488.codegen.assembler;

/**
 * Integer operand for an IR assembly instruction
 * 
 * @author Daniel Bloemendal
 */
public class IntegerOperand implements Operand  {
    public IntegerOperand(short value) { this.value = value; }
    public short getValue()  throws LabelNotResolvedError { return value; }
    public OperandType getType() { return OperandType.OPERAND_INTEGER; }

    // Internal members
    private short value;
}
