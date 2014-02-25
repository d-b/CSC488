package compiler488.ast;

import java.util.Collections;
import java.util.List;

import compiler488.ast.SourceLocNull;
import compiler488.ast.type.Type;

/**
 * This is a placeholder at the top of the Abstract Syntax Tree hierarchy. It is
 * a convenient place to add common behaviour.
 * @author  Dave Wortman, Marsha Chechik, Danny House
 */
public class AST implements SourceLoc {
    Type type;
    AST  parent;
    
    public AST(){
        parent = null;
        type   = Type.TYPE_NIL;
        loc    = new SourceLocNull();
    }
    
    protected SourceLoc loc;
    public AST(SourceLoc loc) {
        this();
        this.loc = loc;
    }
    
    public Type getType()             {return type;         }
    public void setType(Type type)    {this.type = type;    }
    public AST  getParent()           {return parent;       }
    public void setParent(AST parent) {this.parent = parent;}
    
    public List<AST> getChildren() {
        return Collections.emptyList();
    }
    
    public SourceLoc getLoc() {
        return loc;
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
