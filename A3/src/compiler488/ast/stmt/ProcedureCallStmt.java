package compiler488.ast.stmt;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.ASTList;
import compiler488.ast.IdentNode;
import compiler488.ast.SourceLoc;
import compiler488.ast.expn.Expn;

/**
 * Represents calling a procedure.
 */
public class ProcedureCallStmt extends Stmt {
    private IdentNode ident; // The name of the procedure being called.

    private ASTList<Expn> arguments; // The arguments passed to the procedure.

    public ProcedureCallStmt(IdentNode ident, ASTList<Expn> arguments, SourceLoc loc) {
        super(loc);

        this.ident = ident;
        ident.setParent(this);

        this.arguments = arguments;
        arguments.setParent(this);
    }

    /** Returns a string describing the procedure call. */
    @Override
    public String toString() {
        if (arguments != null) {
            return ident + "(" + arguments + ")";
        } else {
            return ident + "()";
        }
    }

    public ASTList<Expn> getArguments() {
        return arguments;
    }

    public IdentNode getIdent() {
        return ident;
    }

    public String getName() {
        return ident.getId();
    }

    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();

        children.add(arguments);

        return children;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof ProcedureCallStmt)) {
            return false;
        }
        
        return equals((ProcedureCallStmt) o);
    }
    
    public boolean equals(ProcedureCallStmt o) {
        return ident.equals(o.ident) &&
                arguments.equals(o.arguments);
    }
}

