package compiler488.symbol;

import compiler488.ast.type.Type;
import compiler488.ast.type.FunctionType;

public class FunctionSymbol extends Symbol {
    FunctionSymbol(String name, FunctionType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }
    
    //
    // Members
    //
    
    String       name;
    FunctionType type;
}