package compiler488.ast;

public interface SourceLoc {
    public String toString();
    public String getFilename();
    public int getStartLine();
    public int getStartColumn();
    public int getEndLine();
    public int getEndColumn();
}

