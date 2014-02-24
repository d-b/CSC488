package compiler488.ast.type;

import compiler488.ast.SourceLoc;

/**
 * Used to denote the return type of a function that doesn't return anything
 * (i.e. the function is actually a procedure.)
 */
public class NilType extends Type {
    public NilType(SourceLoc loc) {
		super(loc);
	}

	@Override
    public String toString() {
        return "><";
    }
}

