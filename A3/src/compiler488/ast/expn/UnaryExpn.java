package compiler488.ast.expn;

import compiler488.ast.SourceLoc;

/**
 * The common features of unary expressions.
 */
public class UnaryExpn extends Expn {
    public final static String OP_NOT = "not";
    public final static String OP_MINUS = "-";

    private Expn operand;	/* operand of the unary  operator. */
    private String opSymbol;	/* Name of the operator. */

    public UnaryExpn(String opSymbol, Expn operand, SourceLoc loc) {
    	super(loc);
        this.opSymbol = opSymbol;
        this.operand = operand;
        operand.setParent(this);
    }

    /** Returns a string that represents the unary expression. */
    @Override
    public String toString() {
        return ( opSymbol + "(" + operand + ")");
    }

    public Expn getOperand() {
        return operand;
    }

    public void setOperand(Expn operand) {
        this.operand = operand;
    }

    public String getOpSymbol() {
        return opSymbol;
    }

    public void setOpSymbol(String opSymbol) {
        this.opSymbol = opSymbol;
    }
}

