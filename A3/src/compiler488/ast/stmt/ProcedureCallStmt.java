package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.expn.Expn;

/**
 * Represents calling a procedure.
 */
public class ProcedureCallStmt extends Stmt {
	private String name; // The name of the procedure being called.

	private ASTList<Expn> arguments; // The arguments passed to the procedure.

	/** Returns a string describing the procedure call. */
	@Override
	public String toString() {
		if (arguments!=null)
			return "Procedure call: " + name + " (" + arguments + ")";
		else
			return "Procedure call: " + name + " ( ) ";
	}

	public ASTList<Expn> getArguments() {
		return arguments;
	}

	public void setArguments(ASTList<Expn> args) {
		this.arguments = args;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
