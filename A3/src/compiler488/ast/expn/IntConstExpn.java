package compiler488.ast.expn;

import compiler488.ast.SourceLoc;

/**
 * Represents a literal integer constant.
 */
public class IntConstExpn extends ConstExpn {
    private Integer value;	// The value of this literal.

    public IntConstExpn(Integer value, SourceLoc loc) {
    	super(loc);
        this.value = value;
    }

    public IntConstExpn(String value, SourceLoc loc) {
    	this(Integer.valueOf(value), loc);
    }

    /** Returns a string representing the value of the literal. */
    @Override
    public String toString () {
        return value.toString();
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}

