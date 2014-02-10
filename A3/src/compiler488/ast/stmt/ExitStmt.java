package compiler488.ast.stmt;

import compiler488.ast.expn.*;

/**
 * Represents the command to exit from a loop.
 */

public class ExitStmt extends Stmt {

	// condition for 'exit when'
        private Expn expn = null;

	/** Returns the string <b>"exit"</b> or <b>"exit when e"</b>" 
	*/
	@Override
	public String toString() {
		  {
		    String stmt = "exit " ;
                    if( expn != null )
		        stmt = stmt + "when " + expn + " " ;
		    return stmt ;
		  }
	}

	public Expn getExpn() {
		return expn;
	}

	public void setExpn(Expn expn) {
		this.expn = expn;
	}

}
