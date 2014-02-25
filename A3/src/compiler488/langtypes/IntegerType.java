package compiler488.langtypes;

/**
 * Used to declare objects that yield integers.
 */
public class IntegerType extends LangType {
    protected IntegerType() {}
    
    public boolean isInteger() {
        return true;
    }
    
    public String toString() {
        return "integer";
    }
}

