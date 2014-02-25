package compiler488.ast.stmt;

import java.io.PrintStream;

import compiler488.ast.SourceLoc;

/**
 * The command to return from a procedure.
 */
public class ReturnStmt extends Stmt {
    public ReturnStmt(SourceLoc loc) {
        super(loc);
    }

    public String toString() {
        return "return";
    }
    
    public boolean equals(Object o) {
        return o instanceof ReturnStmt;
    }
}

