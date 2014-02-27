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
 * Represents the declaration of a function or procedure, both as a 'forward' 
 * declaration and one with a body.
 */
public class RoutineDecl extends Declaration {
    /**
     * The formal parameters of the function/procedure and the
     * statements to execute when the procedure is called.
     */
    private ASTList<ScalarDecl> params;
    private Scope body = null;
    private LangType returnType;
    
    /** A language type corresponding to the function type signature */
    private FunctionType funcType;

    /**
     * Construct a node representing a forward function declaration (with no 
     * body but a return type.)
     */
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
    
    /**
     * Construct a node representing a forward procedure declaration (with no 
     * body nor return type.)
     */
    public RoutineDecl(IdentNode ident, ASTList<ScalarDecl> params, SourceLoc loc) {
        this(ident, null, params, loc);
    }
    
    /**
     * Construct a node representing a function declaration with both a 
     * return type and the body scope.
     */
    public RoutineDecl(IdentNode ident, TypeDecl returnType, ASTList<ScalarDecl> params, Scope body, SourceLoc loc) {
        this(ident, returnType, params, loc);

        this.body = body;
        body.setParent(this);
    }

    /**
     * Derive a new node instance by duplicating this node but including a 
     * body scope.
     */
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
     * return type or a procedure and its name.
     */
    @Override
    public String toString() {
        String s = (body == null) ? "forward " : "";
        s += isFunction() ? "func " : "proc ";
        s += ident;

        return s;
    }

    /**
     * Pretty print the node, including the body if one is present.
     */
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
