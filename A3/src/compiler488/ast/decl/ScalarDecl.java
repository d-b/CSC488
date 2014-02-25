package compiler488.ast.decl;

import compiler488.ast.IdentNode;
import compiler488.ast.SourceLoc;
import compiler488.ast.type.Type;

/**
 * Represents the declaration of a simple variable.
 */
public class ScalarDecl extends Declaration {
    public ScalarDecl(IdentNode ident, Type type, SourceLoc loc) {
        super(ident, type, loc);
    }

    /**
     * Returns a string describing the name and type of the object being
     * declared.
     */
    public String toString() {
        return ident + " : " + type;
    }
}

