package compiler488.ast.expn;

import compiler488.ast.IdentNode;

/**
 *  References to a scalar variable.
 */
public class IdentExpn extends VarRefExpn {
    public IdentExpn(IdentNode ident) {
    	super(ident, ident);
    }
}
