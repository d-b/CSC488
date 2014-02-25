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
    
    public boolean equals(Object o) {
        if (!(o instanceof ScalarDeclPart)) {
            return false;
        }
        
        return equals((ScalarDeclPart) o);
    }
    
    public boolean equals(ScalarDeclPart o) {
        return ident.equals(o.ident);
    }
}

