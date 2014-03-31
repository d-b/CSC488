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
        this.labels = new HashMap<String, Integer>();
        this.size = 0;
        this.address = 0;
    }

    //
    // Labels & instructions
    //

    public void addLabel(String name) {
        labels.put(name, size);
    }

    public Integer getLabel(String name) {
        Integer offset = labels.get(name);
        if (offset == null) return null;
        return address + offset;
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

    public int allocateMemory(int words) {
        int addr = address + size;
        size += words;
        return addr;
    }

    // Getters/setters
    public String getName() { return name; }
    public int getAddress() { return address; }
    public void setAddress(int baseAddress) { this.address = baseAddress; }
    public int getSize() { return size; }

    // Internal members
    private String name;
    private List<Instruction> instructions;
    private Map<String, Integer> labels;
    private int size;
    private int address;
}
