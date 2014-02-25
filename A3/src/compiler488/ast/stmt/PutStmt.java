package compiler488.ast.stmt;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
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
    
    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();
        children.add(outputs);
        return children;
    }
}

