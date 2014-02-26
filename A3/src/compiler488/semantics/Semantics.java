package compiler488.semantics;

import java.io.*;

import compiler488.langtypes.FunctionType;
import compiler488.langtypes.LangType;
import compiler488.symbol.FunctionSymbol;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;
import compiler488.symbol.VariableSymbol;
import compiler488.semantics.Errors;
import compiler488.ast.AST;
import compiler488.ast.Callable;
import compiler488.ast.IdentNode;
import compiler488.ast.SourceLoc;
import compiler488.ast.SourceLocPrettyPrinter;
import compiler488.ast.decl.ArrayBound;
import compiler488.ast.decl.ArrayDeclPart;
import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.MultiDeclarations;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.decl.ScalarDecl;
import compiler488.ast.decl.ScalarDeclPart;
import compiler488.ast.expn.BinaryExpn;
import compiler488.ast.expn.BoolConstExpn;
import compiler488.ast.expn.BoolExpn;
import compiler488.ast.expn.ConditionalExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.FunctionCallExpn;
import compiler488.ast.expn.IdentExpn;
import compiler488.ast.expn.IntConstExpn;
import compiler488.ast.expn.NotExpn;
import compiler488.ast.expn.SubsExpn;
import compiler488.ast.expn.UnaryMinusExpn;
import compiler488.ast.expn.VarRefExpn;
import compiler488.ast.stmt.AssignStmt;
import compiler488.ast.stmt.ExitStmt;
import compiler488.ast.stmt.IfStmt;
import compiler488.ast.stmt.LoopingStmt;
import compiler488.ast.stmt.ProcedureCallStmt;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.ResultStmt;
import compiler488.ast.stmt.ReturnStmt;
import compiler488.ast.stmt.Scope;
import compiler488.ast.stmt.Stmt;
import compiler488.ast.stmt.WhileDoStmt;

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
    ////////////////////////////////////////////////////////////////////
    // Processors
    ////////////////////////////////////////////////////////////////////

    /*
     * program:
     *      S00 scope S01
     */
    @PreProcessor(target = "Program")
    void preProgram(Program program) {
        semanticAction(0); // S00: Start program scope.
    }
    @PostProcessor(target = "Program")
    void postProgram(Program program) {
        semanticAction(1); // S01: End program scope.
    }

    /*
     * statement:
     *      S06 scope S07
     */
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
    
    //
    // Declaration processing
    //

    /*
     * declaration:
     *      'var' variablenames ':' type S47
     */
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

    /*
     * variablenames:
     *      variablename S10
     */
    @PostProcessor(target = "ScalarDeclPart")
    void postScalarDeclPart(ScalarDeclPart scalarDeclPart) {
        semanticAction(10); // S10: Declare scalar variable.
    }

    /*
     * parameters:
     *      parametername ':' type S16 S15
     */
    @PostProcessor(target = "ScalarDecl")
    void postScalarDecl(ScalarDecl scalarDecl) {
        if(firstOf(scalarDecl, RoutineDecl.class) != null) {
            semanticAction(16); // S16: Increment parameter count by one.
            semanticAction(15); // S15: Declare parameter with specified type.
        }
    }

    /*
     * variablenames:
     *      variablename ’[’ bound ’]’ S19
     *      variablename ’[’ bound ’,’ bound S46 ’]’ S48
     */
    @PostProcessor(target = "ArrayDeclPart")
    void postArrayDeclPart(ArrayDeclPart arrayDeclPart) {
        semanticAction(46); // S46: Check that lower bound is <= upper bound.
        if(arrayDeclPart.getDimensions() == 1)
            semanticAction(19); // S19: Declare one dimensional array with specified bound.
        else if(arrayDeclPart.getDimensions() == 2)
            semanticAction(48); // S48: Declare two dimensional array with specified bound.
    }

    /*
     * declaration:
     *      functionHead S49 S04 S54 scope S05 S13
     * functionHead:
     *      'func' functionname '(' S14 parameterList ')' ':' type S12
     */    
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
    
    //
    // Statement processing
    //
    
    @PreProcessor(target = "Stmt")
    void preStmt(Stmt stmt) {
        stmt.setRoutine(symbolTable.scopeRoutine());
    }    

    /*
     * variable ':' '=' expression S34
     */
    @PostProcessor(target = "AssignStmt")
    void postAssignStmt(AssignStmt assignStmt) {
        semanticAction(34); // S34: Check that variable and expression in assignment are the same type.
    }
   
    /*
     * 'if' expression S30 'then' statement 'fi' ,
     * 'if' expression S30 'then' statement 'else' statement 'fi'
     */
    @PostProcessor(target = "IfStmt")
    void postIfStmt(IfStmt ifStmt) {
        setTop(ifStmt.getCondition());
        semanticAction(30); // S30: Check that type of expression is boolean.
    }
    
    /*
     * 'while' expression S30 'do' statement 'end'
     */
    @PostProcessor(target = "WhileDoStmt")
    void postWhileDoStmt(WhileDoStmt whileDoStmt) {
        setTop(whileDoStmt.getExpn());
        semanticAction(30); // S30: Check that type of expression is boolean.
    }
    
    /* 
     * 'exit' S50 ,
     * 'exit' 'when' expression S30 S50
     */
    @PostProcessor(target = "ExitStmt")
    void postExitStmt(ExitStmt exitStmt) {
        semanticAction(50); // S50: Check that exit statement is inside a loop.
        if(exitStmt.getCondition() != null) {
            setTop(exitStmt.getCondition());
            semanticAction(30); // S30: Check that type of expression is boolean.
        }
    }

    /*
     * 'result' expression S51 S35
     */
    @PostProcessor(target = "ResultStmt")
    void postResultStmt(ResultStmt resultStmt) {
        semanticAction(51); // S51: Check that result statement is directly inside a function.
        setTop(resultStmt.getValue());
        semanticAction(35); // S35: Check that expression type matches the return type of enclosing function.
    }

    /*
     * 'return' S52
     */
    @PostProcessor(target = "ReturnStmt")
    void postReturnStmt(ReturnStmt returnStmt) {        
        semanticAction(52); // S52: Check that return statement is directly inside a procedure.
    }    
    
    /*
     * procedurename '(' S44 argumentList ')' S43
     * 
     * argumentList:
     *      arguments
     *      % EMPTY
     * arguments:
     *      expression S45 S36 ,
     *      arguments ',' arguments
     */
    @PostProcessor(target = "ProcedureCallStmt")
    void postProcedureCallStmt(Callable procedureCallStmt) {
        semanticAction(41); // S41: Check that identifier has been declared as a procedure.
        setTop(procedureCallStmt.getIdent());
        semanticAction(29); // S29: Check that identifier is visible according to the language scope rule.
        semanticAction(44); // S44: Set the argument count to zero.
        for(int i = 0; i < procedureCallStmt.getArguments().getList().size(); i++) {
            semanticAction(45); // S45: Increment the argument count by one.
            semanticAction(36); // S36: Check that type of argument expression matches type of corresponding formal parameter.
        }
        semanticAction(43); // S43: Check that the number of arguments is equal to the number of formal parameters.
    }
    
    //
    // Expression processing
    //

    /*
     * variablename S26
     * variablename:
     *      identifier S37 S29
     */
    @PostProcessor(target = "IdentExpn")
    void postIdentExpn(IdentExpn identExpn) {
        semanticAction(37); // S37: Check that identifier has been declared as a scalar variable.
        setTop(identExpn.getIdent());
        semanticAction(29); // S29: Check that identifier is visible according to the language scope rule.
        semanticAction(26); // S26: Set result type to type of variablename.
    }

    /*
     * integer S21
     */
    @PostProcessor(target = "IntConstExpn")
    void postIntConstExpn(IntConstExpn intConstExpn) {
        semanticAction(21); // S21: Set result type to integer.
    }

    /*
     * '-' expression S31 S21
     */
    @PostProcessor(target = "UnaryMinusExpn")
    void postUnaryMinusExpn(UnaryMinusExpn unaryMinusExpn) {
        setTop(unaryMinusExpn.getOperand());
        semanticAction(31); // S31: Check that type of expression or variable is integer.
        semanticAction(21); // S21: Set result type to integer.
    }

    /*
     * expression S31 '+' expression S31 S21
     * expression S31 '-' expression S31 S21
     * expression S31 '*' expression S31 S21
     * expression S31 '/' expression S31 S21
     */
    @PostProcessor(target = "ArithExpn")
    void postArithExpn(BinaryExpn binaryExpn) {
        setTop(binaryExpn.getLeft());
        semanticAction(31); // S31: Check that type of expression or variable is integer.
        setTop(binaryExpn.getRight());
        semanticAction(31); // S31: Check that type of expression or variable is integer.
        semanticAction(21); // S21: Set result type to integer.
    }

    /*
     * 'true' S20
     * 'false' S20
     */
    @PostProcessor(target = "BoolConstExpn")
    void postBoolConstExpn(BoolConstExpn boolConstExpn) {
        semanticAction(20); // S20: Set result type to boolean.
    }

    /*
     * 'not' expression S30 S20
     */
    @PostProcessor(target = "NotExpn")
    void postNotExpn(NotExpn notExpn) {
        setTop(notExpn.getOperand());
        semanticAction(30); // S30: Check that type of expression or variable is boolean.
        semanticAction(20); // S20: Set result type to boolean.
    }

    /*
     * expression S30 'and' expression S30 S20 ,
     * expression S30 'or' expression S30 S20 ,
     */
    @PostProcessor(target = "BoolExpn")
    void postBoolExpn(BoolExpn boolExpn) {
        setTop(boolExpn.getLeft());
        semanticAction(30); // S30: Check that type of expression or variable is boolean.
        setTop(boolExpn.getRight());
        semanticAction(30); // S30: Check that type of expression or variable is boolean.
        semanticAction(20); // S20: Set result type to boolean.
    }

    /*
     * expression '=' expression S32 S20
     * expression 'not' '=' expression S32 S20
     */
    @PostProcessor(target = "EqualsExpn")
    void postEqualsExpn(BinaryExpn binaryExpn) {
        semanticAction(32); // S32: Check that left and right operand expressions are the same type.
        semanticAction(20); // S20: Set result type to boolean.
    }

    /*
     * expression S31 '<' expression S31 S20
     * expression S31 '<' '=' expression S31 S20
     * expression S31 '>' expression S31 S20
     * expression S31 '>' '=' expression S31 S20
     */
    @PostProcessor(target = "CompareExpn")
    void postCompareExpn(BinaryExpn binaryExpn) {
        setTop(binaryExpn.getLeft());
        semanticAction(31); // S31: Check that type of expression or variable is integer.
        setTop(binaryExpn.getRight());
        semanticAction(31); // S31: Check that type of expression or variable is integer.
        semanticAction(20); // S20: Set result type to boolean.
    }

    /*
     * '(' expression S30 '?' expression ':' expression S33 ')' S24
     */
    @PostProcessor(target = "ConditionalExpn")
    void postConditionalExpn(ConditionalExpn conditionalExpn) {
        setTop(conditionalExpn.getCondition());
        semanticAction(30); // S30: Check that type of expression or variable is boolean.
        semanticAction(33); // S33: Check that both expressions in conditional are the same type.
        semanticAction(24); // S24: Set result type to type of conditional expressions.
    }

    /*
     * functionname '(' S44 argumentList ')' S43 S28
     * 
     * argumentList:
     *      arguments
     *      % EMPTY
     * arguments:
     *      expression S45 S36 ,
     *      arguments ',' arguments
     */
    @PostProcessor(target = "FunctionCallExpn")
    void postFunctionCallExpn(Callable functionCallExpn) {
        postProcedureCallStmt(functionCallExpn);
        semanticAction(28); // S28: Set result type to result type of function.
    }
    
    /*
     * arrayname '[' expression S31 ']' S38 S29 S27
     * arrayname '[' expression S31 ',' expression S31 ']' S55 S29 S27
     */
    @PostProcessor(target = "SubsExpn")
    void postSubsExpn(SubsExpn subsExpn) {
        // 1D array
        if(subsExpn.getSubscript2() == null) {
            setTop(subsExpn.getSubscript1());
            semanticAction(31); // S31: Check that type of expression or variable is integer.
            semanticAction(38); // S38: Check that arrayname has been declared as a one dimensional array.
            setTop(subsExpn.getIdent());
            semanticAction(29); // S29: Check that identifier is visible according to the language scope rule.
            semanticAction(27); // S27: Set result type to type of array element.
        }
        // 2D array
        else {
            setTop(subsExpn.getSubscript1());
            semanticAction(31); // S31: Check that type of expression or variable is integer.
            setTop(subsExpn.getSubscript2());
            semanticAction(31); // S31: Check that type of expression or variable is integer.
            semanticAction(55); // S55: Check that arrayname has been declared as a two dimensional array
            setTop(subsExpn.getIdent());
            semanticAction(29); // S29: Check that identifier is visible according to the language scope rule.
            semanticAction(27); // S27: Set result type to type of array element.
        }
    }    
    
    ////////////////////////////////////////////////////////////////////
    // Actions
    ////////////////////////////////////////////////////////////////////

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
        symbolTable.scopeEnter(SymbolTable.ScopeType.Wrapper);
        symbolTable.scopeSet(routineDecl.getName(),
                new FunctionSymbol(routineDecl.getName(), routineDecl.getFunctionType()));
        symbolTable.scopeEnter(SymbolTable.ScopeType.Function, routineDecl);
        return true;
    }

    @Action(number = 5) // End function scope.
    Boolean actionFunctionEnd(RoutineDecl routineDecl) {
        symbolTable.scopeExit(); // Exit function scope
        symbolTable.scopeExit(); // Exit wrapper
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
        symbolTable.scopeEnter(SymbolTable.ScopeType.Wrapper);
        symbolTable.scopeSet(routineDecl.getName(),
                new FunctionSymbol(routineDecl.getName(), routineDecl.getFunctionType()));        
        symbolTable.scopeEnter(SymbolTable.ScopeType.Procedure, routineDecl);
        return true;
    }

    @Action(number = 9) // End procedure scope.
    Boolean actionProcedureEnd(RoutineDecl routineDecl) {
        symbolTable.scopeExit(); // Exit procedure scope
        symbolTable.scopeExit(); // Exit wrapper
        return true;
    }

    @Action(number = 10) // Declare scalar variable.
    Boolean actionDeclareScalar(ScalarDeclPart scalarDecl) {
        return workingSet(scalarDecl.getName(),
                new VariableSymbol(scalarDecl.getName()));
    }

    @Action(number = 11) // Declare forward function.
    Boolean actionDeclareForwardFunction(RoutineDecl routineDecl) {
    	setErrorLocation(routineDecl.getIdent());

        return symbolTable.scopeSet(routineDecl.getName(),
                new FunctionSymbol(routineDecl.getName(), routineDecl.getFunctionType(), false));
    }

    @Action(number = 12) // Declare function with parameters ( if any ) and specified type.
    Boolean actionDeclareFunction(RoutineDecl routineDecl) {
        setErrorLocation(routineDecl.getIdent());

        Symbol symbol = symbolTable.find(routineDecl.getName(), false);
        if(symbol == null)
            return workingSet(routineDecl.getName(),
                    new FunctionSymbol(routineDecl.getName(), routineDecl.getFunctionType()));
        else return FunctionSymbol.isForward(symbol);
    }

    @Action(number = 13) // Associate scope with function/procedure.
    Boolean actionAssociateRoutineDeclaration(RoutineDecl routineDecl) {
    	setErrorLocation(routineDecl.getIdent());
    	
        Symbol symbol = symbolTable.find(routineDecl.getName(), false /* allScopes */);
        if(symbol == null)
            return symbolTable.scopeSet(routineDecl.getName(),
                    workingFind(routineDecl.getName(), false /* allScopes */));
        else if(FunctionSymbol.isForward(symbol)) {
            ((FunctionSymbol) symbol).hasBody(true); return true;
        } return false;
    }

    @Action(number = 14) // Set parameter count to zero.
    Boolean actionResetParameterCount(AST node) {
        analysisParams = 0; return true;
    }

    @Action(number = 15) // Declare parameter with specified type.
    Boolean actionDeclareParameter(ScalarDecl scalarDecl) {
        setErrorLocation(scalarDecl.getIdent());
        
        Symbol symbol = new VariableSymbol(scalarDecl.getName());
        symbol.setType(scalarDecl.getLangType());
        return workingSet(scalarDecl.getName(), symbol, true /* newScope */);
    }

    @Action(number = 16) // Increment parameter count by one.
    Boolean actionIncrementParameterCount(AST node) {
        analysisParams += 1; return true;
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
    	setErrorLocation(arrayDecl.getIdent());
    	
    	ArrayBound b1 = arrayDecl.getBound1();
    	Symbol symbol = new VariableSymbol(arrayDecl.getName(),
                b1.getLowerboundValue(), b1.getUpperboundValue());
        return workingSet(arrayDecl.getName(), symbol);
    }

    @Action(number = 20) // Set result type to boolean.
    Boolean actionSetToBoolean(Expn expr) {
        expr.setEvalType(LangType.TYPE_BOOLEAN); return true;
    }

    @Action(number = 21) // Set result type to integer.
    Boolean actionSetToInteger(Expn expr) {
        expr.setEvalType(LangType.TYPE_INTEGER); return true;
    }

    @Action(number = 24) // Set result type to type of conditional expressions.
    Boolean actionSetToConditional(ConditionalExpn conditionalExpn) {
        conditionalExpn.setEvalType(conditionalExpn.getTrueValue().getEvalType()); return true;
    }

    @Action(number = 26) // Set result type to type of variablename.
    Boolean actionSetToVariable(VarRefExpn varRefExpn) {
        Symbol symbol = symbolTable.find(varRefExpn.getIdent().getId());
        
        // If variable not declared
        if(symbol == null) {
            varRefExpn.setEvalType(LangType.TYPE_ERROR);
            return false;
        }
        // Otherwise evaluation type is identifier's declared type 
        else { 
            varRefExpn.setEvalType(symbol.getType());
            return true;
        }
    }
    
    @Action(number = 27) // Set result type to type of array element.
    Boolean actionSetToArray(SubsExpn subsExpn) {
        return actionSetToVariable(subsExpn);
    }

    @Action(number = 28) // Set result type to result type of function.
    Boolean actionSetToFunction(FunctionCallExpn functionCallExpn) {
        Symbol symbol = symbolTable.find(functionCallExpn.getIdent().getId());
        LangType type = symbol.getType();
        
        // If identifier is not declared or is not a function
        if(symbol == null || !symbol.isRoutine()) {
            functionCallExpn.setEvalType(LangType.TYPE_ERROR);
            return false;
        }
        // Otherwise set evaluation type to function return type
        else {
            functionCallExpn.setEvalType(((FunctionType) type).getReturnType());
            return true;
        }
    }

    @Action(number = 29) // Check that identifier is visible according to the language scope rule.
    Boolean actionCheckIdentifer(IdentNode ident) {
        Symbol symbol = symbolTable.find(ident.getId());
        return symbol != null;
    }

    @Action(number = 30) // Check that type of expression or variable is boolean.
    Boolean actionTypeCheckBoolean(Expn expn) {
        LangType type = expn.getEvalType();
        return type.isBoolean() || type.isError();
    }

    @Action(number = 31) // Check that type of expression or variable is integer.
    Boolean actionTypeCheckInteger(Expn expn) {
        LangType type = expn.getEvalType();
        return type.isInteger() || type.isError();
    }

    @Action(number = 32) // Check that left and right operand expressions are the same type.
    Boolean actionCheckEqualitySides(BinaryExpn binaryExpn) {
        LangType left    = binaryExpn.getLeft().getEvalType(),
                 right   = binaryExpn.getRight().getEvalType();
        LangType unified = LangType.unifyTypes(left, right);
        return left.equals(right) || unified.equals(LangType.TYPE_ERROR);
    }

    @Action(number = 33) // Check that both expressions in conditional are the same type.
    Boolean actionCheckConditionalSides(ConditionalExpn conditionalExpn) {
        LangType left    = conditionalExpn.getTrueValue().getEvalType(),
                 right   = conditionalExpn.getFalseValue().getEvalType();
        LangType unified = LangType.unifyTypes(left, right);
        return left.equals(right) || unified.equals(LangType.TYPE_ERROR);
    }

    @Action(number = 34) // Check that variable and expression in assignment are the same type.
    Boolean actionCheckAssignmentTypes(AssignStmt assignStmt) {
        VarRefExpn left = assignStmt.getLval();
        Symbol leftSymbol = symbolTable.find(left.getIdent().getId());
        if (leftSymbol == null) return false;
        return leftSymbol.getType().equals(assignStmt.getRval().getEvalType());
    }
    
    @Action(number = 35) // Check that expression type matches the return type of enclosing function.
    Boolean actionCheckReturnType(Expn expn) { 
        RoutineDecl routine = firstOf(expn, Stmt.class).getRoutine();
        if(routine == null || !routine.isFunction()) return true; // Ignore: already fails S52
        LangType type = expn.getEvalType();
        return type.equals(routine.getReturnType()) || type.equals(LangType.TYPE_ERROR);
    }
    
    @Action(number = 36) // Check that type of argument expression matches type of corresponding formal parameter.
    Boolean actionCheckArgument(Callable callable) {
        // Get zero based index
        int argumentIndex = analysisArgs - 1;
        
        // Get the identifier and argument
        IdentNode ident = callable.getIdent();
        Expn argument = callable.getArguments().getList().get(argumentIndex);
        setErrorLocation(argument);
        
        // Attempt to find the function symbol
        Symbol symbol = symbolTable.find(ident.getId());
        if(!symbol.isRoutine()) return true; // Ignore: already fails S40/S41
       
        // Verify number of arguments        
        FunctionType funcType = (FunctionType) ((FunctionSymbol) symbol).getType();
        if(analysisArgs < funcType.getArguments().size()) return true; // Ignore: already fails S43
        return argument.getEvalType().equals(funcType.getArguments().get(argumentIndex))
            || argument.getEvalType().equals(LangType.TYPE_ERROR);
    }

    @Action(number = 37) // Check that identifier has been declared as a scalar variable.
    Boolean actionCheckIdentExpn(IdentExpn identExpn) {
        // Find the symbol
        Symbol symbol = symbolTable.find(identExpn.getIdent().getId());
        // Verify result
        return (symbol != null
             && symbol.isVariable()
             && ((VariableSymbol) symbol).getDimensions() == 0);
    }
    
    @Action(number = 38) // Check that arrayname has been declared as a one dimensional array.
    Boolean actionCheckArray1D(SubsExpn subsExpn) {
        // Find the symbol
        Symbol symbol = symbolTable.find(subsExpn.getIdent().getId());
        // Verify result
        return (symbol != null
             && symbol.isVariable()
             && ((VariableSymbol) symbol).getDimensions() == 1);
    }

    @Action(number = 40) // Check that identifier has been declared as a function.
    Boolean actionCheckFunctionCallExpn(FunctionCallExpn functionCallExpn) {
        // Find and check the symbol
        Symbol symbol = symbolTable.find(functionCallExpn.getIdent().getId());
        return FunctionSymbol.isFunction(symbol);
    }

    @Action(number = 41) // Check that identifier has been declared as a procedure.
    Boolean actionCheckProcedureCallStmt(ProcedureCallStmt procedureCallStmt) {
        // Find the symbol
        Symbol symbol = symbolTable.find(procedureCallStmt.getIdent().getId());
        // Verify result
        return (symbol != null
             && symbol.isRoutine()
             && !FunctionSymbol.isFunction(symbol));
    }
    
    @Action(number = 43) // Check that the number of arguments is equal to the number of formal parameters. 
    Boolean actionCheckArgumentCount(Callable callable) {
        // Attempt to find the function symbol
        IdentNode ident = callable.getIdent();
        Symbol symbol = symbolTable.find(ident.getId());
        if(!symbol.isRoutine()) return true; // Ignore: already fails S40/S41
       
        // Verify number of arguments        
        FunctionType funcType = (FunctionType) ((FunctionSymbol) symbol).getType();
        return analysisArgs <= funcType.getArguments().size();
    }

    @Action(number = 44) // Set the argument count to zero. 
    Boolean actionResetArgumentsCount(AST node) {
        analysisArgs = 0; return true;
    }
    
    @Action(number = 45) // Increment the argument count by one.
    Boolean actionIncrementArgumentsCount(AST node) {
        analysisArgs += 1; return true;
    }    
    
    @Action(number = 46) // Check that lower bound is <= upper bound.
    Boolean actionCheckArrayBounds(ArrayDeclPart arrayDecl) {
        ArrayBound b1 = arrayDecl.getBound1(),
                   b2 = arrayDecl.getBound2();
        if (arrayDecl.getDimensions() >= 1 && b1.getLowerboundValue() > b1.getUpperboundValue()) {
            setErrorLocation(b1); return false;
        }
        if (arrayDecl.getDimensions() >= 2 && b2.getLowerboundValue() > b2.getUpperboundValue()) {
            setErrorLocation(b2); return false;
        }
        return true;
    }

    @Action(number = 47) // Associate type with variables.
    Boolean actionAssociateTypeWithVar(Declaration declaration) {
        for(Entry<String, Symbol> entry : workingEntries())
            ((VariableSymbol) entry.getValue()).setType(declaration.getLangType());
        return true;
    }

    @Action(number = 48) // Declare two dimensional array with specified bound.
    Boolean actionDeclareArray2D(ArrayDeclPart arrayDecl) {
        setErrorLocation(arrayDecl.getIdent());

    	ArrayBound b1 = arrayDecl.getBound1();
    	ArrayBound b2 = arrayDecl.getBound2();
    	Symbol symbol = new VariableSymbol(arrayDecl.getName(),
                b1.getLowerboundValue(), b1.getUpperboundValue(),
                b2.getLowerboundValue(), b2.getUpperboundValue());
        return workingSet(arrayDecl.getName(), symbol);
    }

    @Action(number = 49) // If function/procedure was declared forward, verify forward declaration matches.
    Boolean actionCheckRoutineDeclaration(RoutineDecl routineDecl) {
        // Attempt to find a function symbol with the given routine's name
        Symbol symbol = symbolTable.find(routineDecl.getName(), false);
        if(!(symbol instanceof FunctionSymbol)) return true;

        // Verify that it is a function symbol and the type matches
        return ((FunctionSymbol) symbol).getType().equals(routineDecl.getFunctionType());
    }

    @Action(number = 50) // Check that exit statement is inside a loop.
    Boolean actionCheckExit(ExitStmt exitStmt) {
        LoopingStmt loop = firstOf(exitStmt, LoopingStmt.class);
        return loop != null;
    }

    @Action(number = 51) // Check that result statement is directly inside a function.
    Boolean actionCheckResult(ResultStmt returnStmt) {
        RoutineDecl routine = returnStmt.getRoutine();
        return (routine != null && routine.isFunction());
    }

    @Action(number = 52) // Check that return statement is directly inside a procedure.
    Boolean actionCheckReturn(ReturnStmt returnStmt) {
        RoutineDecl routine = returnStmt.getRoutine();
        return (routine != null && !routine.isFunction());

    }

    @Action(number = 54) // Associate parameters if any with scope.
    Boolean actionAssociateParameters(Scope scope) {
        for(Entry<String, Symbol> entry: workingEntries()) {
            if(!(entry.getValue() instanceof VariableSymbol)
            || !symbolTable.scopeSet(entry.getKey(), entry.getValue())) return false;
        } return true;
    }
    
    @Action(number = 55) // Check that arrayname has been declared as a two dimensional array.
    Boolean actionCheckArray2D(SubsExpn subsExpn) {
        // Find the symbol
        Symbol symbol = symbolTable.find(subsExpn.getIdent().getId());
        // Verify result
        return (symbol != null
             && symbol.isVariable()
             && ((VariableSymbol) symbol).getDimensions() == 2);
    }
    
    ////////////////////////////////////////////////////////////////////
    // Machinery
    ////////////////////////////////////////////////////////////////////

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

    @SuppressWarnings("unchecked")
    static <T> T firstOf(AST obj, Class<T> type) {
        for(AST node = obj; node != null; node = node.getParent())
            if(type.isInstance(node)) return (T) node;
        return null;
    }
    
    void setTop(AST node) {
        analysisSubTop = node;
    }
    
    void setErrorLocation(AST node) {
        analysisErrorLoc = node;
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
        // Get class tree
        Deque<Class<?>> classes = new LinkedList<Class<?>>();
        for(Class<?> cls = obj.getClass(); !cls.equals(Object.class); cls = cls.getSuperclass())
            classes.push(cls);
        // Loop over classes
        while(!classes.isEmpty()) {
            Class<?> cls = classes.pop();
            Method m = map.get(cls.getSimpleName());
            if(m == null) continue;
            analysisTop = obj;
            analysisSubTop = null;
    
            // Invoke the processor on object
            try { m.invoke(this, obj); }
            catch (IllegalAccessException e)    { e.printStackTrace(); return false; }
            catch (IllegalArgumentException e)  { e.printStackTrace(); return false; }
            catch (InvocationTargetException e) { e.printStackTrace(); return false; }            
        } return true;
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
                AST node = (analysisSubTop == null) ? analysisTop : analysisSubTop;
                setErrorLocation(node); // Set default error location to top node.
                Boolean result = (Boolean) m.invoke(this, node);
                analysisSubTop = null;

                if (result) {
                    System.out.println("Semantic Action: S" + actionNumber);
                } else {
                    String errorMessage = Errors.getError(actionNumber);
                    if(errorMessage == null) errorMessage = "Semantic Error S" + actionNumber;
                    else errorMessage = "S" + actionNumber + ": " + errorMessage;
                    SourceLocPrettyPrinter pp = new SourceLocPrettyPrinter(System.out, analysisSource, analysisErrorLoc);
                    System.out.println(pp.getFileRef() + ": " + errorMessage);
                    pp.print();
                    analysisErrors += 1;
                }
            }
            catch (IllegalAccessException e)    {
                System.out.println("Illegal access occured during action S" + actionNumber + ": "); e.printStackTrace(); }
            catch (IllegalArgumentException e)  {
                System.out.println("Illegal argument passed to action S" + actionNumber + ": "); e.printStackTrace(); }
            catch (InvocationTargetException e) {
                System.out.println("Exception occurred while executing action S" + actionNumber + ": "); e.printStackTrace(); }
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
    private AST           analysisTop;      // Top AST node
    private AST           analysisSubTop;   // Sub-top AST node
    private Set<AST>      analysisGrey;     // Seen AST nodes
    private Deque<AST>    analysisStack;    // AST node stack
    private Deque<Object> analysisWorking;  // Working symbols
    private Integer       analysisParams;   // Parameter count
    private Integer       analysisArgs;     // Argument count
    private List<String>  analysisSource;   // Original source listing 
    private Integer       analysisErrors;   // Count of errors during semantic analysis
    private SourceLoc     analysisErrorLoc; // Location in source where an error occurred
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
