package compiler488.ast.expn;

import compiler488.ast.AST;
import compiler488.ast.Printable;
import compiler488.ast.SourceLoc;
import compiler488.langtypes.LangType;

/**
 * The common parent of all nodes representing expressions.
 */
public class Expn extends AST implements Printable {
    private LangType evalType;
    
    public Expn(SourceLoc loc) {
        super(loc);
        evalType = null;
    }
    
    public LangType getEvalType() {
        return evalType;
    }
    
    public void setEvalType(LangType evalType) {
        this.evalType = evalType;
    }
}

