package compiler488.ast.stmt;

import compiler488.ast.expn.Expn;

/**
 * Holds the assignment of an expression to a variable.
 */
public class AssignStmt extends Stmt {
	/*
	 * lval is the location being assigned to, and rval is the value being
	 * assigned.
	 */
	private Expn lval, rval;

	/** Returns a string that describes the assignment statement. */
	@Override
	public String toString() {
		return "Assignment: " + lval + " := " + rval;
	}

	public Expn getLval() {
		return lval;
	}

	public void setLval(Expn lval) {
		this.lval = lval;
	}

	public Expn getRval() {
		return rval;
	}

	public void setRval(Expn rval) {
		this.rval = rval;
	}
}
