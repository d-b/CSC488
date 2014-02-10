package compiler488.ast.decl;

import compiler488.ast.AST;

/**
 * The common features of declarations' parts.
 */
public class DeclarationPart extends AST {

	/** The name of the thing being declared. */
	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
