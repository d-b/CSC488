package compiler488.ast.type;

import compiler488.ast.SourceLoc;

/**
 * The type of things that may be true or false.
 */
public class BooleanType extends Type {
    public BooleanType() {
        super();
    }
    public BooleanType(SourceLoc loc) {
        super(loc);
    }

    @Override
    public String toString() {
        return "boolean";
    }
}
