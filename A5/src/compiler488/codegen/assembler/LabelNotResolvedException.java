package compiler488.codegen.assembler;

/**
 * Thrown when the value of an unresolved label is queried
 *
 * @author Daniel Bloemendal
 */
public class LabelNotResolvedException extends Exception {
    private static final long serialVersionUID = 1L;
    LabelNotResolvedException(String message) {
        super(message);
    }
}
