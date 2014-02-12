package compiler488.ast.decl;

import compiler488.ast.type.Type;

/**
 * Represents the declaration of a simple variable.
 */

public class ScalarDecl extends Declaration {
    public ScalarDecl(String name, Type type) {
        super(name, type);
    }

    /**
     * Returns a string describing the name and type of the object being
     * declared.
     */
    @Override
    public String toString() {
        return name + " : " + type;
    }
}

