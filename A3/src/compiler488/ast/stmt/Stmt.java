package compiler488.ast.stmt;

import compiler488.ast.AST;
import compiler488.ast.SourceLoc;
import compiler488.ast.decl.RoutineDecl;

/**
 * A placeholder for statements.
 */
public class Stmt extends AST {
    private RoutineDecl routine;
    
    public Stmt(SourceLoc loc) {
        super(loc);
        routine = null;
    }
    
    public void setRoutine(RoutineDecl routine) {
        this.routine = routine;
    }
    
    public RoutineDecl getRoutine() {
        return routine;
    }
}
