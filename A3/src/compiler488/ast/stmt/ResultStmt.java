package compiler488.ast.stmt;

import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.Indentable;
import compiler488.ast.SourceLoc;
import compiler488.ast.expn.Expn;

/**
 * The command to return from a function or procedure.
 */
public class ResultStmt extends Stmt {
    // The value to be returned by a function.
    private Expn value = null;

    public ResultStmt(Expn value, SourceLoc loc) {
    	super(loc);

        this.value = value;
        value.setParent(this);
    }

    /**
     * Print <b>result</b>  expression on a line, by itself.
     *
     * @param out
     *            Where to print.
     * @param depth
     *            How much indentation to use while printing.
     */
    @Override
    public void printOn(PrintStream out, int depth) {
        Indentable.printIndentOn(out, depth);
        out.println("result " + value );
    }

    public Expn getValue() {
        return value;
    }

    public void setValue(Expn value) {
        this.value = value;
    }
    
    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();
        children.add(value);
        return children;
    }
}

