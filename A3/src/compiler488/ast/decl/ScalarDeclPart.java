package compiler488.ast.decl;

import compiler488.ast.SourceLoc;

/**
 * Represents the declaration of a simple variable.
 */
public class ScalarDeclPart extends DeclarationPart {
    public ScalarDeclPart(String name, SourceLoc loc) {
        super(name, loc);
    }

    /**
     * Returns a string describing the name of the object being
     * declared.
     */
    @Override
    public String toString() {
        return name;
    }
}

