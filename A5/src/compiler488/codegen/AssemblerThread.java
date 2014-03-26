package compiler488.codegen;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import compiler488.codegen.assembler.Assembler;
import compiler488.codegen.assembler.InvalidInstructionError;
import compiler488.codegen.assembler.LabelNotResolvedError;

/**
 * Assembler thread with pipe
 * 
 * @author Daniel Bloemendal
 */
public class AssemblerThread extends Thread {
    AssemblerThread() {
        try {
            assembler = new Assembler();
            assemblerIn = new PipedInputStream();
            assemblerOut = new PipedOutputStream(assemblerIn);
            assemblerSuccess = false;
        } catch (IOException e) {}
    }
    
    @Override
    public void run() {
        try {
            assembler.Assemble(assemblerIn);
            assemblerSuccess = true;
        } catch (InvalidInstructionError e) {
            System.err.println(e.getMessage());
        } catch (LabelNotResolvedError e) {
            System.err.println(e.getMessage());
        }
    }
    
    public PipedOutputStream getPipe() {
        return assemblerOut;
    }
    
    public int getResult() {
        return assemblerSuccess ? assembler.getSize() : -1;
    }
            
    // Assembler & pipes
    private Assembler         assembler;
    private PipedInputStream  assemblerIn;
    private PipedOutputStream assemblerOut;
    private Boolean           assemblerSuccess;
}