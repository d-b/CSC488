package compiler488.ast.expn;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.SourceLoc;

/**
 * The common features of unary expressions.
 */
public class UnaryExpn extends Expn {
    public final static String OP_NOT = "not";
    public final static String OP_MINUS = "-";

    private Expn operand;	/* operand of the unary  operator. */
    private String opSymbol;	/* Name of the operator. */

    public UnaryExpn(String opSymbol, Expn operand, SourceLoc loc) {
        super(loc);

        this.opSymbol = opSymbol;

        this.operand = operand;
        operand.setParent(this);
    }

    /** Returns a string that represents the unary expression. */
    @Override
    public String toString() {
        return opSymbol + "(" + operand + ")";
    }

    public Expn getOperand() {
        return operand;
    }

    public String getOpSymbol() {
        return opSymbol;
    }

    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();

        children.add(operand);

        return children;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof UnaryExpn)) {
            return false;
        }
        
        return equals((UnaryExpn) o);
    }
    
    public boolean equals(UnaryExpn o) {
        return (opSymbol == o.opSymbol) &&
                operand.equals(o.operand);
    }
}
