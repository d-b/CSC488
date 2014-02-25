package compiler488.ast.expn;

import compiler488.ast.SourceLoc;

/**
 * Represents the boolean negation of an expression.
 */
public class NotExpn extends UnaryExpn {
    public NotExpn(Expn operand, SourceLoc loc) {
        super(UnaryExpn.OP_NOT, operand, loc);
    }
}
