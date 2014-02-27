package compiler488.ast;

import java.io.PrintStream;

public class ASTPrettyPrinterContext {
    private int depth;
    private boolean line_start;
    private static final String IDENT_PER_DEPTH = "    ";
    private PrintStream output;

    /**
     * Construct a context that will produce output to the given stream.
     * 
     * @param output where to print to
     */
    public ASTPrettyPrinterContext(PrintStream output) {
        this.output = output;
        depth = 0;
        line_start = true;
    }

    public void print(AST node) {
        node.prettyPrint(this);
        newline();
    }

    /**
     * Print a string without newlines (but when its starting a new line, 
     * include the correct nesting depth indentation.)  
     * 
     * @param str what to output
     */
    public void print(String str) {
        if (line_start) {
            for (int i = 0; i < depth; i++) {
                output.print(IDENT_PER_DEPTH);
            }

            line_start = false;
        }

        output.print(str);
    }

    /**
     * Output a newline
     */
    public void newline() {
        output.println();
        line_start = true;
    }

    /**
     * Combination print with newline
     * 
     * @param str what to output
     */
    public void println(String str) {
        print(str);
        newline();
    }

    /**
     * Enter into a nesting block
     */
    public void enterBlock() {
        line_start = true;
        depth += 1;
        assert depth > 0;
    }

    /**
     * Exit out of a previously entered nesting block
     */
    public void exitBlock() {
        line_start = true;
        depth -= 1;
        assert depth >= 0;
    }
}
