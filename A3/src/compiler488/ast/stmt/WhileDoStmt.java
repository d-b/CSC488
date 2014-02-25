package compiler488.ast.stmt;

import java.io.PrintStream;

import compiler488.ast.ASTList;
import compiler488.ast.ASTPrettyPrinterContext;
import compiler488.ast.SourceLoc;
import compiler488.ast.expn.Expn;

/**
 * Represents a loop in which the exit condition is evaluated before each pass.
 */
public class WhileDoStmt extends LoopingStmt {
    public WhileDoStmt(ASTList<Stmt> body, Expn condition, SourceLoc loc) {
        super(body, condition, loc);
    }

    public void prettyPrint(ASTPrettyPrinterContext p) {
        p.println("while " + condition + " do");
        body.prettyPrintBlock(p);
        p.println("end");
    }
}

