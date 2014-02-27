package compiler488.ast;

/**
 * A source location which tracks an inclusive range of characters from the 
 * source file, specified by a start and end pairs of line and column number.
 */
public interface SourceLoc {
    public String toString();
    
    /**
     * @return the filename where the source location is from
     */
    public String getFilename();
    
    /**
     * @return the inclusive line which this source location starts on
     */
    public int getStartLine();
    
    /**
     * @return the inclusive column position which this location starts at
     */
    public int getStartColumn();
    
    /**
     * @return the inclusive line number which this source location ends on
     */
    public int getEndLine();
    
    /**
     * @return the exclusive column position which this location ends just before
     */
    public int getEndColumn();
}

