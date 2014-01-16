import java.io.IOException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class CSC488Recognizer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if(args.length == 0){
			System.out.println("Usage: <sample>");
			return;
		}
		
		CharStream input = new ANTLRFileStream(args[0]);
		CSC488Lexer lexer = new CSC488Lexer(input);
		CommonTokenStream stream = new CommonTokenStream(lexer);
		CSC488Parser parser = new CSC488Parser(stream);
		parser.program();
	}

}
