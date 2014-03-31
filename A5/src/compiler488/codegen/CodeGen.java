package compiler488.codegen;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import compiler488.ast.AST;
import compiler488.ast.Readable;
import compiler488.ast.Printable;
import compiler488.ast.SourceLocPrettyPrinter;
import compiler488.ast.decl.ArrayBound;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.expn.ArithExpn;
import compiler488.ast.expn.BoolConstExpn;
import compiler488.ast.expn.BoolExpn;
import compiler488.ast.expn.CompareExpn;
import compiler488.ast.expn.ConditionalExpn;
import compiler488.ast.expn.ConstExpn;
import compiler488.ast.expn.EqualsExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.FunctionCallExpn;
import compiler488.ast.expn.IdentExpn;
import compiler488.ast.expn.IntConstExpn;
import compiler488.ast.expn.NewlineConstExpn;
import compiler488.ast.expn.NotExpn;
import compiler488.ast.expn.SubsExpn;
import compiler488.ast.expn.TextConstExpn;
import compiler488.ast.expn.UnaryMinusExpn;
import compiler488.ast.expn.VarRefExpn;
import compiler488.ast.stmt.AssignStmt;
import compiler488.ast.stmt.GetStmt;
import compiler488.ast.stmt.IfStmt;
import compiler488.ast.stmt.ExitStmt;
import compiler488.ast.stmt.WhileDoStmt;
import compiler488.ast.stmt.RepeatUntilStmt;
import compiler488.ast.stmt.ProcedureCallStmt;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.PutStmt;
import compiler488.ast.stmt.ResultStmt;
import compiler488.ast.stmt.ReturnStmt;
import compiler488.ast.stmt.Scope;
import compiler488.runtime.Machine;
import compiler488.compiler.Main;
import compiler488.codegen.visitor.Visitor;
import compiler488.codegen.visitor.Processor;
import compiler488.highlighting.AssemblyHighlighting;

/**
 * Code generator for compiler 488
 *
 * @author Daniel Bloemendal
 * @author Oren Watson
 */
public class CodeGen extends Visitor
{
    //
    // Configuration
    //

    public static final int LANGUAGE_MAX_STRING_LENGTH = 255;

    //
    // Scope processing
    //

    void majorProlog() {
        // Fetch enclosing routine
        RoutineDecl routine = (RoutineDecl) table.currentScope().getParent();

        // Add total locals size to the sizes table
        sizes.put(table.currentScope(), table.getLocalsSize());

        if(routine != null) {
            comment("------------------------------------");
            comment("Start of " + routine.getName());
            comment("------------------------------------");
            // Starting label for routine
            label(table.getRoutineLabel(false));
        }

        emit("SAVECTX", table.getLevel());   // Scope prolog
        reserve(table.getFrameLocalsSize()); // Reserve memory for locals
    }

    void majorEpilog() {
        // Fetch enclosing routine
        RoutineDecl routine = (RoutineDecl) table.currentScope().getParent();

        if(routine != null)                           // Routine ending label
            label(table.getRoutineLabel(true));       // ...
        free(table.getFrameLocalsSize());             // Free locals
        emit("RESTORECTX", table.getLevel(),          // Restore context
                           table.getArgumentsSize()); // ...
        if(routine != null) emit("BR");               // Return from routine
        else                emit("HALT");             // Halt machine

        if(routine != null) {
            comment("------------------------------------");
            comment("End of " + routine.getName());
            comment("------------------------------------");
            assemblerPrintln("");
        }
    }

    @Processor(target="Scope")
    void processScope(Scope scope) throws CodeGenException {
        // Enter the scope
        table.enterScope(scope);

        // Major prolog
        if(table.inMajorScope()) majorProlog();

        // Visit statements in scope
        visit(scope.getStatements());

        // Process routine declarations
        if(table.getRoutineCount() > 0 ) {
            String _end = table.getLabel();
            emit("JMP", _end); emit("");
            visit(scope.getDeclarations());
            label(_end);
        }

        // Major epilog
        if(table.inMajorScope()) majorEpilog();

        // Exit the scope
        table.exitScope();
    }

