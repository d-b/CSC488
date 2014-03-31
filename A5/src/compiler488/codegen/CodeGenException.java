package compiler488.codegen;

import java.util.List;

import compiler488.ast.AST;
import compiler488.ast.SourceLocPrettyPrinter;

/**
 * Thrown when an error occurs during code generation
 *
 * @author Daniel Bloemendal
 */
public class CodeGenException extends Exception {
    private static final long serialVersionUID = 1L;
    CodeGenException(String message) {
        this(null, null, message);
    }
    CodeGenException(AST node, String message) {
        this(null, node, message);
    }
    CodeGenException(List<String> source, AST node, String message) {
        super(message);
        this.source = source;
        this.node = node;
        this.trace = new String();
        buildCodeTrace();
    }

    public void setSource(List<String> source) {
        this.source = source; buildCodeTrace();
    }

    public void setNode(AST node) {
        this.node = node; buildCodeTrace();
    }

    public String getCodeTrace() {
        return trace;
    }

    public void printCodeTrace() {
        System.err.print(trace);
    }

    //
    // Build the pretty printed code trace
    //

    private void buildCodeTrace() {
        // Bail out if we do not have an AST node to work with
        if(source == null || node == null) return;
        // Build location message
        trace = node.getLoc().toString() + ": " + getMessage() + "\n";
        trace += SourceLocPrettyPrinter.printToString(source, node);
    }

    // Internal members
    private List<String> source;  // Source listing
    private AST          node;    // Location of error
    private String       trace;   // Code trace details
}
