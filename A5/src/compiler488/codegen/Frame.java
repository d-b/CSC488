package compiler488.codegen;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import compiler488.ast.AST;
import compiler488.ast.decl.ArrayDeclPart;
import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.DeclarationPart;
import compiler488.ast.decl.MultiDeclarations;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.decl.ScalarDecl;
import compiler488.ast.decl.ScalarDeclPart;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.Scope;
import compiler488.codegen.visitor.Visitor;
import compiler488.codegen.visitor.PreProcessor;
import compiler488.codegen.visitor.PostProcessor;

/**
 * Stack frame for major scopes
 *
 * Offsets returned by Frame correspond to the code template design for stack frames.
 *
 * ON = -N - 3 return value (always present, but ignored if procedure)
 * ON = -N - 2 return address
 * ON = -N - 1 argument 1
 * ... ...
 * ON = -2     argument N
 * ON = -1     saved value of previous ADDR <LL> 0 (offset 0 of display at level <LL>)
 * ON =  0     1st word of local variable memory in activation frame
 * ... ...
 *
 * @author Daniel Bloemendal
 */
public class Frame extends Visitor {
    //
    // Processors
    //

    @PreProcessor(target="Scope")
    void preScope(Scope scope){
        // Enter a new frame
        enterFrame(scope);
        // Exclude new routines
        for(Declaration decl : scope.getDeclarations().getList())
            if(decl instanceof RoutineDecl) exclude(decl);
    }
    @PostProcessor(target="Scope")
    void postScope(Scope scope){
        // Exit a frame
        exitFrame();
    }

    @PreProcessor(target="MultiDeclarations")
    void preMultiDeclarations(MultiDeclarations multiDeclarations) {
        for(DeclarationPart part : multiDeclarations.getElements().getList())
            currentFrame().addVariable(part);
    }

    //
    // Frame management
    //

    MinorFrame currentFrame() {
        return frameCurrent;
    }

    void enterFrame(Scope scope) {
        MinorFrame frameNew = new MinorFrame(frameCurrent);
        if(frameRoot == null) frameRoot = frameNew;
        frameMap.put(scope, frameNew);
        frameCurrent = frameNew;
    }

    void exitFrame() {
        // Compute frame size
        MinorFrame current = currentFrame();
        List<MinorFrame> children = current.getChildren();
        if(!children.isEmpty()) {
            MinorFrame max = Collections.max(children);
            short size = (short)(current.getSize() + max.getSize());
            current.setSize(size);
        }

        // Switch to parent frame
        frameCurrent = frameCurrent.getParent();
    }

    //
    // Helpers
    //

    void addArguments() {
        RoutineDecl routine = getRoutine();
        if(routine == null) return; // Scope does not belong to a routine
        List<ScalarDecl> parameterList = routine.getParameters().getList();
        for(int i = 0; i < parameterList.size(); i++)
            frameArgs.put(parameterList.get(i).getIdent().getId(), i);
    }

    //
    // Frame interface
    //

    public Frame(Scope scope, Scope parent, short lexicalLevel) {
        // Scope must be major
        if(!scopeIsMajor(scope))
            throw new RuntimeException("A frame can only be constructed for a major scope.");
        // Initialize members
        frameScope = scope;
        frameParent = parent;
        frameArgs = new HashMap<String, Integer>();
        frameMap = new HashMap<AST, MinorFrame>();
        frameRoot = null;
        frameCurrent = null;
        frameLevel = lexicalLevel;
        // Add arguments
        addArguments();
        // Traverse the AST
        traverse(frameScope);
    }

    public static boolean scopeIsMajor(Scope scope) {
        boolean isRoutine = (scope.getParent() instanceof RoutineDecl);
        boolean isProgram = (scope instanceof Program);
        return isRoutine || isProgram;
    }

