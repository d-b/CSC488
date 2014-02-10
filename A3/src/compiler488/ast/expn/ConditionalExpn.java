package compiler488.ast.expn;


/** Represents a conditional expression (i.e., x>0?3:4). */
public class ConditionalExpn extends Expn {
	private Expn condition; // Evaluate this to decide which value to yield.

	private Expn trueValue; // The value is this when the condition is true.

	private Expn falseValue; // Otherwise, the value is this.

	/** Returns a string that describes the conditional expression. */
	@Override
	public String toString() {
		return "(" + condition + " ? " + trueValue + " : " + falseValue + ")";
	}

	public Expn getCondition() {
		return condition;
	}

	public void setCondition(Expn condition) {
		this.condition = condition;
	}

	public Expn getFalseValue() {
		return falseValue;
	}

	public void setFalseValue(Expn falseValue) {
		this.falseValue = falseValue;
	}

	public Expn getTrueValue() {
		return trueValue;
	}

	public void setTrueValue(Expn trueValue) {
		this.trueValue = trueValue;
	}
}
