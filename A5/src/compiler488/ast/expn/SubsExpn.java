package compiler488.ast.expn;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.IdentNode;
import compiler488.ast.SourceLoc;

/**
 * An expression reference to an array element variable including subscripts for 
 * 1 or 2 dimensions (which can be used as an l-value)
 */
public class SubsExpn extends VarRefExpn {
    private Expn subscript1;	 // first subscript
    private Expn subscript2 = null;	// second subscript (if any)

    public SubsExpn(IdentNode ident, Expn subscript1, SourceLoc loc) {
        super(ident, loc);

        this.subscript1 = subscript1;
        subscript1.setParent(this);
    }

    public SubsExpn(IdentNode ident, Expn subscript1, Expn subscript2, SourceLoc loc) {
        this(ident, subscript1, loc);

        this.subscript2 = subscript2;
        subscript2.setParent(this);
    }

    /** Returns a string that represents the array subscript. */
    @Override
    public String toString() {
        return ident + "[" + subscript1 +
               ( subscript2 != null ? "," + subscript2 : "" )
               + "]";
    }

    public Expn getSubscript1() {
        return subscript1;
    }

    public Expn getSubscript2() {
        return subscript2;
    }

    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();

        children.add(subscript1);

        if (subscript2 != null) {
            children.add(subscript2);
        }

        return children;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof SubsExpn)) {
            return false;
        }
        
        return equals((SubsExpn) o);
    }
    
    public boolean equals(SubsExpn o) {
        return ident.equals(o.ident) &&
                subscript1.equals(o.subscript1) &&
                ((subscript2 == null) ? (o.subscript2 == null) : subscript2.equals(o.subscript2));
    }
}

