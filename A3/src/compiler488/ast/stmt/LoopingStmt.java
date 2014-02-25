package compiler488.ast.stmt;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.ASTList;
import compiler488.ast.SourceLoc;
import compiler488.ast.expn.Expn;

/**
 * Represents the common parts of loops.
 */
public abstract class LoopingStmt extends Stmt {
    protected ASTList<Stmt> body;	  // body of the loop
    protected Expn condition;          // Loop condition

    public LoopingStmt(ASTList<Stmt> body, Expn condition, SourceLoc loc) {
    	super(loc);
    	
        this.body = body;
        body.setParent(this);
        
        this.condition = condition;
        condition.setParent(this);
    }

    public Expn getExpn() {
        return condition;
    }

    public ASTList<Stmt> getBody() {
        return body;
    }
    
    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();
        children.add(body);
        children.add(condition);
        return children;
    }
}

