package compiler488.ast;

/**
 * Tracks an inclusive range of characters from the source file, specified by a
 * start and end pairs of line and column number.
 */
public class SourceLoc {
	private int start_line;
    private int start_column;
    private int end_line;
    private int end_column;

    public SourceLoc(int start_line, int start_column, int end_line, int end_column) {
        this.start_line = start_line;
        this.start_column = start_column;
        this.end_line = end_line;
        this.end_column = end_column;
    }

    public SourceLoc(int start_line, int start_column, String token) {
        this(start_line, start_column, start_line, start_column + token.length());
    }
    
    public SourceLoc(int start_line, int start_column) {
        this(start_line, start_column, start_line, start_column);
    }
    
    public SourceLoc(int start_line, int start_column, AST end) {
        this(start_line, start_column, end.getLoc().end_line, end.getLoc().end_column);
    }

    public SourceLoc(AST start, AST end) {
        this(start.getLoc().start_line, start.getLoc().start_column, end);
    }
    
    public SourceLoc(AST range) {
    	this(range, range);
    }
    
    public String toString() {
    	return "line " + (start_line + 1) + 
    			", column " + (start_column + 1) + 
    			" ~> line " + (end_line + 1) + 
    			", column " + (end_column + 1);
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

