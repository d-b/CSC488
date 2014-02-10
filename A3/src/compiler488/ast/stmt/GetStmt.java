package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.Readable;

/**
 * The command to read data into one or more variables.
 */
public class GetStmt extends Stmt {
	
	private ASTList<Readable> inputs; // A list of locations to put the values read.

	public GetStmt () {
		inputs = new ASTList<Readable> ();
	}
	
	/** Returns a string describing the <b>get</b> statement. */
	@Override
	public String toString() {
		return "get " + inputs;
	}

	public ASTList<Readable> getInputs() {
		return inputs;
	}

	public void setInputs(ASTList<Readable> inputs) {
		this.inputs = inputs;
	}
}
