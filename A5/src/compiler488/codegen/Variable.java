package compiler488.codegen;

/**
 * Variable reference
 *
 * @author Daniel Bloemendal
 */
public class Variable {
    // Instantiate a new variable reference
    public Variable(short level, short offset){
        this.level = level;
        this.offset = offset;
    }
    
    // Getters for reference info
    public short getLevel() { return level; }
    public short getOffset() { return offset; }
    
    // Reference info
    private short level;
    private short offset;
}
