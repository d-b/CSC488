package compiler488.ast.stmt;

import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.ASTList;
import compiler488.ast.Indentable;
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

    /**
     * Print a description of the <b>if-then-else</b> construct. If the
     * <b>else</b> part is empty, just print an <b>if-then</b> construct.
     *
     * @param out
     *            Where to print the description.
     * @param depth
     *            How much indentation to use while printing.
     */
    @Override
    public void printOn(PrintStream out, int depth) {
        Indentable.printIndentOnLn(out, depth, "if " + condition + " then ");
        //whenTrue.printOn(out, 	depth + 1);

        if (whenFalse != null) {
            Indentable.printIndentOnLn(out, depth, "else");
            //whenFalse.printOn(out, depth + 1);
        }

        Indentable.printIndentOnLn(out, depth, "fi");
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
        if(whenFalse != null) children.add(whenFalse);
        return children;
    }    
}

