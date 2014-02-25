package compiler488.ast.stmt;

import compiler488.ast.SourceLoc;

/**
 * Placeholder for the scope that is the entire program
 */
public class Program extends Scope {
    public Program(Scope scope, SourceLoc loc) {
        super(scope.getDeclarations(), scope.getStatements(), loc);
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof Scope)) {
            return false;
        }
        
        // The above check ensures the correct type, but after that Scope can do the rest 
        return equals((Scope) o);
    }
}
