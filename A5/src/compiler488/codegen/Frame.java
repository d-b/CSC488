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
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.Scope;
import compiler488.codegen.visitor.Visitor;
import compiler488.codegen.visitor.PreProcessor;
import compiler488.codegen.visitor.PostProcessor;
import compiler488.runtime.Machine;

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
    void preMultiDeclarations(MultiDeclarations multiDeclarations) throws CodeGenException {
        for(DeclarationPart part : multiDeclarations.getElements().getList()) {
            // Check the size of the variable if it is an array
            if(part instanceof ArrayDeclPart) {
                ArrayDeclPart decl = (ArrayDeclPart) part;
                if(decl.getSize() > Machine.memorySize)
                    throw new CodeGenException(decl, "size of array exceeds available machine memory");
            }

            // Add the variable to the frame
            currentFrame().addVariable(part, multiDeclarations);
        }
    }

    //
    // Exception handling
    //

    public void traverse(AST root) throws CodeGenException {
        try { super.traverse(root); }
        catch(Exception e) {
            if(e.getCause() instanceof CodeGenException)
                throw (CodeGenException) e.getCause();
            else throw new RuntimeException(e);
        }
    }

    //
    // Frame management
    //

    MinorFrame currentFrame() {
        return frameCurrent;
    }

    void enterFrame(Scope scope) {
        MinorFrame frameNew = new MinorFrame(this, frameCurrent);
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
            int size = current.getSize() + max.getSize();
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
        for(int i = 0; i < parameterList.size(); i++) {
            ScalarDecl decl = parameterList.get(i);
            frameArgs.put(decl.getName(), new Variable(decl, frameLevel,
                    -parameterList.size() - 1 + i)); // ON = -N - 1 + i argument i
        }
    }

    //
    // Frame interface
    //

    public Frame(Scope scope, Scope parent, int lexicalLevel) throws CodeGenException {
        // Scope must be major
        if(!scopeIsMajor(scope))
            throw new RuntimeException("A frame can only be constructed for a major scope.");
        // Initialize members
        frameScope = scope;
        frameParent = parent;
        frameArgs = new HashMap<String, Variable>();
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

    public Variable getVariable(Scope scope, String identifier) {
        // Try the minor scopes first
        for(MinorFrame frame = frameMap.get(scope); frame != null; frame = frame.getParent()) {
            Variable variable = frame.getVariable(identifier);
            if(variable != null) return variable;
        }

        // Otherwise try the arguments
        return frameArgs.get(identifier);
    }

    public Integer getOffsetArguments() {
        if(!isRoutine()) return null; // Bail out if the frame does not belong to a routine
        return -frameArgs.size() - 1; // ON = -N - 1 arguments
    }

    public Integer getOffsetReturn() {
        if(!isRoutine()) return null; // Bail out if the frame does not belong to a routine
        return -frameArgs.size() - 2; // ON = -N - 2 return address
    }

    public Integer getOffsetResult() {
        if(!isRoutine()) return null; // Bail out if the frame does not belong to a routine
        return -frameArgs.size() - 3; // ON = -N - 3 return value (always present, but ignored if procedure)
    }

    public int getLevel() {
        return frameLevel;
    }

    public int getLocalsSize() {
        return (frameRoot != null) ? frameRoot.getSize() : 0;
    }

    public int getArgumentsSize() {
        return frameArgs.size();
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
    private Scope                 frameParent;
    private Scope                 frameScope;
    private Map<String, Variable> frameArgs;
    private Map<AST, MinorFrame>  frameMap;
    private MinorFrame            frameRoot;
    private MinorFrame            frameCurrent;
    private int                   frameLevel;
}

//
// Stack frame for minor scopes
//

class MinorFrame implements Comparable<MinorFrame> {
    MinorFrame(Frame major, MinorFrame parent) {
        // Instantiate internals
        frameMajor = major; frameParent = parent;
        frameChildren = new LinkedList<MinorFrame>();
        frameVariables = new HashMap<String, Variable>();
        frameBase = (parent != null) ? parent.frameSize : 0;
        frameSize = 0;
        // Add this frame to the parent
        if(parent != null) parent.addChild(this);
    }

    public Variable getVariable(String identifier) {
        Variable variable = frameVariables.get(identifier);
        if(variable == null) return null;
        return new Variable(variable, frameMajor.getLevel(), frameBase + variable.getOffset());
    }

    public void addVariable(AST node, MultiDeclarations parent) {
        Variable variable = new Variable(node, parent, frameMajor.getLevel(), frameSize);
        // For scalars, increment the frame size by one
        if(!(node instanceof ArrayDeclPart)) frameSize += 1;
        // If it is an array declaration increment it by the size of the array
        else frameSize += ((ArrayDeclPart) node).getSize();
        // Add it to the map of variables
        frameVariables.put(variable.getName(), variable);
    }

    // Getters/setters
    public MinorFrame getParent() { return frameParent; }
    public List<MinorFrame> getChildren() { return Collections.unmodifiableList(frameChildren); }
    public int getSize() { return frameSize; }
    public void setSize(int size) { frameSize = size; }

    // Compare support
    public int compareTo(MinorFrame other) { return this.frameSize - other.frameSize; }

    //
    // Internal routines & members
    //

    private void addChild(MinorFrame child) {
        frameChildren.add(child);
    }

    private Frame                 frameMajor;
    private MinorFrame            frameParent;
    private List<MinorFrame>      frameChildren;
    private Map<String, Variable> frameVariables;
    private int                   frameBase;
    private int                   frameSize;
}
