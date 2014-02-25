package compiler488.ast;

public class IdentNode extends AST {
    private String id;

    public IdentNode(String id, SourceLoc loc) {
        super(loc);

        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return id;
    }
}

