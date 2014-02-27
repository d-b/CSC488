package compiler488.ast.decl;

import compiler488.ast.IdentNode;
import compiler488.ast.SourceLoc;

/**
 * Holds the declaration part of an array for a MultiDeclaration consisting 
 * of a variable identifier and 1 or 2 dimensions of array bounds.
 * 
 * Example:
 *     var foo, y[1, 2], z [1..5], bar : integer
 *              ^^^^^^^  ^^^^^^^^ 
 */
public class ArrayDeclPart extends DeclarationPart {
    /** The first dimension boundary of the array */
    private ArrayBound b1;
    
    /** The second dimension boundary of the array (iff is2D is true) */ 
    private ArrayBound b2;
    private Boolean is2D = false;

    /** The number of objects the array holds. */
    private Integer size;

    /**
     * Construct a node for a 1D array. 
     */
    public ArrayDeclPart(IdentNode ident, ArrayBound b, SourceLoc loc) {
        super(ident, loc);

        b1 = b;
        is2D = false;
        size = b1.getUpperboundValue() - b1.getLowerboundValue() + 1;
    }

    /**
     * Construct a node for a 2D array. 
     */
    public ArrayDeclPart(IdentNode ident, ArrayBound b1, ArrayBound b2, SourceLoc loc) {
        this(ident, b1, loc);

        this.b2 = b2;
        is2D = true;
        size *= (b2.getUpperboundValue() - b2.getLowerboundValue() + 1);
    }

    /**
     * Returns a string that describes the array.
     */
    @Override
    public String toString() {
        return ident + "[" + b1 + (is2D ? (", " + b2) : "") + "]";
    }

    public Integer getDimensions() {
        return is2D ? 2 : 1;
    }

    public ArrayBound getBound1() {
        return b1;
    }

    public ArrayBound getBound2() {
        assert is2D;
        return b2;
    }

    public Integer getSize() {
        return size;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof ArrayDeclPart)) {
            return false;
        }
        
        return equals((ArrayDeclPart) o);
    }
    
    public boolean equals(ArrayDeclPart o) {
        boolean b1_equals = b1.equals(o.b1);
        
        if (is2D) {
            return o.is2D &&
                    b1_equals &&
                    b2.equals(o.b2);
        } else {
            return !o.is2D && b1_equals;
        }
    }
}
