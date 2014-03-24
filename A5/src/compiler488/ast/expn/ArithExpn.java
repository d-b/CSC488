package compiler488.ast.expn;

import compiler488.ast.SourceLoc;

/**
 * Place holder for all binary expression where both operands must be integer
 * expressions.
 */
public class ArithExpn extends BinaryExpn {
    public final static String OP_PLUS = "+";
    public final static String OP_MINUS = "-";
    public final static String OP_TIMES = "*";
    public final static String OP_DIVIDE = "/";

    public ArithExpn(String opSymbol, Expn left, Expn right, SourceLoc loc) {
        super(opSymbol, left, right, loc);
    }
}

