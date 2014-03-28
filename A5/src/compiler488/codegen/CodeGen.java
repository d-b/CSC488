package compiler488.codegen;

import java.io.*;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import compiler488.ast.AST;
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
import compiler488.ast.expn.TextConstExpn;
import compiler488.ast.expn.UnaryMinusExpn;
import compiler488.ast.stmt.AssignStmt;
import compiler488.ast.stmt.IfStmt;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.PutStmt;
import compiler488.ast.stmt.Scope;
import compiler488.runtime.Machine;
import compiler488.compiler.Main;
import compiler488.codegen.visitor.Visitor;
import compiler488.codegen.visitor.Processor;
import compiler488.codegen.Frame;

/**
 * Code generator for compiler 488
 *
 * @author Daniel Bloemendal
 */
public class CodeGen extends Visitor
{
    //
    // Scope processing
    //
    
    boolean processMajor(Scope scope) {        
        // Bail out if it is not a major scope
        boolean isRoutine = (scope.getParent() instanceof RoutineDecl);
        boolean isProgram = (scope instanceof Program);
        if(!isRoutine && !isProgram) return false;
        
        // The routine
        RoutineDecl routine = null;

        // Emit comment for start of scope
        if(isRoutine) {
            routine = (RoutineDecl) scope.getParent();
            comment("Start of " + routine.getName());
        } else comment("Start of program");

        // Generate code for scope
        enterFrame(scope);                                     // Enter a new stack frame
        if(isRoutine) label(getLabelRoutine(routine));         // Starting label
        emit("SAVECTX", 0);                                    // Scope prolog
        reserve(currentFrame().getSize());                     // Reserve memory for locals
        visit(scope.getStatements());                          // Visit statements in scope
        if(isRoutine) label(getLabelRoutine(routine, true));   // Ending label
        free(currentFrame().getSize());                        // Free locals memory
        emit("RESTORECTX", currentFrame().getLevel(),          // Scope epilog
                           currentFrame().getArgumentsSize()); // ...
        if(!currentFrame().isRoutine()) emit("HALT");          // Program epilog
        exitFrame();                                           // Exit the stack frame

        // Emit comment for end of scope
        if(isRoutine) { comment("End of " + routine.getName()); }
        else comment("End of program");
        
        // Scope was a major scope
        return true;
    }
    

    @Processor(target="Scope")
    void processScope(Scope scope) {
        enterScope(scope);                         // Enter scope        
        codegenCounter = 0;                        // Reset the counter
        boolean isMajor = processMajor(scope);     // Process major scopes
        if(!isMajor) visit(scope.getStatements()); // Add statements for minor scopes

        // Process declarations
        if(codegenCounter > 0) {
            String _end = getLabelGenerated();
            emit("JMP", _end);
            visit(scope.getDeclarations());
            label(_end);
        }
    }

    // TODO: The counter is a bit of a hack.
    // Consider adding routine information to Frame
    @Processor(target="RoutineDecl")
    void processRoutineDecl(RoutineDecl routine) {
        visit(routine.getBody()); codegenCounter += 1;
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
        String _false = getLabelGenerated();
        String _end   = getLabelGenerated();
        
        // Condition
        visit(conditionalExpn.getCondition()); // Evaluate condition
        emit("BFALSE", _false);                // If false jump to false expression
        visit(conditionalExpn.getTrueValue()); // Otherwise evaluate true expression
        emit("JMP", _end);                     // Jump to end of block
        
        // False expression
        label(_false);
        conditionalExpn.getFalseValue();       // Evaluate ``falseValue'' expression
        
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
        //findFrame(functionCallExpn);
        //emit("SETUPCALL", )
        //functionCallExpn.getArguments()
    }
    
