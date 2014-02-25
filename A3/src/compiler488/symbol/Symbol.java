package compiler488.symbol;

import compiler488.langtypes.LangType;

public abstract class Symbol {
    public abstract String getName();
    public abstract LangType   getType();
    public abstract void   setType(LangType type);
}