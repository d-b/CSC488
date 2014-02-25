package compiler488.ast;

public class IntConstNode extends AST {
	private Integer i;
	
	public IntConstNode(Integer i, SourceLoc loc) {
		super(loc);
		this.i = i;
	}
	
	public IntConstNode(String str, SourceLoc loc) {
		this(Integer.valueOf(str), loc);
	}
	
	public IntConstNode negate(SourceLoc loc) {
		return new IntConstNode(-i, loc);
	}
	
	public Integer getInteger() { 
		return i;
	}
	
	public String toString() {
		return i.toString();
	}
}
