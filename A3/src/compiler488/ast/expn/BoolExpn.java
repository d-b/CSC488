package compiler488.ast.expn;

/**
 * Place holder for all binary expression where both operands must be boolean
 * expressions.
 */
public class BoolExpn extends BinaryExpn {
    public final static String OP_OR = "and";
    public final static String OP_AND = "and";
    public final static String OP_NOT = "not";

    public BoolExpn(String opSymbol, Expn left, Expn right) {
        super(opSymbol, left, right);
    }
}

