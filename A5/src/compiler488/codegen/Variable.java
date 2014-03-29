package compiler488.codegen;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import compiler488.ast.AST;
import compiler488.ast.decl.ArrayBound;
import compiler488.ast.decl.ArrayDeclPart;
import compiler488.ast.decl.MultiDeclarations;
import compiler488.ast.decl.ScalarDecl;
import compiler488.ast.decl.ScalarDeclPart;
import compiler488.langtypes.LangType;

/**
 * Variable reference
 *
 * @author Daniel Bloemendal
 */
public class Variable {
    // A copy of an old variable with a new level and offset
    public Variable(Variable variable, short level, short offset) {
        this.name = variable.name;
        this.bounds = variable.bounds;
        this.type = variable.type;
        this.level = level;
        this.offset = offset;
    }

    // Instantiate a new variable reference from a single declaration
    public Variable(AST node, short level, short offset) {
        this(node, null, level, offset);
    }

    // Instantiate a new variable reference from a multi-declaration
    public Variable(AST node, MultiDeclarations parent, short level, short offset){
        // Set the level, offset & bounds
        this.level = level;
        this.offset = offset;
        this.bounds = new LinkedList<ArrayBound>();

        // Deal with partial array declaration
        if(node instanceof ArrayDeclPart) {
            ArrayDeclPart decl = (ArrayDeclPart) node;
            this.name = decl.getName();
            this.bounds.add(decl.getBound1());
            if(decl.getDimensions() > 1)
                this.bounds.add(decl.getBound2());
        }
        // Deal with single scalar
        else if(node instanceof ScalarDecl) {
            ScalarDecl decl = (ScalarDecl) node;
            this.name = decl.getName();
            this.type = decl.getLangType();
        }
        // Deal with partial scalar
        else if(node instanceof ScalarDeclPart && parent != null) {
            ScalarDeclPart decl = (ScalarDeclPart) node;
            this.name = decl.getName();
            this.type = parent.getLangType();
        }
    }

    // Getters for reference info
    public String getName() { return name; }
    public short getLevel() { return level; }
    public short getOffset() { return offset; }
    public List<ArrayBound> getBounds() { return Collections.unmodifiableList(bounds); }
    public LangType getType() { return type; }

    // Reference info
    private String name;
    private short level;
    private short offset;
    private List<ArrayBound> bounds;
    private LangType type;
}
