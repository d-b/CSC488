package compiler488.codegen;

import java.io.*;

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

public class CodeGen
{
    //
    // Assembler
    //
    
    Boolean assemblerStart() {
        assemblerThread = new AssemblerThread();        
        assemblerStream = new PrintStream(assemblerThread.getPipe());
        assemblerThread.start();
        return true;
    }
       
    void assemblerClose() {
        assemblerStream.close();
    }
    
    int assemblerJoin() {
        try {
            assemblerThread.join();
            return assemblerThread.getResult();
        } catch (InterruptedException e) {
            return -1;
        }
    }
    
    //
    // CodGen life cycle
    //

    public void Initialize()
	{
        // Start the assembler
        assemblerStart();
	}
    
    public void Generate() {
        assemblerStream.println("SECTION .code");
        assemblerStream.println("PUTSTR \"Hello world!\"");
        assemblerStream.println("HALT");        
    }
    
    public Boolean Finalize()
	{
        // Finish assembling code
        assemblerClose();
        int result = assemblerJoin();
        if(result < 0) return false;
        // Set initial machine state
        Machine.setPC((short) 0);
        Machine.setMSP((short) result);
        Machine.setMLP((short) (Machine.memorySize - 1)); 
        return true;
	}
    
    // Assembler 
    PrintStream     assemblerStream;
    AssemblerThread assemblerThread;
}
