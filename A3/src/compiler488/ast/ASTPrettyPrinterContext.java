package compiler488.ast;

import java.io.PrintStream;

public class ASTPrettyPrinterContext {
    private int depth;
    private boolean line_start;
    private static final String IDENT_PER_DEPTH = "    ";
    private PrintStream output;

    public ASTPrettyPrinterContext(PrintStream output) {
        this.output = output;
        depth = 0;
        line_start = true;
    }

    public void print(AST node) {
        node.prettyPrint(this);
        newline();
    }

    public void print(String str) {
        if (line_start) {
            for (int i = 0; i < depth; i++) {
                output.print(IDENT_PER_DEPTH);
            }

            line_start = false;
        }

        output.print(str);
    }

    public void newline() {
        output.println();
        line_start = true;
    }

    public void println(String str) {
        print(str);
        newline();
    }

    public void enterBlock() {
        depth += 1;
        assert depth > 0;
    }

    public void exitBlock() {
        depth -= 1;
        assert depth >= 0;
    }
}
