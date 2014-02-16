package compiler488.semantics;

import java.io.*;

import compiler488.symbol.SymbolTable;
import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.MultiDeclarations;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.Scope;
import compiler488.ast.stmt.Stmt;
import compiler488.semantics.Processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Processor {
    String target();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Action {
    int number();
}

/** Implement semantic analysis for compiler 488 
 *  @author Daniel Bloemendal
 */
public class Semantics {
    
    /** flag for tracing semantic analysis */
    private boolean traceSemantics = false;
    /** file sink for semantic analysis trace */
    private String traceFile = new String();
    private SymbolTable symbolTable;
    public FileWriter Tracer;
    public File f;

    /** Maps for processors and actions */
    private Map<String, Method>  processorsMap;
    private Map<Integer, Method> actionsMap;

    /** Analysis state */
    private Object        analysisTop;
    private Stack<Object> analysisStack;
    
    //
    // Processor/action management 
    //
    
    void populateMappings() {
        Class<? extends Semantics> thisClass = this.getClass();
        for(Method method : thisClass.getDeclaredMethods()) {
            Processor procInfo = method.getAnnotation(Processor.class);
            Action    actInfo  = method.getAnnotation(Action.class);
            if(procInfo != null) processorsMap.put(procInfo.target(), method);
            if(actInfo  != null) actionsMap.put(actInfo.number(), method);
        }
    }
    
    boolean invokeProcessor(Object obj) {
        Method m = processorsMap.get(obj.getClass().getSimpleName());
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
                m.invoke(this, analysisTop);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
            
            System.out.println("Semantic Action: S" + actionNumber  );
        }
    }    
        
    //
    // Semantic analysis life cycle
    //

    public Semantics () {
        symbolTable   = new SymbolTable();
        processorsMap = new HashMap<String, Method>();
        actionsMap    = new HashMap<Integer, Method>();
        analysisStack = new Stack<Object>();
        
    }    

    public void Initialize() {
        populateMappings();
    }

    public void Analyze(Program ast) {
        // Add the initial element to the stack
        analysisStack.add(ast);
        
        // Traverse the AST
        while(!analysisStack.empty())
            invokeProcessor(analysisStack.pop());
    }
    
    public void Finalize(){  
    }

    //
    // Processors
    //
    
    @Processor(target = "Program")
    void processProgram(Program program) {
        semanticAction(00);
        processScope(program);
        semanticAction(01);
    }   
    
    @Processor(target = "Scope")
    void processScope(Scope scope) {
        // Add declarations and statements to the stack
        LinkedList<Stmt>          stmts = scope.getStatements().getList();
        LinkedList<Declaration>   decls = scope.getDeclarations().getList();
        ListIterator<Stmt>        si    = stmts.listIterator(stmts.size());
        ListIterator<Declaration> di    = decls.listIterator(decls.size());
        while(si.hasPrevious()) analysisStack.add(si.previous());
        while(di.hasPrevious()) analysisStack.add(di.previous());
    }
    
    @Processor(target = "MultiDeclarations")
    void processMultiDeclarations(MultiDeclarations multiDeclarations) {
    }   
    
    @Processor(target = "Declaration")
    void processDeclaration(Declaration declaration) {
    }
    
    //
    // Actions
    //
    
    @Action(number = 00)
    void actionProgramStart(Program program) {
        symbolTable.scopeEnter(SymbolTable.ScopeType.Program);
    }
    
    @Action(number = 01)
    void actionProgramEnd(Program program) {
        symbolTable.scopeExit();
    }    
}
