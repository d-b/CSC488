package compiler488.codegen;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.stmt.Scope;

/**
 * Code generation table, containing stack frames and symbol information
 *
 * @author Daniel Bloemendal
 */
public class Table {
    // Instantiate a new code generation table
    public Table() {
        majorStack = new LinkedList<Frame>();
        minorStack = new LinkedList<Minor>();
        majorLevel = minorLevel = -1;
        routineCount = labelCount = 0;
    }

    public void enterScope(Scope scope) {
        // Construct a minor scope
        Minor minor = new Minor(scope);
        minorStack.push(minor);
        // If the new scope is a major scope construct a frame
        if(inMajorScope()) {
            RoutineDecl routine = (RoutineDecl) scope.getParent();
            majorStack.push(new Frame(scope, currentScope(), ++majorLevel));
            if(routine != null) minor.setLabel(routine.getName(), generateLabel(routine.getName()));
        }
        // Add routine labels for minor scope
        routineCount = 0; // Reset the routine count
        minorLevel += 1;  // Increment the minor level
        for(Declaration decl : scope.getDeclarations().getList()) {
            if(decl instanceof RoutineDecl) {
                String routine = ((RoutineDecl) decl).getName();
                minor.setLabel(routine, generateLabel(routine));
                routineCount += 1;
            }
        }
    }

    public void exitScope() {
        if(minorStack.isEmpty()) return;
        if(inMajorScope()) { majorStack.pop(); majorLevel--; }
        minorStack.pop(); minorLevel--;
    }

    public Scope currentScope() {
        Minor m = minorStack.peek();
        return (m != null) ? m.getScope() : null;
    }

    public Frame currentFrame() {
        return majorStack.peek();
    }

    public short getLevel() {
        return majorLevel;
    }

    public String getLabel() {
        return "_L" + labelCount++;
    }

    public String getLabel(String routine) {
        return getLabel(routine, false);
    }

    public String getLabel(String routine, boolean end) {
        String prefix = "_R_";
        String postfix = end ? "_END" : "";
        for(Minor m : minorStack) {
            String label = m.getLabel(routine);
            if(label != null) return prefix + label + postfix;
        } return null;
    }

    public Short getOffset(String variable) {
        Scope scope = currentScope();
        for(Frame frame : majorStack) {
            Short offset = frame.getOffset(scope, variable);
            if(offset != null) return offset;
            scope = frame.getParent();
        } return null;
    }

    public boolean inMajorScope() {
        Scope scope = currentScope();
        if(scope == null) return false;
        return Frame.scopeIsMajor(scope);
    }

    public int getRoutineCount() {
        return routineCount;
    }

    // Frame access convenience functions
    public RoutineDecl getRoutine() { return currentFrame().getRoutine(); }
    public short getLocalsSize() { return currentFrame().getLocalsSize(); }
    public short getArgumentsSize() { return currentFrame().getArgumentsSize(); }
    public short getOffsetReturn() { return currentFrame().getOffsetReturn(); }
    public short getOffsetResult() { return currentFrame().getOffsetResult(); }

    // Generate a label
    String generateLabel(String routine) {
        return routine + "_LL" + minorLevel;
    }

    // Major/minor frames
    private Deque<Frame> majorStack;
    private Deque<Minor> minorStack;

    // Internal state
    private short majorLevel;
    private short minorLevel;
    private int routineCount;
    private int labelCount;
}

class Minor {
    // Instantiate a new minor scope
    Minor(Scope scope) {
        this.scope = scope;
        this.labels = new HashMap<String, String>();
    }

    // Getters/setters
    void setLabel(String routine, String label) { labels.put(routine, label); }
    String getLabel(String routine) { return labels.get(routine); }
    Scope getScope() { return scope; }

    // Internal members
    private Scope scope;
    private Map<String, String> labels;
}
