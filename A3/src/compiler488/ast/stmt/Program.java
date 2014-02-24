package compiler488.ast.stmt;

import compiler488.ast.SourceLoc;

/**
 * Placeholder for the scope that is the entire program
 */
public class Program extends Scope {
    public Program(Scope scope, SourceLoc loc) {
        super(scope.getDeclarations(), scope.getStatements(), loc);
    }
}

