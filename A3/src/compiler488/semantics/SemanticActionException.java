package compiler488.semantics;

public class SemanticActionException extends RuntimeException {
	private int actionNumber;

	public SemanticActionException(int actionNumber) {
		this.actionNumber = actionNumber;
	}

	public int getActionNumber() {
		return actionNumber;
	}
}
