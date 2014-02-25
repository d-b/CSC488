package compiler488.ast.expn;

import compiler488.ast.SourceLoc;

/**
 * Boolean literal constants.
 */
public class BoolConstExpn extends ConstExpn {
    private boolean  value;	/* value of the constant */

    public BoolConstExpn(boolean value, SourceLoc loc) {
    	super(loc);

        this.value = value;
    }

    /** Returns the value of the boolean constant */
    @Override
    public String toString() {
        return value ? "(true)" : "(false)";
    }

    public boolean getValue() {
        return value;
    }
}
