package compiler488.ast.expn;

import compiler488.ast.Printable;

/**
 * Represents a literal text constant.
 */
public class TextConstExpn extends ConstExpn implements Printable {
	private String value; // The value of this literal.

	/** Returns a description of the literal text constant. */
	@Override
	public String toString() {
		return "\"" + value + "\"";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
