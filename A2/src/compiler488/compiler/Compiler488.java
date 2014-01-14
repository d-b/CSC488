package compiler488 ;

/* $Id: Compiler488.java 2 2010-01-07 16:12:46Z dw $
 /**
 * This Compiler488 is an extremely simple scanner/parser driver
 * for Assignment 2
 */

/* File Name: Compiler488.java
 * To Create: 
 * After the scanner and the parser, have been created.
 * > javac Compiler488.java
 *
 * To Run: 
 * > java   ???Compiler488   program
 * where program is an input file for the compiler
 * This simple minded driver does not read from standard input
 */

import compiler488.parser.*;
import java.io.*;

public class Compiler488 {
	
	static public void main(String argv[]) {
		/* Start the parser */
		try {
			System.out.println("Start parsing");
			testParser (new File (argv[0]));
			System.out.println("End parsing");
		} catch (Exception e) {
			/* do cleanup here -- possibly rethrow e */
			System.out.println("Exception during Parsing");
			e.printStackTrace();
		}
	}
	
	static public void testParser (File file) throws Exception {
              Parser p = new Parser(new Lexer(new FileReader(file)));
              Object result = p.parse().value;
	}

}
