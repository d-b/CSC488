package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.Printable;
import compiler488.ast.SourceLoc;
import compiler488.ast.expn.Expn;

/**
 * The command to write data on the output device.
 */
public class PutStmt extends Stmt {
    private ASTList<Expn> outputs; // The objects to be printed.

    public PutStmt (ASTList<Expn> outputs, SourceLoc loc) {
    	super(loc);
        this.outputs = outputs;
        outputs.setParent(this);
    }

    /** Returns a description of the <b>put</b> statement. */
    @Override
    public String toString() {
        return "put " + outputs;
    }

    public ASTList<Expn> getOutputs() {
        return outputs;
    }

    public void setOutputs(ASTList<Expn> outputs) {
        this.outputs = outputs;
    }
}

