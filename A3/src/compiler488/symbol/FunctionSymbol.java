package compiler488.symbol;

import compiler488.ast.type.Type;
import compiler488.ast.type.FunctionType;

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
    public Type getType() {
        return type;
    }
    
    @Override
    public void setType(Type type) {
        this.type = (FunctionType) type;
    }    
    
    public Boolean hasBody() {
        return defined;
    }
    
    public void hasBody(Boolean value) {
        defined = value;
    }
    
    static public Boolean isFunction(Symbol instance) {
        return (instance instanceof FunctionSymbol) && !((FunctionSymbol) instance).type.getReturnType().equals(Type.TYPE_NIL);
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