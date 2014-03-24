package compiler488.ast.expn;

import compiler488.ast.SourceLoc;

/**
 * Place holder for all binary expression where both operands must be boolean
 * expressions.
 */
public class BoolExpn extends BinaryExpn {
    public final static String OP_OR = "or";
    public final static String OP_AND = "and";
    public final static String OP_NOT = "not";

    public BoolExpn(String opSymbol, Expn left, Expn right, SourceLoc loc) {
        super(opSymbol, left, right, loc);
    }
}

