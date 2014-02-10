package compiler488.ast.decl;

import java.io.PrintStream;

import compiler488.ast.Indentable;

/**
 * Represents the declaration of a function or procedure.
 */
public class RoutineDecl extends Declaration {
	/*
	 * The formal parameters of the function/procedure and the
	 * statements to execute when the procedure is called.
	 */
	private RoutineBody routineBody;

	/**
	 * Returns a string indicating that this is a function with
	 * return type or a procedure, name, Type parameters, if any,
	 * are listed later by routineBody
	 */
	@Override
	public String toString() {
	  if(type==null)
	    {
	      return " proc " + name;
	    }
	  else
	    {
	      return " func "  + name  + " : " + type ;
	    }
	}

	/**
	 * Prints a description of the function/procedure.
	 * 
	 * @param out
	 *            Where to print the description.
	 * @param depth
	 *            How much indentation to use while printing.
	 */
	@Override
	public void printOn(PrintStream out, int depth) {
		Indentable.printIndentOn(out, depth, this + " ");
		routineBody.printOn(out, depth);
	}

	public RoutineBody getRoutineBody() {
		return routineBody;
	}

	public void setRoutineBody(RoutineBody routineBody) {
		this.routineBody = routineBody;
	}
}
