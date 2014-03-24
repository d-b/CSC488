package compiler488.ast.decl;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.IntConstNode;
import compiler488.ast.SourceLoc;

/**
 * An AST node representing the specified bound of an array declaration, with a
 * possibly implicit lower bound of 1.
 * 
 * Example, from which two ArrayBound nodes will be generated:
 * 
 *     var x[1 .. 2, 4] : integer
 *           ^^^^^^  ^
 *           
 * NB: The source location interval corresponds to the start of the first bound literal
 * to the right end of the upper bound, including all whitespace and the two dots.
 */
public class ArrayBound extends AST {
    /** Whether the 1 lower bound was omitted and thus is implicit */
    private boolean implicit_lb;
    
    /** The AST node for the literal lower bound constant (if any) */
    private IntConstNode lb;
    
    /** The AST node for the literal upper bound constant */
    private IntConstNode ub;

    /**
     * Construct this node with an explicit upper bound specified (and 
     * implicit lower bound.)
     */
    public ArrayBound(IntConstNode ub, SourceLoc loc) {
        super(loc);

        implicit_lb = true;
        this.ub = ub;
        ub.setParent(this);
    }

    /**
     * Construct this node from both an explicit upper and lower bound.
     */
    public ArrayBound(IntConstNode lb, IntConstNode ub, SourceLoc loc) {
        this(ub, loc);

        implicit_lb = false;
        this.lb = lb;
        lb.setParent(this);
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

    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();
        children.add(lb);
        children.add(ub);
        return children;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof ArrayBound)) {
            return false;
        }
        
        return equals((ArrayBound) o);
    }
    
    public boolean equals(ArrayBound o) {
        return getLowerboundValue() == o.getLowerboundValue() &&
                getUpperboundValue() == o.getUpperboundValue();
    }
}
