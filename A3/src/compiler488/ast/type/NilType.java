package compiler488.ast.type;

/**
 * Used to denote the return type of a function that doesn't return anything
 * (i.e. the function is actually a procedure.)
 */
public class NilType extends Type {
    @Override
    public String toString() {
        return "><";
    }
}

