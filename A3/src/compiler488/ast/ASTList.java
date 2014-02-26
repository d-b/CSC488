package compiler488.ast;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * For nodes with an arbitrary number of children.
 */
public class ASTList<E extends SourceLoc & ASTPrettyPrintable> extends AST {
    /*
     * Keep the list here. We delegate rather than subclass LinkedList
     * because Java won't let us override the return type for addLast.
     */
    private LinkedList<E> ll;

    /**
     * Create an empty list.
     */
    public ASTList() {
        ll = new LinkedList<E>();
    }

    /**
     * Create a list with one element.
     */
    public ASTList(E ast) {
        ll = new LinkedList<E>();
        ll.addLast(ast);

        if (ast instanceof AST) {
            ((AST) ast).setParent(this);
        }
    }

    /**
     * The number of elements in the list.
     */
    public int size() {
        return ll.size();
    }

    /**
     * Append an element to the list, then return the list. This is a
     * pure-side-effect method, so it doesn't need to return anything.
     * However, we like the conciseness gained when such methods return the
     * target object.
     */
    public ASTList<E> addLast(E ast) {
        ll.addLast(ast);

        if (ast instanceof AST) {
            ((AST) ast).setParent(this);
        }

        return this;
    }

    public LinkedList<E> getList() {
        return ll;
    }

    public void prettyPrint(ASTPrettyPrinterContext p) {
        p.print(toString());
    }

    public void prettyPrintCommas(ASTPrettyPrinterContext p) {
        boolean first = true;

        for (ASTPrettyPrintable node : ll) {
            if (!first) {
                p.print(", ");
            }

            node.prettyPrint(p);

            first = false;
        }
    }

    public void prettyPrintBlock(ASTPrettyPrinterContext p) {
        p.enterBlock();
        prettyPrintNewlines(p);
        p.exitBlock();
    }

    public void prettyPrintNewlines(ASTPrettyPrinterContext p) {
        for (ASTPrettyPrintable node : ll) {
            node.prettyPrint(p);
            p.newline();
        }
    }

    /**
     * Return the contatenation of the strings obtained by sending
     * <b>toString</b> to each element.
     */
    @Override
    public String toString() {
        if (0 == ll.size()) {
            return "";
        } else {
            ListIterator<E> iterator = ll.listIterator();
            StringBuffer result = new StringBuffer(iterator.next().toString());

            while (iterator.hasNext()) {
                result.append(", " + iterator.next());
            }

            return result.toString();
        }
    }

    public E getFirst() {
        return ll.getFirst();
    }

    public boolean equals(Object o) {
        if (!(o instanceof ASTList)) {
            return false;
        }
        
        return equals((ASTList<?>) o);
    }
    
    public boolean equals(ASTList<?> o) {
        return ll.equals(o.ll);
    }

    @SuppressWarnings("unchecked")
    public List<AST> getChildren() {
        return (List<AST>) ((List<? extends Object>) getList());
    }

    public String getFilename() {
        return ll.getFirst().getFilename();
    }

    public int getStartLine() {
        return ll.getFirst().getStartLine();
    }

    public int getStartColumn() {
        return ll.getFirst().getStartColumn();
    }

    public int getEndLine() {
        return ll.getLast().getEndLine();
    }

    public int getEndColumn() {
        return ll.getLast().getEndColumn();
    }
}