    @Processor(target="RoutineDecl")
    void processRoutineDecl(RoutineDecl routineDecl) {
        if(!routineDecl.isForward())
            visit(routineDecl.getBody()); // Visit routine body
    }

    //
    // Expression processing
    //

    @Processor(target="ArithExpn")
    void processArithExpn(ArithExpn arithExpn) {
        visit(arithExpn.getLeft());  // Evaluate left side
        visit(arithExpn.getRight()); // Evaluate right side
        switch(arithExpn.getOpSymbol().charAt(0)) {
        case '+': emit("ADD"); break;
        case '-': emit("SUB"); break;
        case '*': emit("MUL"); break;
        case '/': emit("DIV"); break;
        }
    }

    @Processor(target="BoolConstExpn")
    void processBoolConstExpn(BoolConstExpn boolConstExpn) {
        emit("PUSH", boolConstExpn.getValue()); // Push the constant literal
    }

    @Processor(target="BoolExpn")
    void processBoolExpn(BoolExpn boolExpn) {
        if(boolExpn.getOpSymbol().equals(BoolExpn.OP_OR))
            processBoolOrExpn(boolExpn);
        else if(boolExpn.getOpSymbol().equals(BoolExpn.OP_AND))
            processBoolAndExpn(boolExpn);
    }

    @Processor(target="CompareExpn")
    void processCompareExpn(CompareExpn compareExpn) {
        visit(compareExpn.getLeft());  // Evaluate left side
        visit(compareExpn.getRight()); // Evaluate right side
        if(compareExpn.getOpSymbol().equals(CompareExpn.OP_LESS))
            { emit("LT"); }
        else if(compareExpn.getOpSymbol().equals(CompareExpn.OP_LESS_EQUAL))
            { emit("SWAP"); emit ("LT"); emit("NOT"); }
        else if(compareExpn.getOpSymbol().equals(CompareExpn.OP_GREATER))
            { emit("SWAP"); emit("LT"); }
        else if(compareExpn.getOpSymbol().equals(CompareExpn.OP_GREATER_EQUAL))
            { emit("LT"); emit("NOT"); }
    }

    @Processor(target="ConditionalExpn")
    void processConditionalExpn(ConditionalExpn conditionalExpn) {
        // Generate unique labels for branch targets
        String _false = table.getLabel();
        String _end = table.getLabel();

        // Condition
        visit(conditionalExpn.getCondition());  // Evaluate condition
        emit("BFALSE", _false);                 // If false jump to false expression
        visit(conditionalExpn.getTrueValue());  // Otherwise evaluate true expression
        emit("JMP", _end);                      // Jump to end of block

        // False expression
        label(_false);
        visit(conditionalExpn.getFalseValue()); // Evaluate ``falseValue'' expression

        // End of block
        label(_end);
    }

    @Processor(target="EqualsExpn")
    void processEqualsExpn(EqualsExpn equalsExpn) {
        visit(equalsExpn.getLeft());  // Evaluate left side
        visit(equalsExpn.getRight()); // Evaluate right side
        if(equalsExpn.getOpSymbol().equals(EqualsExpn.OP_EQUAL))
            { emit("EQ"); }
        else if(equalsExpn.getOpSymbol().equals(EqualsExpn.OP_NOT_EQUAL))
            { emit("EQ"); emit("NOT"); }
    }

    @Processor(target="FunctionCallExpn")
    void processFunctionCallExpn(FunctionCallExpn functionCallExpn) {
        // Generate labels required for call
        String _func = table.getLabel(functionCallExpn.getName());
        String _end = table.getLabel();

        // Setup the call
        emit("SETUPCALL", _end);
        // Evaluate each argument
        for(Expn expn : functionCallExpn.getArguments().getList())
            visit(expn);
        // Call the function
        emit("JMP", _func);
        label(_end);
    }

    void addressIdentExpn(IdentExpn identExpn) {
        Variable var = table.getVaraible(identExpn.getName());
        if(var == null) throw new RuntimeException("identifier does not match any variable declaration");
        emit("ADDR", var.getLevel(), var.getOffset());
    }

    @Processor(target="IdentExpn")
    void processIdentExpn(IdentExpn identExpn) {
        addressIdentExpn(identExpn);
        emit("LOAD");
    }

