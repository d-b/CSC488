package compiler488.ast;

/**
 * Tracks an inclusive range of characters from the source file, specified by a
 * start and end pairs of line and column number.
 */
public class SourceLoc {
	private String filename;
	private int start_line;
    private int start_column;
    private int end_line;
    private int end_column;

    public SourceLoc(String filename, int start_line, int start_column, int end_line, int end_column) {
    	this.filename = filename;
        this.start_line = start_line;
        this.start_column = start_column;
        this.end_line = end_line;
        this.end_column = end_column;
    }

    public SourceLoc(String filename, int start_line, int start_column, String token) {
        this(filename, start_line, start_column, start_line, start_column + token.length());
    }
    
    public SourceLoc(String filename, int start_line, int start_column) {
        this(filename, start_line, start_column, start_line, start_column);
    }
    
    public SourceLoc(String filename, int start_line, int start_column, AST end) {
        this(filename, start_line, start_column, end.getLoc().end_line, end.getLoc().end_column);
    }

    /* NB: Cannot be a constructor because ASTList<T> `is-a` AST for some reason... */
    public static SourceLoc fromList(String filename, int start_line, int start_column, ASTList<? extends AST> nodes) {
    	return new SourceLoc(filename, start_line, start_column, nodes.getList().getLast());
    }

    public SourceLoc(String filename, AST start, AST end) {
        this(filename, start.getLoc().start_line, start.getLoc().start_column, end);
    }
    
    public SourceLoc(String filename, AST range) {
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

