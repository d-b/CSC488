package compiler488.codegen;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
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
        labelCount = 0;
    }

    public void enterScope(Scope scope) throws CodeGenException {
        // If the new scope is a major scope construct a frame
        if(Frame.scopeIsMajor(scope))
            majorStack.push(new Frame(scope, currentScope(), majorStack.size()));
        // Construct minor scope and add labels
        Minor minor = new Minor(scope);
        minorStack.push(minor);
        for(Declaration decl : scope.getDeclarations().getList()) {
            if(decl instanceof RoutineDecl) {
                String routine = ((RoutineDecl) decl).getName();
                minor.setLabel(routine, generateLabel(routine));
            }
        }
    }

    public void exitScope() {
        if(minorStack.isEmpty()) return;
        if(inMajorScope()) majorStack.pop();
        minorStack.pop();
    }

    public Scope currentScope() {
        Minor m = minorStack.peek();
        return (m != null) ? m.getScope() : null;
    }

    public Frame currentFrame() {
        return majorStack.peek();
    }

    public int getLevel() {
        return majorStack.size() - 1;
    }

    public String getLabel() {
        return "_L" + labelCount++;
    }

    public String getLabel(String routine) {
        return getLabel(0, routine, LabelPostfix.Start);
    }

    public String getLabel(String routine, LabelPostfix postfix) {
        return getLabel(0, routine, postfix);
    }

    public Variable getVaraible(String variable) {
        Scope scope = currentScope();
        for(Frame frame : majorStack) {
            Variable var = frame.getVariable(scope, variable);
            if(var != null) return var;
            scope = frame.getParent();
        } return null;
    }

    public String getRoutineLabel(LabelPostfix postfix) {
        return getLabel(1, getRoutine().getName(), postfix);
    }

    public int getRoutineCount() {
        Minor m = minorStack.peek();
        return (m != null) ? m.getSize() : 0;
    }

    public int getLocalsSize() {
        int localsSize = 0;
        for(Frame frame : majorStack)
            localsSize += frame.getLocalsSize();
        return localsSize;
    }

    public boolean inMajorScope() {
        Scope scope = currentScope();
        if(scope == null) return false;
        return Frame.scopeIsMajor(scope);
    }

    // Frame access convenience functions
    public RoutineDecl getRoutine() { return currentFrame().getRoutine(); }
    public int getFrameLocalsSize() { return currentFrame().getLocalsSize(); }
    public int getArgumentsSize() { return currentFrame().getArgumentsSize(); }
    public int getOffsetArguments() { return currentFrame().getOffsetArguments(); }
    public int getOffsetReturn() { return currentFrame().getOffsetReturn(); }
    public int getOffsetResult() { return currentFrame().getOffsetResult(); }

    // Get a postfix
    private String getPostfix(LabelPostfix postfix){
        switch(postfix){
        case Start: return "";
        case Inner: return "_INNER";
        case End:   return "_END";
        default:    return "";
        }
    }

    // Get a label
    private String getLabel(int level, String routine, LabelPostfix postfix) {
        String prefix = "_R_";
        Iterator<Minor> iter = minorStack.iterator();
        for(int i = 0; i < level; i++)
            if(iter.hasNext()) iter.next();
        while(iter.hasNext()) {
            String label = iter.next().getLabel(routine);
            if(label != null) return prefix + label + getPostfix(postfix);
        } return null;
    }

    // Generate a label
    private String generateLabel(String routine) {
        return routine + "_LL" + (minorStack.size() - 1);
    }

    // Label prefix
    public enum LabelPostfix { None, Start, Inner, End };

    // Major/minor frames
    private Deque<Frame> majorStack;
    private Deque<Minor> minorStack;

    // Internal state
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
    int getSize() { return labels.size(); }

    // Internal members
    private Scope scope;
    private Map<String, String> labels;
}