    @Processor(target="IntConstExpn")
    void processIntConstExpn(IntConstExpn intConstExpn) throws CodeGenException {
        if(intConstExpn.getValue() < Machine.MIN_INTEGER)
            throw new CodeGenException(source, intConstExpn,
                    "integer constant below machine minimum of " + Machine.MIN_INTEGER);
        if(intConstExpn.getValue() > Machine.MAX_INTEGER)
            throw new CodeGenException(source, intConstExpn,
                    "integer constant above machine maximum of " + Machine.MAX_INTEGER);
        emit("PUSH", intConstExpn.getValue()); // Push the constant literal
    }

    @Processor(target="NotExpn")
    void processNotExpn(NotExpn notExpn) {
        visit(notExpn.getOperand()); // Evaluate expression
        emit("PUSH", "$false");      // Negate value by comparing with ``false''
        emit("EQ");                  // ...
    }

    void addressSubsExpnChecking(SubsExpn subsExpn) {
        // Fetch address of the array
        Variable var = table.getVaraible(subsExpn.getName());
        if(var == null) throw new RuntimeException("identifier does not match any variable declaration");
        emit("ADDR", var.getLevel(), var.getOffset());

        // Fetch bounds and check sanity
        ArrayBound bound1 = var.getBounds().size() > 0 ? var.getBounds().get(0) : null;
        ArrayBound bound2 = var.getBounds().size() > 1 ? var.getBounds().get(1) : null;
        if(bound1 == null && bound2 == null) throw new RuntimeException("array with no bounds");

        // Generate labels
        String _error_lower1 = table.getLabel();
        String _error_upper1 = table.getLabel();
        String _error_lower2 = table.getLabel();
        String _error_upper2 = table.getLabel();
        String _check_upper1 = table.getLabel();
        String _check_upper2 = table.getLabel();
        String _ready_sub1 = table.getLabel();
        String _ready_sub2 = table.getLabel();
        String _end = table.getLabel();

        // Compute the stride of the array
        int stride = bound1.getUpperboundValue() - bound1.getLowerboundValue();

        visit(subsExpn.getSubscript1());               // Evaluate subscript_1
        emit("DUP");                                   // Create duplicate, for bounds checking
        emit("PUSH", bound1.getLowerboundValue());     // Lower bound on stack
        emit("LT");                                    // Is subscript_1 less than the lower bound?
        emit("BFALSE", _check_upper1);                 // No, then head to the next phase
        emit("JMP", _error_lower1);                    // Yes, then jump to error handler

        label(_check_upper1);
        emit("DUP");                                   // Another duplicate of subscript_1
        emit("PUSH", bound1.getUpperboundValue());     // Upper bound on stack
        emit("SWAP");                                  // Is subscript_1 greater than the upper bound?
        emit("LT");                                    // ...
        emit("BFALSE", _ready_sub1);                   // No, then head to the next phase
        emit("JMP", _error_upper1);                    // Yes, then jump to the error handler.

        label(_ready_sub1);
        emit("PUSH", bound1.getLowerboundValue());     // subscript_1 - lower_bound_1
        emit("SUB");                                   // ...

        // If array is 1D
        if(bound2 == null) {
            emit("ADD");                               // Add subscript to base address
            emit("JMP", _end);                         // Jump to end
        }
        // If array is 2D
        else {
            emit("PUSH", stride);                      // Multiply by stride
            emit("MUL");                               // ...
            emit("ADD");                               // Add to base address

            visit(subsExpn.getSubscript2());           // Evaluate subscript_2
            emit("DUP");                               // Create duplicate, for bounds checking
            emit("PUSH", bound2.getLowerboundValue()); // Lower bound on stack
            emit("LT");                                // Is subscript_2 less than the lower bound?
            emit("BFALSE", _check_upper2);             // No, then head to the next phase
            emit("JMP", _error_lower2);                // Yes, then jump to error handler

            label(_check_upper2);
            emit("DUP");                               // Another duplicate of subscript_2
            emit("PUSH", bound2.getUpperboundValue()); // Upper bound on stack
            emit("SWAP");                              // Is subscript_2 greater than the upper bound?
            emit("LT");                                // ...
            emit("BFALSE", _ready_sub2);               // No, then head to the next phase
            emit("JMP", _error_upper2);                // Yes, then jump to the error handler.

            label(_ready_sub2);
            emit("PUSH", bound2.getLowerboundValue()); // subscript_2 - lower_bound_2
            emit("SUB");
            emit("ADD");                               // Add computed array offset to base address
            emit("JMP", _end);                         // Jump to end
        }

        //
        // Error handling
        //

        // Enhanced bounds checking with pretty location information
        if(checking == BoundsChecking.Enhanced) {
            // Pretty printed locations of error
            String locSub1 = new String(), locSub2 = new String();
            if(bound1 != null) locSub1 = SourceLocPrettyPrinter.printToString(source, subsExpn.getSubscript1()).replace('"', '\'');
            if(bound2 != null) locSub2 = SourceLocPrettyPrinter.printToString(source, subsExpn.getSubscript2()).replace('"', '\'');

            // Handler for subscript_1 < lowerbound_1 & subscript_1 > upperbound_1
            label(_error_lower1);
            label(_error_upper1);
            put(subsExpn.getLoc().toString()); put(": subscript out of range"); newline();
            put(locSub1);
            emit("HALT");

            if(bound2 != null) {
                // Handler for subscript_2 < lowerbound_2 & subscript_2 > upperbound_2
                label(_error_lower2);
                label(_error_upper2);
                put(subsExpn.getLoc().toString()); put(": subscript out of range"); newline();
                put(locSub2);
                emit("HALT");
            }
        // Bounds checking with less detailed information
        } else {
            label(_error_lower1);
            label(_error_upper1);
            label(_error_lower2);
            label(_error_upper2);
            put("Error: subscript out of range for array "); put(subsExpn.getName());
            put(" on line "); put(subsExpn.getLoc().getStartLine() + 1); newline();
            emit("HALT");
        }

        // End of block
        label(_end);
    }

