package compiler488.ast.stmt;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.ASTList;
import compiler488.ast.ASTPrettyPrinterContext;
import compiler488.ast.SourceLoc;
import compiler488.ast.expn.Expn;

/**
 * Represents an if-then or an if-then-else construct.
 */
public class IfStmt extends Stmt {
    // The condition that determines which branch to execute.
    private Expn condition;

    // Represents the statement to execute when the condition is true.
    private ASTList<Stmt> whenTrue;

    // Represents the statement to execute when the condition is false.
    private ASTList<Stmt> whenFalse = null;

    public IfStmt(Expn condition, ASTList<Stmt> whenTrue, SourceLoc loc) {
        super(loc);

        this.condition = condition;
        condition.setParent(this);

        this.whenTrue = whenTrue;
        whenTrue.setParent(this);
    }

    public IfStmt(Expn condition, ASTList<Stmt> whenTrue, ASTList<Stmt> whenFalse, SourceLoc loc) {
        this(condition, whenTrue, loc);

        this.whenFalse = whenFalse;
        whenFalse.setParent(this);
    }

    public void prettyPrint(ASTPrettyPrinterContext p) {
        p.println("if " + condition + " then ");
        whenTrue.prettyPrintBlock(p);

        if (whenFalse != null) {
            p.println("else");
            whenFalse.prettyPrintBlock(p);
        }

        p.println("fi");
    }

    public Expn getCondition() {
        return condition;
    }

    public ASTList<Stmt> getWhenFalse() {
        return whenFalse;
    }

    public ASTList<Stmt> getWhenTrue() {
        return whenTrue;
    }

    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();

        children.add(condition);
        children.add(whenTrue);

        if (whenFalse != null) {
            children.add(whenFalse);
        }

        return children;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof IfStmt)) {
            return false;
        }
        
        return equals((IfStmt) o);
    }
    
    public boolean equals(IfStmt o) {
        return condition.equals(o.condition) &&
                whenTrue.equals(o.whenTrue) &&
                ((whenFalse == null) ? (o.whenFalse == null) : whenFalse.equals(o.whenFalse));
    }
}

