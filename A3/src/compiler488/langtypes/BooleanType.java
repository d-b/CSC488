package compiler488.langtypes;

/**
 * The type of things that may be true or false.
 */
public class BooleanType extends LangType {
    protected BooleanType() {}
    
    public boolean isBoolean() {
        return true;
    }

    public String toString() {
        return "boolean";
    }
}

