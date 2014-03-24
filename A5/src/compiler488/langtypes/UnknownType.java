package compiler488.langtypes;

public class UnknownType extends LangType {
    protected UnknownType() {}
    
    public boolean isUnknown() {
        return true;
    }
    
    public String toString() {
        return "unknown";
    }
}