    void addressSubsExpn(SubsExpn subsExpn) {
        // See if we need to perform bounds checking
        if(checking != BoundsChecking.None) { addressSubsExpnChecking(subsExpn); return; }

        // Fetch address of the array
        Variable var = table.getVaraible(subsExpn.getName());
        if(var == null) throw new RuntimeException("identifier does not match any variable declaration");
        emit("ADDR", var.getLevel(), var.getOffset());

        // Fetch bounds and check sanity
        ArrayBound bound1 = var.getBounds().size() > 0 ? var.getBounds().get(0) : null;
        ArrayBound bound2 = var.getBounds().size() > 1 ? var.getBounds().get(1) : null;
        if(bound1 == null && bound2 == null) throw new RuntimeException("array with no bounds");

        // Generate labels
        String _end = table.getLabel();

        // Compute the stride of the array
        int stride = bound1.getUpperboundValue() - bound1.getLowerboundValue();

        visit(subsExpn.getSubscript1());               // Evaluate subscript_1
        emit("PUSH", bound1.getLowerboundValue());     // subscript_1 - lower_bound_1
        emit("SUB");                                   // ...

        // If array is 1D
        if(bound2 == null) {
            emit("ADD");                               // Add subscript to base address
            emit("JMP", _end);                         // Jump to end
        }
        // If array is 2D
        else {
            emit("PUSH", stride);                      // Multiply by stride
            emit("MUL");                               // ...
            emit("ADD");                               // Add to base address
            visit(subsExpn.getSubscript2());           // Evaluate subscript_2
            emit("PUSH", bound2.getLowerboundValue()); // subscript_2 - lower_bound_2
            emit("SUB");
            emit("ADD");                               // Add computed array offset to base address
            emit("JMP", _end);                         // Jump to end
        }

        // End of block
        label(_end);
    }

    @Processor(target="SubsExpn")
    void processSubsExpn(SubsExpn subsExpn) {
        addressSubsExpn(subsExpn);
        emit("LOAD");
    }

