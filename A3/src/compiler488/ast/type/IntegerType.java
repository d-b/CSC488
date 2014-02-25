package compiler488.ast.type;

import compiler488.ast.SourceLoc;

/**
 * Used to declare objects that yield integers.
 */
public class IntegerType extends Type {
    public IntegerType() {super();}
	public IntegerType(SourceLoc loc) {super(loc);}
	
    @Override
    public String toString() {
        return "integer";
    }
}