    public Short getOffset(Scope scope, String identifier) {
        // Try the scope first
        MinorFrame frame = frameMap.get(scope);
        if(frame == null) return null;
        Short offset = frame.getOffset(identifier);
        if(offset != null) return offset;

        // Otherwise try the arguments
        if(!isRoutine()) return null; // Bail out if the frame does not belong to a routine
        Integer arg = frameArgs.get(identifier);
        if(arg == null) return null;
        offset = (short) (int) arg;
        return (short)(offset - frameArgs.size() - 1); // ON = -N - 1 argument 1
    }

    public Short getOffsetReturn() {
        if(!isRoutine()) return null; // Bail out if the frame does not belong to a routine
        return (short)(-frameArgs.size() - 2); // ON = -N - 2 return address
    }

    public Short getOffsetResult() {
        if(!isRoutine()) return null; // Bail out if the frame does not belong to a routine
        return (short)(-frameArgs.size() - 3); // ON = -N - 3 return value (always present, but ignored if procedure)
    }

    public short getLevel() {
        return frameLevel;
    }

    public short getSize() {
        return (frameRoot != null) ? frameRoot.getSize() : 0;
    }

    public short getArgumentsSize() {
        return (short) frameArgs.size();
    }

    public Scope getScope() {
        return frameScope;
    }

    public Scope getParent() {
        return frameParent;
    }

    public RoutineDecl getRoutine() {
        return isRoutine() ? ((RoutineDecl) frameScope.getParent()) : null;
    }

    public boolean isRoutine() {
        return (frameScope.getParent() instanceof RoutineDecl);
    }

    // Internal members
    private Scope                frameParent;
    private Scope                frameScope;
    private Map<String, Integer> frameArgs;
    private Map<AST, MinorFrame> frameMap;
    private MinorFrame           frameRoot;
    private MinorFrame           frameCurrent;
    private short                frameLevel;
}

//
// Stack frame for minor scopes
//

class MinorFrame implements Comparable<MinorFrame> {
    MinorFrame(MinorFrame parent) {
        // Instantiate internals
        nodeMap = new HashMap<String, AST>();
        offsetMap = new HashMap<String, Short>();
        frameChildren = new LinkedList<MinorFrame>();
        frameBase = (parent != null) ? parent.frameSize : 0;
        frameParent = parent;
        frameSize = 0;
        // Add this frame to the parent
        if(parent != null) parent.addChild(this);
    }

    public AST getNode(String identifier) {
        return nodeMap.get(identifier);
    }

    public Short getOffset(String identifier) {
        for(MinorFrame minor = this; minor != null; minor = minor.frameParent) {
            Short offset = minor.offsetMap.get(identifier);
            if(offset != null) return (short)(minor.frameBase + offset);
        } return null;
    }

    public void addVariable(DeclarationPart decl) {
        if(decl instanceof ScalarDeclPart)
            addVariable((ScalarDeclPart) decl);
        else if(decl instanceof ArrayDeclPart)
            addVariable((ArrayDeclPart) decl);
    }

    public void addVariable(ScalarDeclPart scalarDecl) {
        String ident = scalarDecl.getIdent().getId();
        nodeMap.put(ident, scalarDecl);
        offsetMap.put(ident, frameSize);
        frameSize += 1;
    }

    public void addVariable(ArrayDeclPart arrayDecl) {
        String ident = arrayDecl.getIdent().getId();
        nodeMap.put(ident, arrayDecl);
        offsetMap.put(ident, frameSize);
        frameSize += arrayDecl.getSize();
    }

    // Getters/setters
    public MinorFrame getParent() { return frameParent; }
    public List<MinorFrame> getChildren() { return Collections.unmodifiableList(frameChildren); }
    public short getSize() { return frameSize; }
    public void setSize(short size) { frameSize = size; }

    // Compare support
    public int compareTo(MinorFrame other) { return this.frameSize - other.frameSize; }

    //
    // Internal routines & members
    //

    private void addChild(MinorFrame child) {
        frameChildren.add(child);
    }

    private Map<String, AST>   nodeMap;
    private Map<String, Short> offsetMap;
    private MinorFrame         frameParent;
    private List<MinorFrame>   frameChildren;
    private short              frameBase;
    private short              frameSize;
}