    @Processor(target="TextConstExpn")
    void processTextConstExpn(TextConstExpn textConstExpn) throws CodeGenException {
        if(textConstExpn.getValue().length() > LANGUAGE_MAX_STRING_LENGTH)
            throw new CodeGenException(source, textConstExpn,
                    "string exceeds maximum allowable length of " + LANGUAGE_MAX_STRING_LENGTH + " characters");
    }

    @Processor(target="UnaryMinusExpn")
    void processUnaryMinusExpn(UnaryMinusExpn unaryMinusExpn) {
        visit(unaryMinusExpn.getOperand()); // Evaluate expression
        emit("NEG");                        // Negate the value
    }

    void addressVarRefExpn(VarRefExpn varRefExpn) {
        if(varRefExpn instanceof IdentExpn)
            addressIdentExpn((IdentExpn) varRefExpn);
        else if(varRefExpn instanceof SubsExpn)
            addressSubsExpn((SubsExpn) varRefExpn);
        else throw new RuntimeException("unknown variable reference type");
    }

    //
    // Short circuited boolean expressions
    //

    void processBoolOrExpn(BoolExpn boolExpn) {
        // Generate unique labels for branch targets
        String _checkRHS = table.getLabel();
        String _end = table.getLabel();

        // Left side check
        visit(boolExpn.getLeft());  // Evaluate left hand side
        emit("DUP");                // Duplicate result, required for return value
        emit("BFALSE", _checkRHS);  // If false try the right hand side
        emit("JMP", _end);          // Otherwise, since it is true short circuit to end

        // Right side check
        label(_checkRHS);
        emit("POP");                // Get rid of duplicated result from left hand side
        visit(boolExpn.getRight()); // Evaluate right hand side

        // End of block
        label(_end);
    }

    void processBoolAndExpn(BoolExpn boolExpn) {
        // Generate unique labels for branch targets
        String _end = table.getLabel();

        // Left side check
        visit(boolExpn.getLeft());  // Evaluate left hand side
        emit("DUP");                // Duplicate result, required for return value
        emit("BFALSE", _end);       // If false short circuit to the end

        // Right side check
        emit("POP");                // Get rid of duplicated result from left hand side
        visit(boolExpn.getRight()); // Evaluate right hand side

        // End of block
        label(_end);
    }

    //
    // Statement processing
    //

    @Processor(target="AssignStmt")
    void processAssignStmt(AssignStmt assignStmt) {
        addressVarRefExpn(assignStmt.getLval()); // Fetch address of left side
        visit(assignStmt.getRval());             // Evaluate the right side expression
        emit("STORE");                           // Store the value of the expression in the left side variable
    }

    @Processor(target="ExitStmt")
    void processExitStmt(ExitStmt exitStmt) {
        if(exitStmt.getCondition() == null)
            emit("JMP", loopExitLabel);
        else {
            visit(exitStmt.getCondition());
            emit("NOT");
            emit("BFALSE", loopExitLabel);
        }
    }

    @Processor(target="GetStmt")
    void processGetStmt(GetStmt getStmt) {
        for(Readable readable : getStmt.getInputs().getList()) {
            // Skip any readable that is not a variable reference
            if(!(readable instanceof VarRefExpn)) continue;

            // Push address of variable on to stack
            addressVarRefExpn((VarRefExpn) readable);

            // Perform the read and store the result
            emit("READI");
            emit("STORE");
        }
    }

    @Processor(target="IfStmt")
    void processIfStmt(IfStmt ifStmt) {
        // Generate unique labels for branch targets
        String _else = table.getLabel();
        String _end = table.getLabel();

        // If then clause
        visit(ifStmt.getCondition()); // Evaluate condition of the if statement
        emit("BFALSE", _else);        // Branch to else statement if false
        visit(ifStmt.getWhenTrue());  // Execute ``when true'' statements
        emit("JMP", _end);            // Jump to the end of the if statement

        // Else clause
        label(_else);
        // If a ``when false'' clause exists, execute the statements
        if(ifStmt.getWhenFalse() != null)
            visit(ifStmt.getWhenFalse());

        // End of block
        label(_end);
    }

