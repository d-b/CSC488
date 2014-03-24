package compiler488.ast.expn;

import compiler488.ast.Printable;
import compiler488.ast.SourceLoc;

/**
 * Represents the special literal keyword 'newline' associated with writing a 
 * newline character on the output device.
 */
public class NewlineConstExpn extends ConstExpn implements Printable {
    public NewlineConstExpn(SourceLoc loc) {
        super(loc);
    }

    /** Returns the string <b>"newline"</b>. */
    @Override
    public String toString() {
        return "newline";
    }
    
    public boolean equals(Object o) {
        return o instanceof NewlineConstExpn;
    }
}
