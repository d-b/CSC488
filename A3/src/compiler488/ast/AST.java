package compiler488.ast;

import java.util.Collections;
import java.util.List;

/**
 * This is a placeholder at the top of the Abstract Syntax Tree hierarchy. It is
 * a convenient place to add common behaviour.
 * @author  Dave Wortman, Marsha Chechik, Danny House
 */
public class AST {
    AST parent;
    public AST(){parent = null;}
    public AST  getParent() {return parent;}
    public void setParent(AST parent) {this.parent = parent;}
    
    public List<AST> getChildren() {
        return Collections.emptyList();
    }
}