    @Processor(target="ProcedureCallStmt")
    void processProcedureCallStmt(ProcedureCallStmt procedureCallStmt) {
        // Generate labels required for call
        String _proc = table.getLabel(procedureCallStmt.getName());
        String _end = table.getLabel();

        // Setup the call
        emit("SETUPCALL", _end);
        // Evaluate each argument
        for(Expn expn : procedureCallStmt.getArguments().getList())
            visit(expn);
        // Call the procedure
        emit("JMP", _proc);
        label(_end);
        // Pop off the unused result
        emit("POP");
    }

    @Processor(target="PutStmt")
    void processPutStmt(PutStmt putStmt) {
        for(Printable p : putStmt.getOutputs().getList()) {
            // Visit the printable
            if(p instanceof ConstExpn) visit((ConstExpn) p);
            // Process the printable
            if(p instanceof TextConstExpn)
                put(((TextConstExpn) p).getValue()); // String printable
            else if(p instanceof NewlineConstExpn)
                newline();                           // Newline printable
            else if(p instanceof Expn)
                { visit((Expn) p); emit("PRINTI"); } // Expression printable
            else throw new RuntimeException("unknown printable");
        }
    }

    @Processor(target="RepeatUntilStmt")
    void processRepeatUntilStmt(RepeatUntilStmt repeatUntilStmt) {
        // Preserve the previous loop exit and generate a new one
        String prevLoopExitLabel = loopExitLabel;
        loopExitLabel = table.getLabel();

        // Generate unique labels for branch targets
        String _start = table.getLabel();
        String _end = loopExitLabel;
        // If then clause
        label(_start);
        visit(repeatUntilStmt.getBody());
        visit(repeatUntilStmt.getExpn()); // Evaluate condition of the while statement
        emit("BFALSE", _start);           // Branch to end if false
        label(_end);


        // Restore old loop exit label
        loopExitLabel = prevLoopExitLabel;
    }

    @Processor(target="ResultStmt")
    void processResultStmt(ResultStmt resultStmt) {
        // Get routine end label
        String _end = table.getLabel(table.getRoutine().getName(), true);
        // Get the address of the result
        emit("ADDR", table.getLevel(), table.getOffsetResult());
        visit(resultStmt.getValue()); // Evaluate result
        emit("STORE");                // Store the result
        emit("JMP", _end);            // Jump to the end of the function
    }

    @Processor(target="ReturnStmt")
    void processReturnStmt(ReturnStmt returnStmt) {
        // Get routine end label
        String _end = table.getLabel(table.getRoutine().getName(), true);
        emit("JMP", _end); // Jump to the end of the function
    }

    @Processor(target="WhileDoStmt")
    void processWhileDoStmt(WhileDoStmt whileDoStmt) {
        // Preserve the previous loop exit and generate a new one
        String prevLoopExitLabel = loopExitLabel;
        loopExitLabel = table.getLabel();

        // Generate unique labels for branch targets
        String _start = table.getLabel();
        String _end = loopExitLabel;
        // If then clause
        label(_start);
        visit(whileDoStmt.getExpn()); // Evaluate condition of the while statement
        emit("BFALSE", _end);         // Branch to end if false
        visit(whileDoStmt.getBody());
        emit("JMP", _start);          // Jump to the start again
        label(_end);

        // Restore old loop exit label
        loopExitLabel = prevLoopExitLabel;
    }

    //
    // Exception handling
    //

