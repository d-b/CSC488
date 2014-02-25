package compiler488.ast.decl;

import compiler488.ast.IdentNode;

/**
 * Represents the declaration of a simple variable.
 */
public class ScalarDeclPart extends DeclarationPart {
    public ScalarDeclPart(IdentNode ident) {
        super(ident, ident);
    }

    public String toString() {
        return ident.toString();
    }
}

