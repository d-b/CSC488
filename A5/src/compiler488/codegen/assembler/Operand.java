package compiler488.codegen.assembler;

/**
 * Assembly instruction operand
 *
 * @author Daniel Bloemendal
 */
public interface Operand {
    enum OperandType { OPERAND_INTEGER, OPERAND_STRING }
    public OperandType getType();
}
