package compiler488.ast.decl;

import compiler488.ast.IdentNode;
import compiler488.ast.Indentable;
import compiler488.ast.SourceLoc;
import compiler488.ast.type.Type;

/**
 * The common features of declarations.
 */
public class Declaration extends Indentable {
    /** The type of thing being declared. */
    protected Type type = null;

    /** The name of the thing being declared. */
    protected IdentNode ident;

    public Declaration(IdentNode ident, Type type, SourceLoc loc) {
    	super(loc);

    	this.ident = ident;
        this.type = type;
    }

    public String getName() {
        return ident.getId();
    }
    
    public IdentNode getIdent() {
    	return ident;
    }

    public Type getType() {
        return type;
    }

    public String toString() {
        return  ident + " : " + type;
    }
}

