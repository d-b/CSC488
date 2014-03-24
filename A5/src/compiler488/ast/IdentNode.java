package compiler488.ast;

/**
 * An AST node that represents an identifier literal with its source location 
 * from the original file. 
 */
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
    
    public boolean equals(Object o) {
        if (!(o instanceof IdentNode)) {
            return false;
        }
        
        return equals((IdentNode) o);
    }
    
    public boolean equals(IdentNode o) {        
        return id.equals(o.id);
    }
}
