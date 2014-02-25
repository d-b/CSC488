package compiler488.ast.expn;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.SourceLoc;

/**
 * The common features of binary expressions.
 */
public class BinaryExpn extends Expn {
    Expn left, right;	/* Left and right operands of the binary operator. */
    String opSymbol;	/* Name of the operator. */

    public BinaryExpn(String opSymbol, Expn left, Expn right, SourceLoc loc) {
        super(loc);

        this.opSymbol = opSymbol;

        this.left = left;
        left.setParent(this);

        this.right = right;
        right.setParent(this);
    }

    /** Returns a string that represents the binary expression. */
    @Override
    public String toString () {
        return ("(" + left + ") " + opSymbol + " (" + right + ")");
    }

    public Expn getLeft() {
        return left;
    }

    public String getOpSymbol() {
        return opSymbol;
    }

    public Expn getRight() {
        return right;
    }

    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();

        children.add(left);
        children.add(right);

        return children;
    }
}

