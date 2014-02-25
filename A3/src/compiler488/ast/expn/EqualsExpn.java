package compiler488.ast.expn;

import compiler488.ast.SourceLoc;

/**
 * Place holder for all binary expression where both operands could be either
 * integer or boolean expressions. e.g. = and not = comparisons
 */
public class EqualsExpn extends BinaryExpn {
    public final static String OP_EQUAL = "=";
    public final static String OP_NOT_EQUAL = "not=";

    public EqualsExpn(String opSymbol, Expn left, Expn right, SourceLoc loc) {
        super(opSymbol, left, right, loc);
    }
}

