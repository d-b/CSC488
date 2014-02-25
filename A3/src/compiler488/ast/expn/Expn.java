package compiler488.ast.expn;

import compiler488.ast.AST;
import compiler488.ast.Printable;
import compiler488.ast.SourceLoc;
import compiler488.langtypes.LangType;

/**
 * A placeholder for all expressions.
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