    void exception(Exception e) {
        // Increment error count
        errors += 1;
        // Print a stack trace if it is not an expected error
        if(e.getCause() instanceof CodeGenException) {
            CodeGenException cge = (CodeGenException) e.getCause();
            cge.setSource(source);
            cge.printCodeTrace();
        } else {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(AST node) {
        try{super.visit(node);}
        catch(Exception e) { exception(e); }
    }

    @Override
    public void visit(List<AST> list) {
        try{super.visit(list);}
        catch(Exception e) { exception(e); }
    }

    //
    // Bounds checking modes
    //

    public enum BoundsChecking { None, Simple, Enhanced };

    //
    // Code generator life cycle
    //

    public CodeGen(List<String> source) {
        super(source);
        this.source = source;
    }

    public void Initialize() {
        // Instantiate internals
        table = new Table();
        sizes = new HashMap<Scope, Integer>();
        // Start the assembler
        assemblerStart();
    }

    public void Generate(Program program, BoundsChecking boundsChecking) {
        // Set bounds checking mode
        checking = boundsChecking;

        // Assemble the runtime library
        assemblerPrint(Library.code);

        comment("------------------------------------");
        comment("Start of program"                    );
        comment("------------------------------------");

        section(".code"); // Start the code section
        visit(program);   // Traverse the AST

        comment("------------------------------------");
        comment("End of program"                      );
        comment("------------------------------------");
    }

    public Boolean Finalize() {
        // Finish assembling code
        int result = assemblerEnd();
        if(result < 0) return false;
        // See if any errors have occurred
        if(errors > 0) return false;

        // Check sanity of program
        int sizeProgram = result;
        int sizeStack = Machine.memorySize - sizeProgram;
        for(Entry<Scope, Integer> entry : sizes.entrySet()) {
            if(entry.getValue() > sizeStack) {
                CodeGenException e = new CodeGenException(source, entry.getKey(),
                        "locals size of major scope, " + entry.getValue() + " words, exceeds the machine stack size, " + sizeStack + " words");
                e.printCodeTrace(); return false;
            }
        }

        // Set initial machine state
        Machine.setPC((short) 0);
        Machine.setMSP((short) sizeProgram);
        Machine.setMLP((short) (Machine.memorySize - 1));
        return true;
    }

    // Code generator internals
    private int                 errors;        // The error count
    private Table               table;         // Master table
    private List<String>        source;        // Source listing
    private Map<Scope, Integer> sizes;         // Major scope sizes
    private BoundsChecking      checking;      // Bounds checking mode
    private String              loopExitLabel; // Loop exit label

    //
    // Assembler
    //

    Boolean assemblerStart() {
        assemblerThread = new AssemblerThread();
        assemblerStream = new PrintStream(assemblerThread.getPipe());
        assemblerHighlighting = new AssemblyHighlighting(System.out, Main.syntaxHighlighting);
        assemblerThread.start();
        return true;
    }

    void assemblerPrint(String x) {
        assemblerStream.print(x);
        if(Main.dumpCode || Main.traceCodeGen)
            assemblerHighlighting.print(x);
    }

    void assemblerPrintln(String x) {
        assemblerStream.println(x);
        if(Main.dumpCode || Main.traceCodeGen)
            assemblerHighlighting.println(x);
    }

    int assemblerEnd() {
        try {
            assemblerStream.close();
            assemblerThread.join();
            return assemblerThread.getResult();
        } catch (InterruptedException e) {
            return -1;
        }
    }

    void comment(String comment) {
        assemblerPrintln("; " + comment);
    }

    void section(String name) {
        assemblerPrintln("    SECTION " + name);
    }

    void label(String name) {
        assemblerPrintln(name + ":");
    }

    void print(String line) {
        String _ret = table.getLabel();
        emit("SETUPCALL", _ret);
        emit("PUSHSTR", "\"" + line + "\"");
        emit("JMP", "print");
        label(_ret);
        emit("POP");
    }

    void newline() {
        emit("PUSH", (short) '\n');
        emit("PRINTC");
    }

    void put(int number) {
        emit("PUSH", (short) number);
        emit("PRINTI");
    }

    void put(String string) {
        boolean firstLine = true;
        for(String line : string.split("\\r?\\n")) {
            if(!firstLine) newline();
            else firstLine = false;
            print(line);
        }
    }

    void emit(String instruction, Object... operands) {
        String command = "    " + instruction;
        for(Object op : operands) {
            if(!(op instanceof Boolean)) command += " " + op.toString();
            else command += " " + (((Boolean) op) ? "$true" : "$false");
        } assemblerPrintln(command);
    }

    void reserve(int words) {
        if(words == 0) return;
        emit("RESERVE", words);
    }

    void free(int words) {
        if(words == 0) return;
        emit("PUSH", words);
        emit("POPN");
    }

    // Assembler internals
    private PrintStream          assemblerStream;
    private AssemblerThread      assemblerThread;
    private AssemblyHighlighting assemblerHighlighting;
}
