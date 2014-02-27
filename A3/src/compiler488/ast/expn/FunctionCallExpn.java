package compiler488.ast.expn;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.ASTList;
import compiler488.ast.Callable;
import compiler488.ast.IdentNode;
import compiler488.ast.SourceLoc;

/**
 * Represents a function call with zero or more arguments.
 */
public class FunctionCallExpn extends Expn implements Callable {
    /** The identifier that names the function to be called */
    private IdentNode ident;

    /** he arguments passed to the function. */
    private ASTList<Expn> arguments;

    public FunctionCallExpn(IdentNode ident, ASTList<Expn> arguments, SourceLoc loc) {
        super(loc);

        this.ident = ident;
        ident.setParent(this);

        this.arguments = arguments;
        arguments.setParent(this);
    }

    /** Returns a string describing the function call. */
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

    public String getName() {
        return ident.getId();
    }

    public IdentNode getIdent() {
        return ident;
    }

    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();

        children.add(arguments);

        return children;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof FunctionCallExpn)) {
            return false;
        }
        
        return equals((FunctionCallExpn) o);
    }
    
    public boolean equals(FunctionCallExpn o) {
        return ident.equals(o.ident) &&
                arguments.equals(o.arguments);
    }
}
