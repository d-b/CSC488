package compiler488.ast;

public interface ASTPrettyPrintable {
    /**
     * Use the pretty printer context to print this node.
     */
    public void prettyPrint(ASTPrettyPrinterContext p);
}
