package compiler488.ast.decl;

import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

import compiler488.ast.AST;
import compiler488.ast.ASTList;
import compiler488.ast.SourceLoc;
import compiler488.ast.type.Type;

/**
 * Holds the declaration of multiple elements.
 */
public class MultiDeclarations extends Declaration {
    /* The elements being declared */
    private ASTList<DeclarationPart> elements;

    public MultiDeclarations(ASTList<DeclarationPart> elements, Type type, SourceLoc loc) {
        super(null, type, loc);

        this.elements = elements;
        elements.setParent(this);
    }

    public List<AST> getChildren() {
        Vector<AST> children = new Vector<AST>();
        children.add(elements);
        return children;
    }

    /**
     * Returns a string that describes the array.
     */
    @Override
    public String toString() {
        return  "var " + elements + " : " + type ;
    }

    public ASTList<DeclarationPart> getElements() {
        return elements;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof MultiDeclarations)) {
            return false;
        }
        
        return equals((MultiDeclarations) o);
    }
    
    public boolean equals(MultiDeclarations o) {
        return type.equals(o.type) &&
                elements.equals(o.elements);
    }
}
