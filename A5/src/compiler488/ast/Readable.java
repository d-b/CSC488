package compiler488.ast;

public interface Readable extends SourceLoc, ASTPrettyPrintable {
    /*
     * Classes that extend this interface can be used
     * as arguments to GetStmt. Don't confuse this
     * concept with the printing of the
     * AST itself.
     */
}

