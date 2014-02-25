package compiler488.langtypes;

public class NilType extends LangType {
    protected NilType() {}
    
    public boolean isNil() {
        return true;
    }
    
    public String toString() {
        return "nil";
    }
}
