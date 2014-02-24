package compiler488.ast.expn;

import compiler488.ast.Readable;
import compiler488.ast.SourceLoc;

public abstract class VarRefExpn extends Expn implements Readable {
	public VarRefExpn(SourceLoc loc) {
		super(loc);
	}
}

