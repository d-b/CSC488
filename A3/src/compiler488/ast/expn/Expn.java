package compiler488.ast.expn;

import compiler488.ast.AST;
import compiler488.ast.Printable;
import compiler488.ast.SourceLoc;

/**
 * A placeholder for all expressions.
 */
public class Expn extends AST implements Printable {
	public Expn(SourceLoc loc) {
		this.loc = loc;
	}
}
