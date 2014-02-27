package compiler488.ast;

/**
 * A source location which tracks an inclusive range of characters from the 
 * source file, specified by a start and end pairs of line and column number.
 */
public class SourceLocImpl implements SourceLoc {
    private String filename;
    private int start_line;
    private int start_column;
    private int end_line;
    private int end_column;

    /**
     * Construct a source location when you have exact filename, start & end lines and columns
     */
    public SourceLocImpl(String filename, int start_line, int start_column, int end_line, int end_column) {
        this.filename = filename;
        this.start_line = start_line;
        this.start_column = start_column;
        this.end_line = end_line;
        this.end_column = end_column;
    }

    /**
     * Construct a single line source location of a token string for which 
     * you know the starting line and column
     * 
     * @param token the string whose length will determine the end column
     */
    public SourceLocImpl(String filename, int start_line, int start_column, String token) {
        this(filename, start_line, start_column, start_line, start_column + token.length());
    }

    /**
     * Construct a single line source location that is exactly one character wide.
     */
    public SourceLocImpl(String filename, int start_line, int start_column) {
        this(filename, start_line, start_column, start_line, start_column + 1);
    }

    /**
     * Construct a source location with a concrete start and whose end will 
     * coincide with the end of another SourceLoc instance.
     * 
     * @param end the other source location to take ending coordinates from 
     */
    public SourceLocImpl(String filename, int start_line, int start_column, SourceLoc end) {
        this(filename, start_line, start_column, end.getEndLine(), end.getEndColumn());
    }

    /**
     * Construct a source location with explicit filename from two other SourceLoc's,
     * taking the start from the first and the end from the second.
     * 
     * @param filename the filename to use for this location
     */
    public SourceLocImpl(String filename, SourceLoc start, SourceLoc end) {
        this(filename, start.getStartLine(), start.getStartColumn(), end);
    }

    /**
     * Construct a source location from two other SourceLoc's, taking the 
     * start from the first and the end from the second, and using the firsts'
     * filename
     */
    public SourceLocImpl(SourceLoc start, SourceLoc end) {
        this(start.getFilename(), start, end);
    }

    /**
     * Duplicate another SourceLoc with an explicit filename.
     * 
     * @param filename the filename to use for this location
     */
    public SourceLocImpl(String filename, SourceLoc range) {
        this(filename, range, range);
    }

    public String toString() {
        return filename + ":" + (start_line + 1) + ":" + (start_column + 1);
    }

    public String getFilename() {
        return filename;
    }

    public int getStartLine() {
        return start_line;
    }

    public int getStartColumn() {
        return start_column;
    }

    public int getEndLine() {
        return end_line;
    }

    public int getEndColumn() {
        return end_column;
    }
}
