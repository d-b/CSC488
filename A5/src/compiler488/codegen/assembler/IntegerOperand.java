package compiler488.codegen.assembler;

/**
 * Integer operand for an IR assembly instruction
 *
 * @author Daniel Bloemendal
 */
public class IntegerOperand implements Operand  {
    public IntegerOperand(int value) { this.value = value; }
    public int getValue()  throws LabelNotResolvedException { return value; }
    public OperandType getType() { return OperandType.OPERAND_INTEGER; }

    // Internal members
    private int value;
}
