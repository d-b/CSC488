package compiler488.ast;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class SourceLocPrettyPrinter {
    private PrintStream output;
    private List<String> lines;
    private SourceLoc loc;

    public SourceLocPrettyPrinter(PrintStream output, List<String> lines, SourceLoc loc) {
        this.output = output;
        this.lines = lines;
        this.loc = loc;
    }
    
    static public String printToString(List<String> lines, SourceLoc loc) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(baos);
        SourceLocPrettyPrinter printer = new SourceLocPrettyPrinter(stream, lines, loc);
        printer.print(); return baos.toString();
    }

    public void print() {
        String line = lines.get(loc.getStartLine());

        // If a tab appears in the source line, convert to a single space to correspond to the single tab character!
        line = line.replace("\t", " ");

        int start = loc.getStartColumn();
        int num;

        if (loc.getStartLine() == loc.getEndLine()) {
            // Range fits on one line
            num = loc.getEndColumn() - start;
        } else {
            // Show only the first line
            num = line.length() - start;
        }

        output.println(line);

        for (int i = 0; i < start; i++) {
            output.print(" ");
        }

        for (int i = 0; i < num; i++) {
            output.print("^");
        }

        output.println();
    }

    public String getFileRef() {
        return loc.getFilename() + ":" + (loc.getStartLine() + 1) + ":" + (loc.getStartColumn() + 1);
    }
}

