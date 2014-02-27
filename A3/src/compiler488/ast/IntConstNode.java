package compiler488.ast;

/**
 * An AST node that represents a positive or negative integer literal along 
 * with its source location from the original file. 
 */
public class IntConstNode extends AST {
    private Integer i;

    public IntConstNode(Integer i, SourceLoc loc) {
        super(loc);

        this.i = i;
    }

    public IntConstNode(String str, SourceLoc loc) {
        this(Integer.valueOf(str), loc);
    }

    /**
     * Return a newly constructed node representing the literals' negated value.
     */
    public IntConstNode negate(SourceLoc loc) {
        return new IntConstNode(-i, loc);
    }

    public Integer getInteger() {
        return i;
    }

    public String toString() {
        return i.toString();
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof IntConstNode)) {
            return false;
        }
        
        return equals((IntConstNode) o);
    }
    
    public boolean equals(IntConstNode o) {
        return i.equals(o.i);
    }
}
