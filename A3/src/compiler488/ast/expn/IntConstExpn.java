package compiler488.ast.expn;

/**
 * Represents a literal integer constant.
 */
public class IntConstExpn extends ConstExpn {
    private Integer value;	// The value of this literal.

    public IntConstExpn(Integer value) {
        this.value = value;
    }

    public IntConstExpn(String value) {
        this.value = Integer.valueOf(value);
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

