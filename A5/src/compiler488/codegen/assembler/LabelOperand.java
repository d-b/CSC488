package compiler488.codegen.assembler;

/**
 * Label operand for an IR assembly instruction
 *
 * @author Daniel Bloemendal
 */
public class LabelOperand extends IntegerOperand {
    public LabelOperand(String label) {super(0); this.label = label;}
    public void setAddress(Integer addr) { resolved = true; value = addr; }
    public String getLabel() { return label; }

    @Override // Get resolved value
    public int getValue() throws LabelNotResolvedException {
        if(resolved) return value;
        else throw new LabelNotResolvedException("label '" + label + "' has not been resolved" );
    }

    // Internal members
    private boolean resolved;
    private String label;
    private int value;
}
