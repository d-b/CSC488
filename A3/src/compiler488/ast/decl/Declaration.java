package compiler488.ast.decl;

import compiler488.ast.AST;
import compiler488.ast.IdentNode;
import compiler488.ast.SourceLoc;
import compiler488.langtypes.LangType;

/**
 * The common features of declarations.
 */
public class Declaration extends AST {
    /** The type of thing being declared. */
    protected TypeDecl typeDecl;

    /** The name of the thing being declared. */
    protected IdentNode ident;

    public Declaration(IdentNode ident, TypeDecl typeDecl, SourceLoc loc) {
        super(loc);

        this.ident = ident;
        // FIXME MultiDeclaration passes in a NULL for this, and that feels wrong...
        if (ident != null) {
            ident.setParent(this);
        }

        this.typeDecl = typeDecl;
        if (typeDecl != null) {
            typeDecl.setParent(this);
        }   
    }

    public String getName() {
        return ident.getId();
    }

    public IdentNode getIdent() {
        return ident;
    }

    public TypeDecl getTypeDecl() {
        return typeDecl;
    }
    
    public LangType getLangType() {
        return typeDecl.getLangType();
    }

    public String toString() {
        return  ident + " : " + typeDecl;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof Declaration)) {
            return false;
        }
        
        return equals((Declaration) o);
    }
    
    public boolean equals(Declaration o) {
        return typeDecl.equals(o.typeDecl) &&
                ((ident == null) ? (o.ident == null) : ident.equals(o.ident));
    }
}
