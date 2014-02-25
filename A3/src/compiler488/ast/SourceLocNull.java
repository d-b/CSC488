package compiler488.ast;

public class SourceLocNull implements SourceLoc {
    public SourceLocNull() {}
    public String toString()    {
        return "??:??";
    }
    public String getFilename() {
        return "??";
    }
    public int getStartLine()   {
        return -1;
    }
    public int getStartColumn() {
        return -1;
    }
    public int getEndLine()     {
        return -1;
    }
    public int getEndColumn()   {
        return -1;
    }
}
