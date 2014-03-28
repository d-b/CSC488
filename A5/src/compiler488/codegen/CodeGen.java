package compiler488.codegen;

import java.io.*;

import compiler488.ast.Printable;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.expn.ArithExpn;
import compiler488.ast.expn.BoolConstExpn;
import compiler488.ast.expn.BoolExpn;
import compiler488.ast.expn.CompareExpn;
import compiler488.ast.expn.ConditionalExpn;
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
import compiler488.ast.stmt.AssignStmt;
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

/**
 * Code generator for compiler 488
 *
 * @author Daniel Bloemendal
 * @author Oren Watson
 */
public class CodeGen extends Visitor
{
    //
    // Scope processing
    //

    void majorProlog() {
        // Prepare information about scope
        RoutineDecl routine = null;
        Scope scope = table.currentScope();
        boolean isProgram = (scope instanceof Program);
        if(!isProgram) routine = (RoutineDecl) scope.getParent();

        // Emit comment for start of scope
        if(isProgram) { comment("Start of program");
                        comment("------------------------------------"); }
        else comment("Start of " + routine.getName());

        if(!isProgram)
            label(table.getLabel(routine.getName()));       // Starting label
        emit("SAVECTX", table.getLevel());                  // Scope prolog
        reserve(table.getLocalsSize());                     // Reserve memory for locals
    }

    void majorEpilog() {
        // Prepare information about scope
        RoutineDecl routine = null;
        Scope scope = table.currentScope();
        boolean isProgram = (scope instanceof Program);
        if(!isProgram) routine = (RoutineDecl) scope.getParent();

        if(!isProgram)
            label(table.getLabel(routine.getName(), true)); // Ending label
        free(table.getLocalsSize());                        // Free locals memory
        emit("RESTORECTX", table.getLevel(),                // Restore context
                           table.getArgumentsSize());       // ...
        if(isProgram) emit("HALT");                         // Program epilog
        else          emit("BR");                           // Routine epilog

        // Emit comment for end of scope
        if(isProgram) comment(" ---------- End of program ----------");
        else { comment("End of " + routine.getName()); emit(""); }
    }

    @Processor(target="Scope")
    void processScope(Scope scope) {
        // Enter the scope
        table.enterScope(scope);

        // Major prolog
        if(table.inMajorScope()) majorProlog();

        // Visit statements in scope
        visit(scope.getStatements());

        // Process routine declarations
        if(table.getRoutineCount() > 0 ) {
            comment("Routine declarations");
            comment("------------------------------------");
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
        String _func = table.getLabel(functionCallExpn.getIdent().getId());
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

    @Processor(target="IdentExpn")
    void processIdentExpn(IdentExpn identExpn) {
        Variable var = table.getVaraible(identExpn.getIdent().getId());
        emit("ADDR", var.getLevel(), var.getOffset());
        emit("LOAD");
    }

    @Processor(target="IntConstExpn")
    void processIntConstExpn(IntConstExpn intConstExpn) {
        emit("PUSH", intConstExpn.getValue()); // Push the constant literal
    }

    @Processor(target="NotExpn")
    void processNotExpn(NotExpn notExpn) {
        visit(notExpn.getOperand()); // Evaluate expression
        emit("PUSH", "$false");      // Negate value by comparing with ``false''
        emit("EQ");                  // ...
    }

    @Processor(target="SubsExpn")
    void processSubsExpn(SubsExpn subsExpn) {
    }

    @Processor(target="UnaryMinusExpn")
    void processUnaryMinusExpn(UnaryMinusExpn unaryMinusExpn) {
        visit(unaryMinusExpn.getOperand()); // Evaluate expression
        emit("NEG");                        // Negate the value
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
        Variable leftVar = table.getVaraible(assignStmt.getLval().getIdent().getId());
        emit("ADDR", leftVar.getLevel(), leftVar.getOffset()); // Emit address of target variable
        visit(assignStmt.getRval());                           // Evaluate the right side expression
        emit("STORE");                                         // Store the value of the expression in the left side variable
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
        String _proc = table.getLabel(procedureCallStmt.getIdent().getId());
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
        for(Printable p : putStmt.getOutputs().getList())
            if(p instanceof TextConstExpn)
                put(((TextConstExpn) p).getValue()); // String printable
            else if(p instanceof NewlineConstExpn)
                newline();                           // Newline printable
            else if(p instanceof Expn)
                { visit((Expn) p); emit("PRINTI"); } // Expression printable
            else throw new RuntimeException("unknown printable");
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
    // Code generator life cycle
    //

    public void Initialize() {
        // Instantiate internals
        table = new Table();
        // Start the assembler
        assemblerStart();
    }

    public void Generate(Program program) {
        // Load the library
        section(Library.section);
        assemblerPrint(Library.code);

        // Generate the code
        section(".code"); // Start the code section
        visit(program);   // Traverse the AST
    }

    public Boolean Finalize() {
        // Finish assembling code
        int result = assemblerEnd();
        if(result < 0) return false;
        // Set initial machine state
        Machine.setPC((short) 0);
        Machine.setMSP((short) result);
        Machine.setMLP((short) (Machine.memorySize - 1));
        return true;
    }

    // Code generator internals
    private Table  table;         // Master table for code generator
    private String loopExitLabel; // Loop exit label

    //
    // Assembler
    //

    Boolean assemblerStart() {
        assemblerThread = new AssemblerThread();
        assemblerStream = new PrintStream(assemblerThread.getPipe());
        assemblerThread.start();
        return true;
    }

    void assemblerPrint(String x) {
        assemblerStream.print(x);
        if(Main.dumpCode) System.out.print(x);
    }

    void assemblerPrintln(String x) {
        assemblerStream.println(x);
        if(Main.dumpCode) System.out.println(x);
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
        assemblerPrintln("SECTION " + name);
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

    void reserve(short words) {
        if(words == 0) return;
        emit("RESERVE", words);
    }

    void free(short words) {
        if(words == 0) return;
        emit("PUSH", words);
        emit("POPN");
    }

    // Assembler internals
    private PrintStream     assemblerStream;
    private AssemblerThread assemblerThread;
}
