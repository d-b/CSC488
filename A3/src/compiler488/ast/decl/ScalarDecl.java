package compiler488.ast.decl;

/**
 * Represents the declaration of a simple variable.
 */

public class ScalarDecl extends Declaration {

	/**
	 * Returns a string describing the name and type of the object being
	 * declared.
	 */
	@Override
	public String toString() {
		return   name + " : " + type ;
	}
}
