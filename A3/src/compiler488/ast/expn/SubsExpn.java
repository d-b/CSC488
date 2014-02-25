package compiler488.ast.expn;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.SourceLoc;

/**
 * References to an array element variable
 *
 */
public class SubsExpn extends VarRefExpn {
    private String ident;
    private Expn subscript1;	 // first subscript
    private Expn subscript2 = null;	// second subscript (if any)

    public SubsExpn(String ident, Expn subscript1, Expn subscript2, SourceLoc loc) {
    	super(loc);
        this.ident = ident;
        this.subscript1 = subscript1;
        this.subscript2 = subscript2;
        subscript1.setParent(this);
        if(subscript2 != null) subscript2.setParent(this);
    }

    public SubsExpn(String ident, Expn subscript1, SourceLoc loc) {
        this(ident, subscript1, null, loc);
    }

    /** Returns a string that represents the array subscript. */
    @Override
    public String toString() {
        return (ident + "[" + subscript1 +
                ( subscript2 != null ? "," + subscript2 : "" )
                + "]");
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public Expn getSubscript1() {
        return subscript1;
    }

    public void setSubscript1(Expn subscript1) {
        this.subscript1 = subscript1;
    }

    public Expn getSubscript2() {
        return subscript2;
    }

    public void setSubscript2(Expn subscript2) {
        this.subscript2 = subscript2;
    }
    
    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();
        children.add(subscript1);
        if(subscript2 != null) children.add(subscript2);
        return children;
    }
}
