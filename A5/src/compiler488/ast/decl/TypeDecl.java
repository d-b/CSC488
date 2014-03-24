package compiler488.ast.decl;

import compiler488.ast.AST;
import compiler488.ast.SourceLoc;
import compiler488.langtypes.LangType;

/**
 * The AST node corresponding to the type name keywords. Note that the 
 * constructor is private, so you can only generate a node for a given source location from the make*() factory methods.
 * 
 * Example:
 *     forward func foo(x : integer, y : boolean) : integer
 *                          ^^^^^^^      ^^^^^^^    ^^^^^^^
 */
public class TypeDecl extends AST {
    private static final String DECL_INTEGER = "integer";
    private static final String DECL_BOOLEAN = "boolean";
    
    private String typeName;

    private TypeDecl(String typeName, SourceLoc loc) {
        super(loc);
        this.typeName = typeName;
    }

    /**
     * Construct a "integer" keyword type declaration for the given source 
     * location.
     */
    public static TypeDecl makeInteger(SourceLoc loc) {
        return new TypeDecl(DECL_INTEGER, loc);
    }

    /**
     * Construct a "boolean" keyword type declaration for the given source
     * location.
     */
    public static TypeDecl makeBoolean(SourceLoc loc) {
        return new TypeDecl(DECL_BOOLEAN, loc);
    }
    
    public String toString() {
        return typeName;
    }
    
    public String getName() {
        return typeName;
    }
    
    public LangType getLangType() {
        if (typeName == DECL_INTEGER) {
            return LangType.TYPE_INTEGER;
        } else if (typeName == DECL_BOOLEAN) {
            return LangType.TYPE_BOOLEAN;
        } else {
            return null;
        }
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof TypeDecl)) {
            return false;
        }
        
        return equals((TypeDecl) o);
    }
    
    public boolean equals(TypeDecl o) {
        return typeName == o.typeName;
    }
}
