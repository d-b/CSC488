package compiler488.ast;

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

	public void print() {
		if (loc.getStartLine() != loc.getEndLine()) {
			output.println("Multi-line pretty print not available: " + loc);
			return;
		}
		
		String line = lines.get(loc.getStartLine());
		
		int start = loc.getStartColumn();
		int num = loc.getEndColumn() - start;
		
		output.println("Line " + loc.getStartLine() + ":");	
		output.println(line);
		
		for (int i = 0; i < start; i++) {
			output.print(" ");
		}
		
		for (int i = 0; i < num; i++) {
			output.print("^");
		}
		
		output.println();
	}
}
