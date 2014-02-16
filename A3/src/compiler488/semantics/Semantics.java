package compiler488.semantics;

import java.io.*;

import compiler488.symbol.SymbolTable;
import compiler488.ast.AST;
import compiler488.ast.decl.ArrayDeclPart;
import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.DeclarationPart;
import compiler488.ast.decl.MultiDeclarations;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.decl.ScalarDeclPart;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.Scope;
import compiler488.ast.stmt.Stmt;
import compiler488.ast.type.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

/** Implement semantic analysis for compiler 488 
 *  @author Daniel Bloemendal
 */
public class Semantics {
    //
    // Actions
    //
    
    @Action(number = 0) // Start program scope.
    Boolean actionProgramStart(Program program) {
        symbolTable.scopeEnter(SymbolTable.ScopeType.Program);
        return true;
    }
    
    @Action(number = 1) // End program scope.
    Boolean actionProgramEnd(Program program) {
        symbolTable.scopeExit();
        return true;
    }
    
    @Action(number = 2) // Associate declaration(s) with scope.
    Boolean actionAssociateDeclarations(Declaration decl) {
        if(decl instanceof MultiDeclarations) {
            WorkingVarList varList = getWorkingVarList();
            for(WorkingVar var : varList.variables)
                if     (var.dimensions  < 1) symbolTable.declareVariable(var.name, var.type);
                else if(var.dimensions == 1) symbolTable.declareVariable(var.name, var.type, var.lowerBounds.get(0), var.upperBounds.get(0));
                else if(var.dimensions >= 2) symbolTable.declareVariable(var.name, var.type, var.lowerBounds.get(0), var.upperBounds.get(0), var.lowerBounds.get(1), var.upperBounds.get(1));
        } return true;
    }
    
    @Action(number = 4) // Start function scope.
    Boolean actionFunctionStart(RoutineDecl routineDecl) {
        symbolTable.scopeEnter(SymbolTable.ScopeType.Function);
        return true;
    }
    
    @Action(number = 5) // End function scope.
    Boolean actionFunctionEnd(RoutineDecl routineDecl) {
        symbolTable.scopeExit();
        return true;
    }

    @Action(number = 8) // Start procedure scope.
    Boolean actionProcedureStart(RoutineDecl routineDecl) {
        symbolTable.scopeEnter(SymbolTable.ScopeType.Procedure);
        return true;
    }
    
    @Action(number = 9) // End procedure scope.
    Boolean actionProcedureEnd(RoutineDecl routineDecl) {
        symbolTable.scopeExit();
        return true;
    }
    
    @Action(number = 10) // Declare scalar variable.
    Boolean actionDeclareScalar(ScalarDeclPart scalarDecl) {
        WorkingVarList varList = getWorkingVarList();
        WorkingVar var = new WorkingVar();
        var.name = scalarDecl.getName();
        var.dimensions = 0;
        varList.variables.add(var);
        return true;
    }
    
    @Action(number = 19) // Declare one dimensional array with specified bound.
    Boolean actionDeclareArray1D(ArrayDeclPart arrayDecl) {
        WorkingVarList varList = getWorkingVarList();
        WorkingVar var = new WorkingVar();
        var.name = arrayDecl.getName();
        var.dimensions = 1;
        var.lowerBounds.add(arrayDecl.getLowerBoundary1());
        var.upperBounds.add(arrayDecl.getUpperBoundary1());
        varList.variables.add(var);
        return true;
    }
    
    @Action(number = 46) // Check that lower bound is <= upper bound.
    Boolean actionCheckArrayBounds(ArrayDeclPart arrayDecl) {
        if(arrayDecl.getDimensions() >= 1)
            if(arrayDecl.getLowerBoundary1() > arrayDecl.getUpperBoundary1()) return false;
        if(arrayDecl.getDimensions() >= 2)
            if(arrayDecl.getLowerBoundary2() > arrayDecl.getUpperBoundary2()) return false;
        return true;
    }    
    
    @Action(number = 47) // Associate type with variables.
    Boolean actionAssociateTypeWithVar(Declaration declaration) {
        WorkingVarList varList = getWorkingVarList();
        for(WorkingVar var : varList.variables) {
            var.type = declaration.getType().equals(Type.TYPE_BOOLEAN)
                    ? SymbolTable.ScalarType.Boolean : SymbolTable.ScalarType.Integer;
        } return true;
    }
    
