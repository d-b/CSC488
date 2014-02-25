package compiler488.ast.decl;

import compiler488.ast.AST;
import compiler488.ast.IntConstNode;
import compiler488.ast.SourceLoc;

public class ArrayBound extends AST {
	private boolean implicit_lb;
	private IntConstNode lb;
	private IntConstNode ub;
	
	public ArrayBound(IntConstNode ub, SourceLoc loc) {
		super(loc);

		implicit_lb = true;
		this.ub = ub;
	}
	
	public ArrayBound(IntConstNode lb, IntConstNode ub, SourceLoc loc) {
		this(ub, loc);

		implicit_lb = false;
		this.lb = lb;
	}
	
	public Integer getLowerboundValue() {
		if (implicit_lb) {
			return 1;
		} else {
			return lb.getInteger();
		}
	}
	
	public IntConstNode getLowerbound() {
		return lb;
	}
	
	public Integer getUpperboundValue() {
		return ub.getInteger();
	}
	
	public IntConstNode getUpperbound() {
		return ub;
	}
	
	public String toString() {
		if (implicit_lb) {
			return ub.toString();
		} else {
			return lb + ".." + ub;
		}
	}
	
	public boolean isValid() {
		
		if (implicit_lb) {
			return ub.getInteger() >= 1;
		} else {
			return lb.getInteger() <= ub.getInteger();
		}
	}
}
