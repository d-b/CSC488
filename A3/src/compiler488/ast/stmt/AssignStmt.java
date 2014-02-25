package compiler488.ast.stmt;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.SourceLoc;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.VarRefExpn;

/**
 * Holds the assignment of an expression to a variable.
 */
public class AssignStmt extends Stmt {
    /*
     * lval is the location being assigned to, and rval is the value being
     * assigned.
     */
    private VarRefExpn lval;
    private Expn rval;

    public AssignStmt(VarRefExpn lval, Expn rval, SourceLoc loc) {
        super(loc);

        this.lval = lval;
        lval.setParent(this);

        this.rval = rval;
        rval.setParent(this);
    }

    /** Returns a string that describes the assignment statement. */
    @Override
    public String toString() {
        return lval + " := " + rval;
    }

    public VarRefExpn getLval() {
        return lval;
    }

    public Expn getRval() {
        return rval;
    }

    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();

        children.add(lval);
        children.add(rval);

        return children;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof AssignStmt)) {
            return false;
        }
        
        return equals((AssignStmt) o);
    }
    
    public boolean equals(AssignStmt o) {
        return lval.equals(o.lval) &&
                rval.equals(o.rval);
    }
}
