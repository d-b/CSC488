package compiler488.ast.type;

public class NilType extends Type {
	public NilType() {
		// NB: This has no concrete SourceLoc, oops!
		super(null);
	}
}
