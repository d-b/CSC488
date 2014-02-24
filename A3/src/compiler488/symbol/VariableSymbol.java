package compiler488.symbol;

import compiler488.ast.type.Type;

public class VariableSymbol extends Symbol {
    VariableSymbol(String name, Type type) {
        this.name = name;
        this.type = type;
        this.dimensions = 0;
    }
    
    VariableSymbol(String name, Type type, int lb, int ub) {
        this.name = name;
        this.type = type;
        this.dimensions = 1;
        this.lowerBounds = new int[this.dimensions];
        this.upperBounds = new int[this.dimensions];
        this.lowerBounds[0] = lb;
        this.upperBounds[0] = ub;
    }
    
    VariableSymbol(String name, Type type, int lb1, int ub1, int lb2, int ub2) {
        this.name = name;
        this.type = type;
        this.dimensions = 2;
        this.lowerBounds = new int[this.dimensions];
        this.upperBounds = new int[this.dimensions];
        this.lowerBounds[0] = lb1;
        this.upperBounds[0] = ub1;
        this.lowerBounds[1] = lb2;
        this.upperBounds[1] = ub2;                
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
    
    String name;
    Type   type;
    int    dimensions;
    int    lowerBounds[];
    int    upperBounds[];
}
