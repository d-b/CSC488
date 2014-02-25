package compiler488.langtypes;

public class ErrorType extends LangType {
    protected ErrorType() {}
    
    public boolean isError() {
        return true;
    }
    
    public String toString() {
        return "error";
    }
}
