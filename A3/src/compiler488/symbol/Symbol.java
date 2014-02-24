package compiler488.symbol;

import compiler488.ast.type.Type;

public abstract class Symbol {
    public abstract String getName();
    public abstract Type   getType();
}