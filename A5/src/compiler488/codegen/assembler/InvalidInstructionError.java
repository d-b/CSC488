package compiler488.codegen.assembler;

/**
 * Thrown when an invalid instruction is encountered
 * 
 * @author Daniel Bloemendal
 */
public class InvalidInstructionError extends Exception {
    private static final long serialVersionUID = 1L;
    InvalidInstructionError(String message, String instruction, int line) {
        this.message = message;
        this.instruction = instruction;
        this.line = line;
    }
    
    // The error message 
    @Override
    public String getMessage() {
        return line + ": " + instruction + " ; " + message;
    }
    
    // Internal members
    String message;
    String instruction;
    int line;
}
