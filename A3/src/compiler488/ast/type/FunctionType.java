package compiler488.ast.type;

import compiler488.ast.ASTList;
import compiler488.ast.SourceLoc;

/**
 *
 */
public class FunctionType extends Type {
    private Type returnType;
    private ASTList<Type> arguments;

    public FunctionType(Type returnType, ASTList<Type> arguments, SourceLoc loc) {
    	super(loc);
    	
        this.returnType = returnType;
        if (returnType != null) {
        	returnType.setParent(this);
        }
        
        this.arguments = arguments;
        arguments.setParent(this);
    }

    public String toString() {
        return "><";
    }

    public Type evaluatesTo() {
        return returnType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public ASTList<Type> getArguments() {
        return arguments;
    }

    public boolean equals(Object other) {
        if (!(other instanceof FunctionType)) {
            return false;
        }

        FunctionType func = (FunctionType) other;

        return returnType.equals(func.returnType) &&
               arguments.equals(func.arguments);
    }
}

