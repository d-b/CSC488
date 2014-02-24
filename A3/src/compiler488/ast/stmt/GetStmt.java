package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.Readable;
import compiler488.ast.SourceLoc;

/**
 * The command to read data into one or more variables.
 */
public class GetStmt extends Stmt {
    private ASTList<Readable> inputs; // A list of locations to put the values read.

    public GetStmt(ASTList<Readable> inputs, SourceLoc loc) {
    	super(loc);
        this.inputs = inputs;
        inputs.setParent(this);
    }

    /** Returns a string describing the <b>get</b> statement. */
    @Override
    public String toString() {
        return "get " + inputs;
    }

    public ASTList<Readable> getInputs() {
        return inputs;
    }

    public void setInputs(ASTList<Readable> inputs) {
        this.inputs = inputs;
    }
}

