package compiler488.ast.decl;

import compiler488.ast.AST;
import compiler488.ast.IdentNode;
import compiler488.ast.SourceLoc;

/**
 * The common features of declarations' parts.
 */
public class DeclarationPart extends AST {
    /** The name of the thing being declared. */
    protected IdentNode ident;

    public DeclarationPart(IdentNode ident, SourceLoc loc) {
        super(loc);
        this.ident = ident;
        ident.setParent(this);
    }

    public String getName() {
        return ident.getId();
    }

    public IdentNode getIdent() {
        return ident;
    }

    public String toString() {
        return ident.toString();
    }
}
