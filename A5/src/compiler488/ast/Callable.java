package compiler488.ast;

import compiler488.ast.expn.Expn;

public interface Callable {
    public ASTList<Expn> getArguments();
    public IdentNode getIdent();
}
