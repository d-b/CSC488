package compiler488.semantics;

import java.io.*;

import compiler488.symbol.FunctionSymbol;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;
import compiler488.symbol.VariableSymbol;
import compiler488.semantics.Errors;
import compiler488.ast.AST;
import compiler488.ast.SourceLoc;
import compiler488.ast.SourceLocPrettyPrinter;
import compiler488.ast.decl.ArrayBound;
import compiler488.ast.decl.ArrayDeclPart;
import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.MultiDeclarations;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.decl.ScalarDecl;
import compiler488.ast.decl.ScalarDeclPart;
import compiler488.ast.expn.IdentExpn;
import compiler488.ast.stmt.AssignStmt;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.ResultStmt;
import compiler488.ast.stmt.ReturnStmt;
import compiler488.ast.stmt.Scope;
import compiler488.ast.type.BooleanType;
import compiler488.ast.type.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
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
    Boolean actionAssociateVariableDeclarations(Declaration decl) {
        if(decl instanceof MultiDeclarations)
            for(Entry<String, Symbol> entry: workingEntries())
                if(!symbolTable.scopeSet(entry.getKey(), entry.getValue())) return false;
        return true;
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
    
    @Action(number = 6) // Start statement scope.
    Boolean actionMinorScopeStart(Scope scope) {
        symbolTable.scopeEnter(SymbolTable.ScopeType.Statement);
        return true;
    }
    
    @Action(number = 7) // End statement scope.
    Boolean actionMinorScopeEnd(Scope scope) {
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
        return workingSet(scalarDecl.getName(),
                new VariableSymbol(scalarDecl.getName()));
    }
    
    @Action(number = 11) // Declare forward function.
    Boolean actionDeclareForwardFunction(RoutineDecl routineDecl) {
        return symbolTable.scopeSet(routineDecl.getName(),
                new FunctionSymbol(routineDecl.getName(), routineDecl.getFunctionType(), false));
    }
    
    @Action(number = 12) // Declare function with parameters ( if any ) and specified type. 
    Boolean actionDeclareFunction(RoutineDecl routineDecl) {
        Symbol symbol = symbolTable.find(routineDecl.getName(), false);
        if(symbol == null)
            return workingSet(routineDecl.getName(),
                    new FunctionSymbol(routineDecl.getName(), routineDecl.getFunctionType()));
        else return FunctionSymbol.isForward(symbol);
    }
    
    @Action(number = 13) // Associate scope with function/procedure.
    Boolean actionAssociateRoutineDeclaration(RoutineDecl routineDecl) { 
        Symbol symbol = symbolTable.find(routineDecl.getName(), false /* allScopes */);
        if(symbol == null)            
            return symbolTable.scopeSet(routineDecl.getName(),
                    workingFind(routineDecl.getName(), false /* allScopes */));
        else if(FunctionSymbol.isForward(symbol)) {
            ((FunctionSymbol) symbol).hasBody(true); return true;
        } return false;
    }
        
    @Action(number = 14) // Set parameter count to zero.
    Boolean actionResetParameterCount(RoutineDecl routineDecl) {
        return true;
    }
    
    @Action(number = 15) // Declare parameter with specified type.
    Boolean actionDeclareParameter(ScalarDecl scalarDecl) {
        return workingSet(scalarDecl.getName(), new VariableSymbol(scalarDecl.getName()), true /* newScope */);
    }
    
    @Action(number = 16) // Increment parameter count by one.
    Boolean actionIncrementParameterCount(ScalarDecl scalarDecl) {
        return true;
    }
    
    @Action(number = 17) // Declare forward procedure.
    Boolean actionDeclareForwardProcedure(RoutineDecl routineDecl) {
        return actionDeclareForwardFunction(routineDecl);
    }    
    
    @Action(number = 18) // Declare procedure with parameters ( if any ).
    Boolean actionDeclareProcedure(RoutineDecl routineDecl) {
        return actionDeclareFunction(routineDecl);
    }
    
    @Action(number = 19) // Declare one dimensional array with specified bound.
    Boolean actionDeclareArray1D(ArrayDeclPart arrayDecl) {
    	ArrayBound b1 = arrayDecl.getBound1();
    	Symbol sym = new VariableSymbol(arrayDecl.getName(),
                b1.getLowerboundValue(), b1.getUpperboundValue()); 
    	
        return workingSet(arrayDecl.getName(), sym);
    }
    
    // TODO: Finish assignment checking after expression checking is finished
    @Action(number = 34) // Check that variable and expression in assignment are the same type.
    Boolean actionCheckAssignmentTypes(AssignStmt assignStmt) {
        return true;
    }
    
    private boolean isBoundValid(ArrayBound b) {
    	return b.getLowerboundValue() <= b.getUpperboundValue();
    }

    @Action(number = 46) // Check that lower bound is <= upper bound.
    Boolean actionCheckArrayBounds(ArrayDeclPart arrayDecl) {
        if(arrayDecl.getDimensions() >= 1)
            if (!isBoundValid(arrayDecl.getBound1())) return false;
        if(arrayDecl.getDimensions() >= 2)
        	if (!isBoundValid(arrayDecl.getBound2())) return false;
        return true;
    }    
    
    @Action(number = 47) // Associate type with variables.
    Boolean actionAssociateTypeWithVar(Declaration declaration) {
        for(Entry<String, Symbol> entry : workingEntries())
            ((VariableSymbol) entry.getValue()).setType(declaration.getType());
        return true;
    }
    
    @Action(number = 48) // Declare two dimensional array with specified bound.
    Boolean actionDeclareArray2D(ArrayDeclPart arrayDecl) {
    	ArrayBound b1 = arrayDecl.getBound1();
    	ArrayBound b2 = arrayDecl.getBound2();
    	Symbol sym = new VariableSymbol(arrayDecl.getName(),
                b1.getLowerboundValue(), b1.getUpperboundValue(),
                b2.getLowerboundValue(), b2.getUpperboundValue());
    	
        return workingSet(arrayDecl.getName(), sym);
    }
    
    @Action(number = 49) // If function/procedure was declared forward, verify forward declaration matches.
    Boolean actionCheckRoutineDeclaration(RoutineDecl routineDecl) {
        // Attempt to find a function symbol with the given routine's name
        Symbol symbol = symbolTable.find(routineDecl.getName(), false);
        if(!(symbol instanceof FunctionSymbol)) return true;

        // Verify that it is a function symbol and the type matches
        return ((FunctionSymbol) symbol).getType().equals(routineDecl.getFunctionType());
    }    
    
    @Action(number = 51) // Check that result statement is directly inside a function.
    Boolean actionCheckResult(ResultStmt returnStmt) {
        Scope scope = (Scope) firstOf(returnStmt, Scope.class);
        return (scope != null
             && scope.getParent() instanceof RoutineDecl
             && ((RoutineDecl) scope.getParent()).isFunction());
    }    
    
    @Action(number = 52) // Check that return statement is directly inside a procedure.
    Boolean actionCheckReturn(ReturnStmt returnStmt) {
        Scope scope = (Scope) firstOf(returnStmt, Scope.class);
        return (scope != null
             && scope.getParent() instanceof RoutineDecl
             && !((RoutineDecl) scope.getParent()).isFunction());
    }
    
    @Action(number = 54) // Associate parameters if any with scope.
    Boolean actionAssociateParameters(Scope scope) {
        for(Entry<String, Symbol> entry: workingEntries()) {
            if(!(entry.getValue() instanceof VariableSymbol)
            || !symbolTable.scopeSet(entry.getKey(), entry.getValue())) return false;
        } return true;
    }
    
    //
    // Processors
    //
       
    @PreProcessor(target = "Program")
    void preProgram(Program program) {
        semanticAction(0); // S00: Start program scope.
    }
    
    @PostProcessor(target = "Program")
    void postProgram(Program program) {
        semanticAction(1); // S01: End program scope.    
    }
    
    @PreProcessor(target = "Scope")
    void preScope(Scope scope) {
        if(scope.getParent() instanceof RoutineDecl)
            semanticAction(54); // S54: Associate parameters if any with scope.
        else
            semanticAction(6); // S06: Start statement scope.
    }
    
    @PostProcessor(target = "Scope")
    void postScope(Scope scope) {
        if(!(scope.getParent() instanceof RoutineDecl))
            semanticAction(7); // S07: End statement scope.
    }    
    
    @PreProcessor(target = "MultiDeclarations")
    void preMultiDeclarations(MultiDeclarations multiDecls) {
        workingPush(); // Prepare for variable declarations.
    }
    
    @PostProcessor(target = "MultiDeclarations")
    void postMultiDeclarations(MultiDeclarations multiDecls) {
        semanticAction(47); // S47: Associate type with variables.
        semanticAction(02); // S02: Associate declaration(s) with scope.
        workingPop(); // Exit variable declaration scope.
    }
    
    @PostProcessor(target = "ScalarDeclPart")
    void postScalarDeclPart(ScalarDeclPart scalarDeclPart) {
        semanticAction(10); // S10: Declare scalar variable.
    }
    
    @PostProcessor(target = "ScalarDecl")
    void postScalarDecl(ScalarDecl scalarDecl) {
        if(firstOf(scalarDecl, RoutineDecl.class) != null) {
            semanticAction(15); // S15: Declare parameter with specified type.
            semanticAction(16); // S16: Increment parameter count by one.
        }
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
        workingPush(); // Prepare for routine declarations.
        semanticAction(14); // S14: Set parameter count to zero.
        if(routineDecl.isFunction()) semanticAction(12); // S12: Declare function with parameters ( if any ) and specified type.
        else                         semanticAction(18); // S18: Declare procedure with parameters ( if any ).         
        if(!routineDecl.isForward()) {
            semanticAction(49); // S49: If function/procedure was declared forward, verify forward declaration matches.
            if(routineDecl.isFunction()) semanticAction(4); // S04: Start function scope.
            else                         semanticAction(8); // S08: Start procedure scope.
        }
        workingPush(); // Prepare for parameter declarations.
    }
    
    @PostProcessor(target = "RoutineDecl")
    void postRoutineDecl(RoutineDecl routineDecl) {
        workingPop(); // Exit parameter scope.
        if(!routineDecl.isForward()) {
            if(routineDecl.isFunction()) semanticAction(5); // S05: End function scope.
            else                         semanticAction(9); // S09: End procedure scope.
            semanticAction(13); // S13: Associate scope with function/procedure.
        }
        else if(routineDecl.isFunction()) semanticAction(11); // S11: Declare forward function.
             else                         semanticAction(17); // S17: Declare forward procedure.
        workingPop(); // Exit routine scope. 
    }
    
    @PostProcessor(target = "ResultStmt")
    void postResultStmt(ResultStmt resultStmt) {
        semanticAction(51); // S51: Check that result statement is directly inside a function.
    }    
    
    @PostProcessor(target = "ReturnStmt")
    void postReturnStmt(ReturnStmt returnStmt) {
        semanticAction(52); // S52: Check that return statement is directly inside a procedure.
    }
    
    @PostProcessor(target = "AssignStmt")
    void postAssignStmt(AssignStmt assignStmt) {
        semanticAction(34); // S34: Check that variable and expression in assignment are the same type.
    }
    
    //
    // Working scope
    //
    
    void workingPush() {
        analysisWorking.push(new HashMap<String, Symbol>());
    }
    
    void workingPop() {
        analysisWorking.pop();
    }
    
    @SuppressWarnings("unchecked")
    Map<String, Symbol> workingTop() {
        return (Map<String, Symbol>) analysisWorking.peek();
    }
        
    Set<Entry<String, Symbol>> workingEntries() {
        return workingTop().entrySet();
    }
    
    Boolean workingSet(String name, Symbol symbol) {
        return workingSet(name, symbol, false);
    }    
    
    Boolean workingSet(String name, Symbol symbol, Boolean newScope) {
        // If we are not working in a new scope, ensure the symbol is not yet defined
        if(!newScope && symbolTable.find(name, false) != null) return false;
        // Ensure symbol was not yet defined in working scope
        if(workingTop().get(name) != null) return false;
        // Add symbol to working scope
        workingTop().put(name, symbol); return true;
    }
    
    Symbol workingFind(String name) {
        return workingFind(name, true);
    }
    
    @SuppressWarnings("unchecked")
    Symbol workingFind(String name, Boolean allScopes) {
        if(!allScopes) return workingTop().get(name);
        for(Object scope : analysisWorking) {
            Symbol symbol = ((Map<String, Symbol>) scope).get(name);
            if(symbol != null) return symbol;
        } return null;
    }
    
    void workingClear() {
        analysisWorking.clear();
        workingPush();
    }
    
    //
    // Helpers
    //
    
    static <T> AST firstOf(AST obj, Class<T> type) {
        for(AST node = obj; node != null; node = node.getParent())
            if(type.isInstance(node)) return node; 
        return null;
    }
    
    static SymbolTable.ScalarType translateType(Type type) {
        return type instanceof BooleanType ? SymbolTable.ScalarType.Boolean : SymbolTable.ScalarType.Integer;
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
        try { m.invoke(this, obj); return true; }
        catch (IllegalAccessException e)    { e.printStackTrace(); return false; }
        catch (IllegalArgumentException e)  { e.printStackTrace(); return false; }
        catch (InvocationTargetException e) { e.printStackTrace(); return false; }
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
            Boolean result = false;

            // Invoke the semantic action. 
            try {
                result = (Boolean) m.invoke(this, analysisTop);

                if (result) {
                    System.out.println("Semantic Action: S" + actionNumber);
                } else {
                    SourceLoc loc = analysisTop.getLoc();
                    String errorMessage = Errors.getError(actionNumber);
                    if(errorMessage == null) errorMessage = "Semantic Error S" + actionNumber;
                    else errorMessage = "S" + actionNumber + ": " + errorMessage;
                    SourceLocPrettyPrinter pp = new SourceLocPrettyPrinter(System.out, analysisSource, loc);
                    System.out.println(pp.getFileRef() + ": " + errorMessage);
                    pp.print();
                    analysisErrors += 1;
                }
            }
            catch (IllegalAccessException e)    { e.printStackTrace(); }
            catch (IllegalArgumentException e)  { e.printStackTrace(); }
            catch (InvocationTargetException e) { e.printStackTrace(); }

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
        analysisStack     = new LinkedList<AST>();
        analysisWorking   = new LinkedList<Object>();
        analysisErrors    = 0;
    }    

    public void Initialize() {
        populateMappings();
    }

    public Boolean Analyze(Program ast, List<String> source) {
        // Store source code
        analysisSource = new Vector<String>(source);
    	
        // Add the initial element to the stack
        analysisStack.add(ast);
        
        // Traverse the AST
        while(!analysisStack.isEmpty()) {
            // Fetch top of the analysis stack
            AST top = analysisStack.peek();

            // If the node has not yet been seen
            if(!analysisGrey.contains(top)) {
                // Add node to grey set and invoke preprocessor
                analysisGrey.add(top);
                invokePreProcessor(top);
                
                // Add children to the stack
                List<AST> children = top.getChildren();
                ListIterator<AST> li = children.listIterator(children.size());
                while(li.hasPrevious()) {
                    AST node = li.previous();
                    if(node != null) analysisStack.push(node);
                }
            }
            // Finish processing node and pop it off of the stack
            else {
                invokePostProcessor(top);
                analysisStack.pop();
            }
        }
        
        // Return true if no errors occurred
        return analysisErrors == 0;
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
    private AST           analysisTop;
    private Set<AST>      analysisGrey;
    private Deque<AST>    analysisStack;
    private Deque<Object> analysisWorking;
    private List<String>  analysisSource;
    private Integer       analysisErrors;
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
