package compiler488.ast.stmt;

import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.ASTList;
import compiler488.ast.ASTPrettyPrinterContext;
import compiler488.ast.SourceLoc;
import compiler488.ast.decl.Declaration;

/**
 * Represents the declarations and instructions of a scope construct.
 */
public class Scope extends Stmt {
    private ASTList<Declaration> declarations; // The declarations at the top.

    private ASTList<Stmt> statements; // The statements to execute.

    public Scope(ASTList<Declaration> decls, ASTList<Stmt> stmts, SourceLoc loc) {
        super(loc);

        if (decls == null) {
            declarations = new ASTList<Declaration>();
        } else {
            declarations = decls;
        }

        if (stmts == null) {
            statements = new ASTList<Stmt>();
        } else {
            statements = stmts;
        }

        declarations.setParent(this);
        statements.setParent(this);
    }
    
    public Scope(SourceLoc loc) {
        this(null, null, loc);
    }

    public void prettyPrint(ASTPrettyPrinterContext p) {
        p.println("{");
        p.enterBlock();
        declarations.prettyPrintNewlines(p);
        statements.prettyPrintNewlines(p);
        p.exitBlock();
        p.print("}");
    }

    public ASTList<Declaration> getDeclarations() {
        return declarations;
    }

    public ASTList<Stmt> getStatements() {
        return statements;
    }

    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();

        children.add(declarations);
        children.add(statements);

        return children;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof Scope)) {
            return false;
        }
        
        return equals((Scope) o);
    }
    
    public boolean equals(Scope o) {
        return ((declarations == null) ? (o.declarations == null) : declarations.equals(o.declarations)) &&
                ((statements == null) ? (o.statements == null) : statements.equals(o.statements));
    }
}

