package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.expn.Expn;

/**
 * Represents the common parts of loops.
 */
public abstract class LoopingStmt extends Stmt {
    protected ASTList<Stmt> body;	  // body of the loop
    protected Expn condition;          // Loop condition

    public LoopingStmt(ASTList<Stmt> body, Expn condition) {
        this.body = body;
        this.condition = condition;
        body.setParent(this);
        condition.setParent(this);
    }

    public Expn getExpn() {
        return condition;
    }

    public void setExpn(Expn expn) {
        this.condition = expn;
    }

    public ASTList<Stmt> getBody() {
        return body;
    }

    public void setBody(ASTList<Stmt> body) {
        this.body = body;
    }
}

