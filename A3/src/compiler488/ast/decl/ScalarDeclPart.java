package compiler488.ast.decl;

import compiler488.ast.IdentNode;

/**
 * Represents the identifier declaration of a non-array variable, as it would 
 * appear in a MultiDeclarations. Note that this only consists of the identifier 
 * node.
 * 
 * Example:
 *     var foo, y[1, 2], z [1..5], bar : integer
 *         ^^^                     ^^^
 */
public class ScalarDeclPart extends DeclarationPart {
    /**
     * Construct a node declaring a simple scalar variable of a given name.
     */
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

