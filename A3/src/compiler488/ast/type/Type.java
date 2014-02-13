package compiler488.ast.type;

import compiler488.ast.AST;

/**
 * A placeholder for types.
 */
abstract public class Type extends AST {
    public final static Type TYPE_NIL = new NilType();
    public final static Type TYPE_INTEGER = new IntegerType();
    public final static Type TYPE_BOOLEAN = new BooleanType();

    public Type evaluatesTo() {
        return this;
    }

    public boolean equals(Object o) {
        return this == o;
    }
}

