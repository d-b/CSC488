package compiler488.ast.decl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.ASTList;
import compiler488.ast.ASTPrettyPrinterContext;
import compiler488.ast.IdentNode;
import compiler488.ast.SourceLoc;
import compiler488.ast.stmt.Scope;
import compiler488.langtypes.FunctionType;
import compiler488.langtypes.LangType;

/**
 * Represents the declaration of a function or procedure.
 */
public class RoutineDecl extends Declaration {
    /*
     * The formal parameters of the function/procedure and the
     * statements to execute when the procedure is called.
     */
    private ASTList<ScalarDecl> params; // The formal parameters of the routine.
    private Scope body = null;
    private LangType returnType;
    private FunctionType funcType;

    public RoutineDecl(IdentNode ident, TypeDecl returnTypeDecl, ASTList<ScalarDecl> params, SourceLoc loc) {
        super(ident, returnTypeDecl, loc);
        
        this.params = params;
        params.setParent(this);
        
        returnType = (returnTypeDecl == null) ? LangType.TYPE_NIL : returnTypeDecl.getLangType();

        List<LangType> argTypes = new ArrayList<LangType>();
        for (ScalarDecl argDecl : params.getList()) {
            argTypes.add(argDecl.getTypeDecl().getLangType());
        }

        funcType = new FunctionType(returnType, argTypes);
    }
    
    public RoutineDecl(IdentNode ident, ASTList<ScalarDecl> params, SourceLoc loc) {
        this(ident, null, params, loc);
    }
    
    public RoutineDecl(IdentNode ident, TypeDecl returnType, ASTList<ScalarDecl> params, Scope body, SourceLoc loc) {
        this(ident, returnType, params, loc);

        this.body = body;
        body.setParent(this);
    }

    public RoutineDecl withBody(Scope body, SourceLoc wider_loc) {
        return new RoutineDecl(ident, typeDecl, params, body, wider_loc);
    }

    public boolean isFunction() {
        return !returnType.isNil();
    }

    public boolean isForward() {
        return body == null;
    }

    public Scope getBody() {
        return body;
    }
    
    public LangType getReturnType() {
        return returnType;
    }

    public FunctionType getFunctionType() {
        return funcType;
    }

    public ASTList<ScalarDecl> getParameters() {
        return params;
    }

    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();

        children.add(typeDecl);
        children.add(params);

        if (body != null) {
            children.add(body);
        }

        return children;
    }

    /**
     * Returns a string indicating that this is a function with
     * return type or a procedure, name, Type parameters, if any,
     * are listed later by routineBody
     */
    @Override
    public String toString() {
        String s = (body == null) ? "forward " : "";
        s += isFunction() ? "func " : "proc ";
        s += ident;

        return s;
    }

    public void prettyPrint(ASTPrettyPrinterContext p) {
        p.print(toString());
        p.print("(");
        params.prettyPrintCommas(p);
        p.print(")");
        
        if (isFunction()) {
            p.print(" : ");
            typeDecl.prettyPrint(p);
        }

        if (body != null) {
            p.print(" ");
            body.prettyPrint(p);
        }
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof RoutineDecl)) {
            return false;
        }
        
        return equals((RoutineDecl) o);
    }
    
    public boolean equals(RoutineDecl o) {
        return ident.equals(o.ident) &&
                typeDecl.equals(o.typeDecl) && 
                params.equals(o.params) && 
                ((body == null) ? (o.body == null) : body.equals(o.body));
    }
}