    @Action(number = 48) // Declare two dimensional array with specified bound.
    Boolean actionDeclareArray2D(ArrayDeclPart arrayDecl) {
        WorkingVarList varList = getWorkingVarList();
        WorkingVar var = new WorkingVar();
        var.name = arrayDecl.getName();
        var.dimensions = 2;
        var.lowerBounds.add(arrayDecl.getLowerBoundary1());
        var.lowerBounds.add(arrayDecl.getLowerBoundary2());
        var.upperBounds.add(arrayDecl.getUpperBoundary1());
        var.upperBounds.add(arrayDecl.getUpperBoundary2());
        varList.variables.add(var);
        return true;
    }
    
    //
    // Processors
    //
    
    @PreProcessor(target = "Program")
    void preProgram(Program program) {
        semanticAction(0); // S00: Start program scope.
        exploreScope(program);
    }
    
    @PostProcessor(target = "Program")
    void postProgram(Program program) {
        semanticAction(1); // S01: End program scope.    
    }
    
    @PreProcessor(target = "MultiDeclarations")
    void preMultiDeclarations(MultiDeclarations multiDecls) {
        analysisWorking = new WorkingVarList();
        for(DeclarationPart part : multiDecls.getElements().getList())
            discoverNode(part);
    }
    
    @PostProcessor(target = "MultiDeclarations")
    void postMultiDeclarations(MultiDeclarations multiDecls) {
        semanticAction(47); // S47: Associate type with variables.
        semanticAction(02); // S02: Associate declaration(s) with scope.
    }    
    
    @PostProcessor(target = "ScalarDeclPart")
    void postScalarDeclPart(ScalarDeclPart scalarDeclPart) {
        semanticAction(10); // S10: Declare scalar variable.
    }
    
    @PostProcessor(target = "ArrayDeclPart")
    void postArrayDeclPart(ArrayDeclPart arrayDeclPart) {
        semanticAction(46); // S46: Check that lower bound is <= upper bound.
        if(arrayDeclPart.getDimensions() == 1)
            semanticAction(19); // S19: Declare one dimensional array with specified bound.
        else if(arrayDeclPart.getDimensions() == 2)
            semanticAction(48); // S48: Declare two dimensional array with specified bound.
    }
    
    @PreProcessor(target = "RoutineDecl")
    void preRoutineDecl(RoutineDecl routineDecl) {
        if(!routineDecl.isForward()) {
            if(!routineDecl.getReturnType().equals(Type.TYPE_NIL))
                semanticAction(4); // S04: Start function scope.
            else
                semanticAction(8); // S08: Start procedure scope.
            exploreScope(routineDecl.getBody());
        }
    }
    
    @PostProcessor(target = "RoutineDecl")
    void postRoutineDecl(RoutineDecl routineDecl) {
        if(!routineDecl.isForward())
            if(!routineDecl.getReturnType().equals(Type.TYPE_NIL))
                semanticAction(5); // S05: End function scope.
            else
                semanticAction(9); // S09: End procedure scope.            
    }
    
    //
    // Helpers
    //
    
    void exploreScope(Scope scope) {
        // Add declarations and statements to the stack
        LinkedList<Stmt>          stmts = scope.getStatements().getList();
        LinkedList<Declaration>   decls = scope.getDeclarations().getList();
        ListIterator<Stmt>        si    = stmts.listIterator(stmts.size());
        ListIterator<Declaration> di    = decls.listIterator(decls.size());
        while(si.hasPrevious()) discoverNode(si.previous());
        while(di.hasPrevious()) discoverNode(di.previous());
    }
       
    //
    // State management
    //
    
    void discoverNode(AST obj) {
        analysisStack.push(obj);
    }
    
    WorkingVar getWorkingVar() {
        return (WorkingVar) analysisWorking;
    }    
    
    WorkingVarList getWorkingVarList() {
        return (WorkingVarList) analysisWorking;
    }
    
    //
    // Processor/action management 
    //
    
