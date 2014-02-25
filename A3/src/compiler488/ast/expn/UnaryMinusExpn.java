package compiler488.ast.expn;

import compiler488.ast.SourceLoc;

/**
 * Represents negation of an integer expression
 */
public class UnaryMinusExpn extends UnaryExpn {
    public UnaryMinusExpn(Expn operand, SourceLoc loc) {
        super(UnaryExpn.OP_MINUS, operand, loc);
    }
}
