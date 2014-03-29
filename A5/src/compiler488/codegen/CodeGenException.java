package compiler488.codegen;

/**
 * Thrown when an error occurs during code generation
 *
 * @author Daniel Bloemendal
 */
public class CodeGenException extends Exception {
    private static final long serialVersionUID = 1L;
    CodeGenException(String message) { super(message); }
}
