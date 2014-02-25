package compiler488.ast.decl;

import compiler488.ast.AST;
import compiler488.ast.IdentNode;
import compiler488.ast.SourceLoc;
import compiler488.ast.type.Type;

/**
 * The common features of declarations.
 */
public class Declaration extends AST {
    /** The type of thing being declared. */
    protected Type type;

    /** The name of the thing being declared. */
    protected IdentNode ident;

    public Declaration(IdentNode ident, Type type, SourceLoc loc) {
        super(loc);

        this.ident = ident;
        // FIXME MultiDeclaration passes in a NULL for this, and that feels wrong...
        if (ident != null) {
            ident.setParent(this);
        }

        this.type = type;
        type.setParent(this);
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
    
    public boolean equals(Object o) {
        if (!(o instanceof Declaration)) {
            return false;
        }
        
        return equals((Declaration) o);
    }
    
    public boolean equals(Declaration o) {
        return type.equals(o.type) &&
                ((ident == null) ? (o.ident == null) : ident.equals(o.ident));
    }
}
