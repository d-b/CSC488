package compiler488.ast.type;

import compiler488.ast.ASTList;

/**
 *
 */
public class FunctionType extends Type {
    private Type returnType;
    private ASTList<Type> arguments;

    public FunctionType(Type returnType, ASTList<Type> arguments) {
        this.returnType = returnType;
        this.arguments = arguments;
        returnType.setParent(this);
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

