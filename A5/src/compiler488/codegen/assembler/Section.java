package compiler488.codegen.assembler;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Assembly section
 * 
 * @author Daniel Bloemendal
 */
public class Section {
    // New section
    Section(String name) {
        this.name = name;
        this.instructions = new LinkedList<Instruction>();
        this.labels = new HashMap<String, Short>();
        this.size = 0;
        this.address = 0;
    }
    
    //
    // Labels & instructions
    //
    
    public void addLabel(String name) {
        labels.put(name, size);
    }
    
    public Short getLabel(String name) {
        Short offset = labels.get(name);
        if (offset == null) return null;
        return (short)(address + offset);
    }
    
    public void addInstruction(Instruction instruction) {
        this.size += instruction.getSize();
        instructions.add(instruction);
    }
    
    public List<Instruction> getInstructions() {
        return Collections.unmodifiableList(instructions);
    }
    
    //
    // Memory allocation
    //
    
    public short allocateMemory(short words) {
        short addr = (short)(address + size);
        size += words;
        return addr;
    }
   
    // Getters/setters
    public String getName() { return name; }    
    public short getAddress() { return address; }
    public void setAddress(short address) { this.address = address; }
    public short getSize() { return size; }

    // Internal members
    private String name;
    private List<Instruction> instructions;
    private Map<String, Short> labels;
    private short size;
    private short address;
}
