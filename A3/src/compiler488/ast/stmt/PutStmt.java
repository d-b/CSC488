package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.Printable;
import compiler488.ast.SourceLoc;

/**
 * The command to write data on the output device.
 */
public class PutStmt extends Stmt {
    private ASTList<Printable> outputs; // The objects to be printed.

    public PutStmt (ASTList<Printable> outputs, SourceLoc loc) {
    	super(loc);
        this.outputs = outputs;
        outputs.setParent(this);
    }

    /** Returns a description of the <b>put</b> statement. */
    @Override
    public String toString() {
        return "put " + outputs;
    }

    public ASTList<Printable> getOutputs() {
        return outputs;
    }

    public void setOutputs(ASTList<Printable> outputs) {
        this.outputs = outputs;
    }
}

