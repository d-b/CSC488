package compiler488.ast;

import java.util.Collections;
import java.util.List;

import compiler488.ast.SourceLocNull;

/**
 * This is a placeholder at the top of the Abstract Syntax Tree hierarchy. It is
 * a convenient place to add common behaviour.
 * 
 * @author Dave Wortman, Marsha Chechik, Danny House
 * @author pdmccormick
 */
public class AST implements SourceLoc, ASTPrettyPrintable {
    protected AST parent;
    protected SourceLoc loc;

    public AST() {
        parent = null;
        loc = new SourceLocNull();
    }

    /**
     * Construct an AST node with the given source location coordinates. 
     * 
     * @param loc the source location 
     */
    public AST(SourceLoc loc) {
        this();

        this.loc = loc;
    }

    public AST getParent() {
        return parent;
    }

    public void setParent(AST parent) {
        this.parent = parent;
    }

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

    public void prettyPrint(ASTPrettyPrinterContext p) {
        p.print(toString());
    }
}
