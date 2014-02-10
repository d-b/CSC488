package compiler488.ast.expn;

import compiler488.ast.Readable;

/**
 *  References to a scalar variable.
 */
public class IdentExpn extends Expn implements Readable
    {
    private String ident;  	// name of the identifier

    /**
     * Returns the name of the variable or function.
     */
    @Override
	public String toString () { return ident; }

	public String getIdent() {
		return ident;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}
}
