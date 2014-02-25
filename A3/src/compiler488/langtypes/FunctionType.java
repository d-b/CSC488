package compiler488.langtypes;

import java.util.List;

public class FunctionType extends LangType {
    private LangType returnType;
    private List<LangType> arguments;

    public FunctionType(LangType returnType, List<LangType> arguments) {
        this.returnType = returnType;
        this.arguments = arguments;
    }
    
    public boolean isFunction() {
        return true;
    }

    public String toString() {
        String s = "";
        
        for (LangType t : arguments) {
            s += t + " -> ";
        }
        
        s += returnType.toString();
        
        return s;
    }

    public LangType getReturnType() {
        return returnType;
    }

    public List<LangType> getArguments() {
        return arguments;
    }

    public boolean equals(Object o) {
        if (!(o instanceof FunctionType)) {
            return false;
        }

        return equals((FunctionType) o);
    }
    
    public boolean equals(FunctionType o) {
        return returnType.equals(o.returnType) &&
            arguments.equals(o.arguments);
    }
}

