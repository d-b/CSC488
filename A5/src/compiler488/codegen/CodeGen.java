package compiler488.codegen;

import java.io.*;

import compiler488.ast.stmt.Program;
import compiler488.runtime.Machine;

/**      CodeGenerator.java 
 *<pre>
 *  Code Generation Conventions
 *
 *  To simplify the course project, this code generator is
 *  designed to compile directly to pseudo machine memory
 *  which is available as the private array memory[]
 *
 *  It is assumed that the code generator places instructions
 *  in memory in locations
 *
 *      memory[ 0 .. startMSP - 1 ]
 *
 *  The code generator may also place instructions and/or
 *  constants in high memory at locations (though this may
 *  not be necessary)
 *      memory[ startMLP .. Machine.memorySize - 1 ]
 *
 *  During program exection the memory area
 *      memory[ startMSP .. startMLP - 1 ]
 *  is used as a dynamic stack for storing activation records
 *  and temporaries used during expression evaluation.
 *  A hardware exception (stack overflow) occurs if the pointer
 *  for this stack reaches the memory limit register (mlp).
 *
 *  The code generator is responsible for setting the global
 *  variables:
 *      startPC         initial value for program counter
 *      startMSP        initial value for msp
 *      startMLP        initial value for mlp
 * </pre>
 * @author Daniel Bloemendal
 */

public class CodeGen extends Visitor
{   
    //
    // Code generator life cycle
    //

    public void Initialize() {
        // Start the assembler
        assemblerStart();
    }
    
    public void Generate(Program program) {
        new Frame(program, (short) 0);
        section(".code");
        put("Hello world!");
        emit("HALT");
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
    // Assembler
    //
    
    Boolean assemblerStart() {
        assemblerThread = new AssemblerThread();        
        assemblerStream = new PrintStream(assemblerThread.getPipe());
        assemblerThread.start();
        return true;
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
    
    void section(String name) {
        assemblerStream.println("SECTION " + name);
    }
    
    void put(String string) {
        boolean firstLine = true;
        for(String line : string.split("\\r?\\n")) {
            if(!firstLine) assemblerStream.println("PUTNEWLINE");
            else firstLine = false; 
            assemblerStream.println("PUTSTR \"" + line + "\"");
        }
    }
    
    void emit(String instruction, Object... operands) {
        String command = instruction;
        for(Object op : operands)
            command += " " + op.toString();
        assemblerStream.println(command);
    }
    
    // Assembler internals
    PrintStream     assemblerStream;
    AssemblerThread assemblerThread;
}
