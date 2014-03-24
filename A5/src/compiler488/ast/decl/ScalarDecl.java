package compiler488.ast.decl;

import compiler488.ast.IdentNode;
import compiler488.ast.SourceLoc;

/**
 * Represents the declaration of a simple variable (only used in function or procedure arguments.)
 * 
 * Example:
 *      forward proc foo(x : integer, y : boolean)
 *                       ^^^^^^^^^^^  ^^^^^^^^^^^
 */
public class ScalarDecl extends Declaration {
    /**
     * Construct a scalar declaration from a given identifier and type declaration
     */
    public ScalarDecl(IdentNode ident, TypeDecl typeDecl, SourceLoc loc) {
        super(ident, typeDecl, loc);
    }

    /**
     * Returns a string describing the name and type of the object being
     * declared.
     */
    public String toString() {
        return ident + " : " + typeDecl;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof ScalarDecl)) {
            return false;
        }
        
        return equals((ScalarDecl) o);
    }
    
    public boolean equals(ScalarDecl o) {
        return ident.equals(o.ident) &&
                typeDecl.equals(o.typeDecl); 
    }
}