    @Processor(target="IdentExpn")
    void processIdentExpn(IdentExpn identExpn) {
        short offset = currentFrame().getOffset(currentScope(), identExpn.getIdent().getId());
        emit("ADDR", currentLexicalLevel(), offset);
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
        String _checkRHS = getLabelGenerated();
        String _end      = getLabelGenerated();

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
        String _end = getLabelGenerated();

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
        short leftOffset = currentFrame().getOffset(currentScope(), assignStmt.getLval().getIdent().getId());
        emit("ADDR", currentFrame().getLevel(), leftOffset); // Emit address of target variable
        visit(assignStmt.getRval());                         // Evaluate the right side expression
        emit("STORE");                                       // Store the value of the expression in the left side variable
    }

    @Processor(target="IfStmt")
    void processIfStmt(IfStmt ifStmt) {
        // Generate unique labels for branch targets
        String _else = getLabelGenerated();
        String _end  = getLabelGenerated();

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

    @Processor(target="PutStmt")
    void processPutStmt(PutStmt putStmt) {
        for(Printable p : putStmt.getOutputs().getList())
            if(p instanceof TextConstExpn)
                put(((TextConstExpn) p).getValue()); // String printable
            else if(p instanceof NewlineConstExpn)
                put("\n");                           // Newline printable
            else if(p instanceof Expn)
                { visit((Expn) p); emit("PRINTI"); } // Expression printable
            else throw new RuntimeException("unknown printable");
    }

    //
    // Code generator life cycle
    //

    public void Initialize() {
        // Instantiate internals
        codegenLevel = 0;
        codegenFrames = new LinkedList<Frame>();
        codegenRoutines = new HashMap<AST, Frame>();
        codegenCounter = 0;
        codegenLabels = 0;
        codegenDump = Main.dumpCode;
        // Start the assembler
        assemblerStart();
    }

    public void Generate(Program program) {
        emit("SECTION", ".code"); // Start the code section
        visit(program);           // Traverse the AST
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

    //
    // Labels
    //

    String getLabelGenerated() {
        return "_L" + codegenLabels++;
    }

    String getLabelRoutine(RoutineDecl routine) {
        return getLabelRoutine(routine, false);
    }

    String getLabelRoutine(RoutineDecl routine, boolean end) {
        Frame frame = codegenRoutines.get(routine);
        if(frame == null) return null;
        String label = routine.getName() + "_LL" + frame.getLevel();
        if(end) label += "_END";
        return label;
    }

    //
    // Helpers
    //

    void enterFrame(Scope scope) {
        Frame frame = new Frame(scope, currentLexicalLevel());
        if(frame.isRoutine()) codegenRoutines.put(frame.getRoutine(), frame);
        codegenFrames.push(frame);
    }

    void exitFrame() {
        codegenFrames.pop();
    }

    Frame currentFrame() {
        return codegenFrames.peek();
    }
    
    Frame findFrame(RoutineDecl routineDecl){
        return codegenRoutines.get(routineDecl);
    }

    short currentLexicalLevel() {
        return (short) Math.max(codegenFrames.size() - 1, 0);
    }
    
    void enterScope(Scope scope) {
        codegenScope = scope;
    }
    
    void exitScope() {
        codegenScope = null;
    }

    Scope currentScope() {
        return codegenScope;
    }

    // Code generator internals
    int             codegenLevel;
    Scope           codegenScope;
    Deque<Frame>    codegenFrames;
    Map<AST, Frame> codegenRoutines;
    int             codegenCounter;
    int             codegenLabels;
    boolean         codegenDump;

    //
    // Assembler
    //

    Boolean assemblerStart() {
        assemblerThread = new AssemblerThread();
        assemblerStream = new PrintStream(assemblerThread.getPipe());
        assemblerThread.start();
        return true;
    }

    void assemblerPrintln(String x) {
        assemblerStream.println(x);
        if(codegenDump) System.out.println(x);
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

    void put(String string) {
        boolean firstLine = true;
        for(String line : string.split("\\r?\\n")) {
            if(!firstLine) assemblerPrintln("PUTNEWLINE");
            else firstLine = false;
            assemblerPrintln("PUTSTR \"" + line + "\"");
        }
    }

    void emit(String instruction, Object... operands) {
        String command = instruction;
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
    PrintStream     assemblerStream;
    AssemblerThread assemblerThread;
}
