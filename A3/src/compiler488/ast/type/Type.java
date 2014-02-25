package compiler488.ast.type;

import compiler488.ast.AST;
import compiler488.ast.SourceLoc;

/**
 * A placeholder for types.
 */
abstract public class Type extends AST {
    public static final Type TYPE_INTEGER = new IntegerType();
    public static final Type TYPE_BOOLEAN = new BooleanType();
    public static final Type TYPE_NIL     = new NilType();

    public Type() {
        super();
    }

    public Type(SourceLoc loc) {
        super(loc);
    }

    public Type evaluatesTo() {
        return this;
    }

    public boolean equals(Object o) {
        return this.getClass().equals(o.getClass());
    }

    public boolean equals(Class<?> cls) {
        return this.getClass().equals(cls);
    }

    public boolean isNil() {
        return equals(NilType.class);
    }

    public boolean isBoolean() {
        return equals(BooleanType.class);
    }

    public boolean isInteger() {
        return equals(IntegerType.class);
    }
}

