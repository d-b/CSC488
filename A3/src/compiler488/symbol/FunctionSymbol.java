package compiler488.symbol;

import compiler488.langtypes.FunctionType;
import compiler488.langtypes.LangType;

public class FunctionSymbol extends Symbol {
    public FunctionSymbol(String name, FunctionType type) {
        this.name = name;
        this.type = type;
        this.defined = false;
    }
    
    public FunctionSymbol(String name, FunctionType type, Boolean hasBody) {
        this.name = name;
        this.type = type;
        this.defined = hasBody;
    }    

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LangType getType() {
        return type;
    }
    
    @Override
    public void setType(LangType type) {
        this.type = (FunctionType) type;
    }    
    
    public Boolean hasBody() {
        return defined;
    }
    
    public void hasBody(Boolean value) {
        defined = value;
    }
    
    static public Boolean isFunction(Symbol instance) {
        return (instance instanceof FunctionSymbol) &&
        		!(((FunctionSymbol) instance).type.getReturnType().isNil());
    }    
    
    static public Boolean isForward(Symbol instance) {
        return (instance instanceof FunctionSymbol) && !((FunctionSymbol) instance).hasBody();
    }
    
    //
    // Members
    //
    
    String       name;
    FunctionType type;
    Boolean      defined;
}