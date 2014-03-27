package compiler488.codegen.assembler;

/**
 * Label operand for an IR assembly instruction
 *
 * @author Daniel Bloemendal
 */
public class LabelOperand extends IntegerOperand {
    public LabelOperand(String label) {super((short) 0); this.label = label;}
    public void setAddress(short address) { resolved = true; value = address; }
    public String getLabel() { return label; }

    // Get resolved value
    public short getValue() throws LabelNotResolvedError {
        if(!resolved) throw new LabelNotResolvedError("label '" + label + "' has not been resolved" );
        return value;
    }

    // Internal members
    private boolean resolved;
    private String label;
    private short value;
}
