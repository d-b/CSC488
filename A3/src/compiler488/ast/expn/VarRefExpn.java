package compiler488.ast.expn;

import compiler488.ast.IdentNode;
import compiler488.ast.Readable;
import compiler488.ast.SourceLoc;

public abstract class VarRefExpn extends Expn implements Readable {
	protected IdentNode ident;
	
	public VarRefExpn(IdentNode ident, SourceLoc loc) {
		super(loc);
		
		this.ident = ident;
		ident.setParent(this);
	}
	
	public String getName() {
		return ident.getId();
	}

	public IdentNode getIdent() {
		return ident;
	}	
}
