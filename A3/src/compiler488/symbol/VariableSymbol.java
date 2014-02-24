package compiler488.symbol;

import compiler488.ast.type.Type;

public class VariableSymbol extends Symbol {
    public VariableSymbol(String name) {
        this.name = name;
        this.dimensions = 0;
    }
    
    public VariableSymbol(String name, int lb, int ub) {
        this.name = name;
        this.dimensions = 1;
        this.lowerBounds = new int[this.dimensions];
        this.upperBounds = new int[this.dimensions];
        this.lowerBounds[0] = lb;
        this.upperBounds[0] = ub;
    }
    
    public VariableSymbol(String name, int lb1, int ub1, int lb2, int ub2) {
        this.name = name;
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
    
    @Override
    public void setType(Type type) {
        this.type = type;
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
