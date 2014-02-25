package compiler488.ast.expn;

import compiler488.ast.IntConstNode;

/**
 * Represents a literal integer constant.
 */
public class IntConstExpn extends ConstExpn {
    private IntConstNode intconst;

    public IntConstExpn(IntConstNode intconst) {
        // IntConstExpn is a thin wrapper around the underlying IntConstNode, so use as a `SourceLoc` directly
        super(intconst);

        this.intconst = intconst;
        intconst.setParent(this);
    }

    /** Returns a string representing the value of the literal. */
    public String toString () {
        return intconst.toString();
    }

    public Integer getValue() {
        return intconst.getInteger();
    }
}
