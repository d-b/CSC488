package compiler488.codegen.assembler;

/**
 * Thrown when the size of a program exceeds size of machine memory
 *
 * @author Daniel Bloemendal
 */
public class ProgramSizeException extends Exception {
    private static final long serialVersionUID = 1L;
    ProgramSizeException(String message) {
        super(message);
    }
}
