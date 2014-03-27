package compiler488.codegen.assembler;

/**
 * String operand for an IR assembly instruction
 *
 * @author Daniel Bloemendal
 */
public class StringOperand implements Operand {
    public StringOperand(String value) { this.value = value; }
    public String getValue() { return value; }
    public OperandType getType() { return OperandType.OPERAND_STRING; }

    // Internal members
    private String value;
}
