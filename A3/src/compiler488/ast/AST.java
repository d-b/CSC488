package compiler488.ast;

import java.util.Collections;
import java.util.List;

/**
 * This is a placeholder at the top of the Abstract Syntax Tree hierarchy. It is
 * a convenient place to add common behaviour.
 * @author  Dave Wortman, Marsha Chechik, Danny House
 */
public class AST implements SourceLoc {
    AST parent;
    protected SourceLoc loc;

    public AST(){parent = null;}
    
    public AST(SourceLoc loc) {
    	this.loc = loc;
    }
    
    public AST  getParent() {return parent;}
    public void setParent(AST parent) {this.parent = parent;}

    public SourceLoc getLoc() { return loc; }
    
    public List<AST> getChildren() {
        return Collections.emptyList();
    }

	public String getFilename() {
		return loc.getFilename();
	}

	public int getStartLine() {
		return loc.getStartLine();
	}

	public int getStartColumn() {
		return loc.getStartColumn();
	}

	public int getEndLine() {
		return loc.getEndLine();
	}

	public int getEndColumn() {
		return loc.getEndColumn();
	}
}
