package compiler488.ast.stmt;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.SourceLoc;
import compiler488.ast.expn.Expn;

/**
 * Represents the command to exit from a loop.
 */

public class ExitStmt extends Stmt {
    // condition for 'exit when'
    private Expn condition = null;

    public ExitStmt(SourceLoc loc) {
        super(loc);
    }

    public ExitStmt(Expn condition, SourceLoc loc) {
        this(loc);

        this.condition = condition;
        condition.setParent(this);
    }

    /** Returns the string <b>"exit"</b> or <b>"exit when e"</b>"
    */
    @Override
    public String toString() {
        String stmt = "exit" ;

        if (condition != null) {
            stmt += " when " + condition;
        }

        return stmt;
    }

    public Expn getCondition() {
        return condition;
    }

    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();
        children.add(condition);
        return children;
    }
}

