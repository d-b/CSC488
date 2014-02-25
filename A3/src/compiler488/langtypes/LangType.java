package compiler488.langtypes;

import compiler488.ast.AST;
import compiler488.ast.SourceLoc;

/**
 * A placeholder for types.
 */
abstract public class LangType {
    public static final LangType TYPE_INTEGER = new IntegerType();
    public static final LangType TYPE_BOOLEAN = new BooleanType();
    public static final LangType TYPE_NIL     = new NilType();
    public static final LangType TYPE_ERROR   = new ErrorType();

    public boolean equals(Object o) {
        return equals(o.getClass());
    }

    public boolean equals(Class<?> cls) {
        return this.getClass().equals(cls);
    }

    public boolean equals(LangType o) {
        return equals(o.getClass());
    }

    public boolean isNil() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isInteger() {
        return false;
    }
    
    public boolean isError() {
        return false;
    }
    
    public boolean isFunction() {
        return false;
    }
    
    public LangType unifyTypes(LangType a, LangType b) {
        if (a.isError() || b.isError()) {
            return TYPE_ERROR;
        }
        
        if (!a.equals(b)) {
            return TYPE_ERROR;
        }
        
        return a;
    }
}

