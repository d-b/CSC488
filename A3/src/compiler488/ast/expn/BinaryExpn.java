package compiler488.ast.expn;


/**
 * The common features of binary expressions.
 */
public class BinaryExpn extends Expn
    {
    Expn left, right;	/* Left and right operands of the binary operator. */
    String opSymbol;	/* Name of the operator. */

    /** Returns a string that represents the binary expression. */
    @Override
	public String toString ()
	{
	return ("(" + left + ")" + opSymbol + "(" + right + ")");
    }

	public Expn getLeft() {
		return left;
	}

	public void setLeft(Expn left) {
		this.left = left;
	}

	public String getOpSymbol() {
		return opSymbol;
	}

	public void setOpSymbol(String opSymbol) {
		this.opSymbol = opSymbol;
	}

	public Expn getRight() {
		return right;
	}

	public void setRight(Expn right) {
		this.right = right;
	}
}
