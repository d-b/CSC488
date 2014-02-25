package compiler488.ast.stmt;

import java.io.PrintStream;

import compiler488.ast.ASTList;
import compiler488.ast.ASTPrettyPrinterContext;
import compiler488.ast.SourceLoc;
import compiler488.ast.expn.Expn;

/**
 * Represents a loop in which the exit condition is evaluated after each pass.
 */
public class RepeatUntilStmt extends LoopingStmt {
    public RepeatUntilStmt(ASTList<Stmt> body, Expn condition, SourceLoc loc) {
        super(body, condition, loc);
    }

    public void prettyPrint(ASTPrettyPrinterContext p) {
        p.println("repeat");
        body.prettyPrintBlock(p);
        p.println("until " + condition);
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof RepeatUntilStmt)) {
            return false;
        }
        
        // The above check ensures the correct type, but after that LoopingStmt can do the rest 
        return equals((LoopingStmt) o);
    }   
}
