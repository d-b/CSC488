package compiler488.ast.type;

import compiler488.ast.AST;
import compiler488.ast.SourceLoc;

/**
 * A placeholder for types.
 */
abstract public class Type extends AST {
    public Type(SourceLoc loc) {
    	super(loc);
    }

    public Type evaluatesTo() {
        return this;
    }

    public boolean equals(Object o) {
        return this.getClass().equals(o.getClass());
    }
}