    void populateMappings() {
        Class<? extends Semantics> thisClass = this.getClass();
        for(Method method : thisClass.getDeclaredMethods()) {
            PreProcessor  preProcInfo  = method.getAnnotation(PreProcessor.class);
            PostProcessor postProcInfo = method.getAnnotation(PostProcessor.class);
            Action        actInfo      = method.getAnnotation(Action.class);
            if(preProcInfo  != null) preProcessorsMap.put(preProcInfo.target(), method);
            if(postProcInfo != null) postProcessorsMap.put(postProcInfo.target(), method);
            if(actInfo      != null) actionsMap.put(actInfo.number(), method);
        }
    }
    
    boolean invokePreProcessor(AST node) {
        return invokeProcessor(node, preProcessorsMap);
    }
    
    boolean invokePostProcessor(AST node) {
        return invokeProcessor(node, postProcessorsMap);
    }    
    
    boolean invokeProcessor(AST obj, Map<String, Method> map) {
        Method m = map.get(obj.getClass().getSimpleName());
        if(m == null) return false;
        analysisTop = obj;
        
        // Invoke the processor on object
        try {
            m.invoke(this, obj); return true;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace(); return false;
        }
    }
    
    void semanticAction(int actionNumber) {
        if( traceSemantics ){
            if(traceFile.length() > 0 ){
                //output trace to the file represented by traceFile
                try{
                    //open the file for writing and append to it
                    new File(traceFile);
                    Tracer = new FileWriter(traceFile, true);
                                  
                    Tracer.write("Sematics: S" + actionNumber + "\n");
                    //always be sure to close the file
                    Tracer.close();
                }
                catch (IOException e) {
                  System.out.println(traceFile + 
                    " could be opened/created.  It may be in use.");
                }
            }
            else{
                //output the trace to standard out.
                System.out.println("Sematics: S" + actionNumber );
            }
         
        }
        
        Method m = actionsMap.get(actionNumber);
        if(m == null) System.out.println("Unhandled Semantic Action: S" + actionNumber );
        else {
            // Invoke the semantic action. 
            try {
                Boolean result = (Boolean) m.invoke(this, analysisTop);
                System.out.println((result ? "Semantic Action: S" : "Semantic Error: S") + actionNumber);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    //
    // Semantic analysis life cycle
    //

    public Semantics () {
        symbolTable       = new SymbolTable();
        preProcessorsMap  = new HashMap<String, Method>();
        postProcessorsMap = new HashMap<String, Method>();
        actionsMap        = new HashMap<Integer, Method>();
        analysisGrey      = new HashSet<AST>();
        analysisStack     = new Stack<AST>();
        analysisWorking   = null;
    }    

    public void Initialize() {
        populateMappings();
    }

    public void Analyze(Program ast) {
        // Add the initial element to the stack
        analysisStack.add(ast);
        
        // Traverse the AST
        while(!analysisStack.empty()) {
            // Fetch top of the analysis stack
            AST top = analysisStack.peek();

            // If the object has not yet been seen
            if(!analysisGrey.contains(top)) {
                analysisGrey.add(top);
                invokePreProcessor(top);
            }
            // Finish processing object and pop it off of the stack
            else {
                invokePostProcessor(top);
                analysisStack.pop();
            }
        }
    }
    
    public void Finalize() {  
    }
    
    //
    // Members
    //
    
    /** flag for tracing semantic analysis */
    private boolean traceSemantics = false;
    /** file sink for semantic analysis trace */
    private String traceFile = new String();
    private SymbolTable symbolTable;
    public FileWriter Tracer;
    public File f;

    /** Maps for processors and actions */
    private Map<String, Method>  preProcessorsMap;
    private Map<String, Method>  postProcessorsMap;
    private Map<Integer, Method> actionsMap;

    /** Analysis state */
    private AST         analysisTop;
    private Set<AST>    analysisGrey;
    private Stack<AST>  analysisStack;
    private WorkingDecl analysisWorking;    
}

//
// Processor/action annotations
//

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface PreProcessor {
    String target();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface PostProcessor {
    String target();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Action {
    int number();
}

//
// Working structures
//

class WorkingDecl{};
class WorkingVar extends WorkingDecl {
    public String name;
    public int dimensions;
    public Vector<Integer> lowerBounds;
    public Vector<Integer> upperBounds;
    public SymbolTable.ScalarType type;
    WorkingVar() {lowerBounds = new Vector<Integer>();
                  upperBounds = new Vector<Integer>();}
}
class WorkingVarList extends WorkingDecl {
    public List<WorkingVar> variables;
    WorkingVarList() {variables = new Vector<WorkingVar>();}
}
