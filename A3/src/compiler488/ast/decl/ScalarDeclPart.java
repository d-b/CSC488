package compiler488.ast.decl;

import compiler488.ast.IdentNode;
import compiler488.ast.SourceLoc;

/**
 * Represents the declaration of a simple variable.
 */
public class ScalarDeclPart extends DeclarationPart {
    public ScalarDeclPart(IdentNode ident) {
    	// NB: Again, we are taking advantage of the fact that IdentNode implements SourceLoc (via AST) as well!
        super(ident, ident);
    }
}

