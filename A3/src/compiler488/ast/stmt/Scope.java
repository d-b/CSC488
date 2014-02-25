package compiler488.ast.stmt;

import java.io.PrintStream;
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

    public Scope(SourceLoc loc) {
        super(loc);

        declarations = new ASTList<Declaration>();
        statements = new ASTList<Stmt>();
        declarations.setParent(this);
        statements.setParent(this);
    }

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
}

