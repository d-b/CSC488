package compiler488.compiler;

import java.io.*;

import compiler488.parser.*;
import compiler488.ast.AST ;
import compiler488.ast.stmt.Program;
import compiler488.semantics.Semantics;
import compiler488.symbol.SymbolTable;
import compiler488.codegen.CodeGen;
import compiler488.runtime.*;

/** This class serves as the main driver for the CSC488S compiler.<BR>
 *  It accepts user options and coordinates overall control flow.
 *  The main flow of control includes the following activities: 
 *  <ul>
 *    <li> Parse user supplied arguments and options.
 *    <li> Open output files.
 *    <li> Parse the input and produce an AST.
 *    <li> Perform semantic Analysis on the AST
 *    <li> Perform code generation on the AST
 *    <li> Invoke the machine interpreter to execute the compiled program
 *    <li> Close output files.
 *  </ul>
 *
 *  See the compiler man page for a list of Main program options.
 *
 * @author Dave Wortman
 */

public class Main {

  /** Only constructor is private, 
   *  Do not allocate any instances of this class. 
   */
  private Main() { }

  /**  public error flag  
   *   Setting this flag to true will suppress further processing 
   *   This can be used by parsing, semantic analysis or code generation
   *   to quit early if errors have occurred.
   */
  public static boolean errorOccurred = false ;

  /*
   * Options set by the user 
   */

  /** User option -- supress execution */
  public static boolean supressExecution   = false ;

  // DUMP Options 
  /** User option -- dump AST after parsing */
  private static boolean dumpAST1  = false;
  /** User option -- dump AST after semantic analysis */
  private static boolean dumpAST2  = false;
  /** User option -- dump compiled code before execution */
  public static boolean dumpCode  = false;
  /** User option -- dump symbol table after semantic analysis */
  public static boolean dumpSymbolTable   = false;

  /* TRACE options switches */
  /** User option -- trace lexical analysis */
  public static boolean traceLexical  = false;
  /** User option -- trace syntax analysis */
  public static boolean traceSyntax = false ;
  /** User option -- trace AST operations */
  public static boolean traceAST  = false;
  /** User option -- trace semantic analysis */
  public static boolean traceSemantics  = false;
  /** User option -- trace symbol table operations */
  public static boolean traceSymbols  = false;
  /** User option -- trace code generation */
  public static boolean traceCodeGen  = false;
  /** User option -- trace program execution */
  public static boolean traceExecution  = false;

  /* FILE NAMES supplied by the user  */
  /** Source file to be compiled	      */
  private static String sourceFileName = new String() ;
  /** User option -- alternative file sink for compiler error messages */
  private static String errorFileName  = new String() ;
  /** User option -- alternative file sink for compiler output */
  private static String compilerOutputFileName  = new String()  ;
  /** User option -- alternative file sink for compiler trace output */
  private static String compilerTraceFileName  = new String() ;
  /** User option -- alternative file sink for compiler dump output */
  private static String compilerDumpFileName   = new String() ;
  /** User option -- alternative file sink for program trace output */
  private static String executeTraceFileName  = new String() ;
  /** User option -- alternative file sink for program execution input */
  private static String executeInputFileName  = new String () ;
  
  /* FILES and STREAMS */
  /** compiler and program input file */
  private static File  inputFile = null ;
  /** stream source for compiler or program input */
  private static FileInputStream inputStream = null ;
  /** file sink for compiler or program output  */
  private static File  outputFile = null ;
  /** stream sink for compiler and program output */
  private static FileOutputStream outputStream = null ;
  /** file sink for error messages  */
  private static File  errorFile = null ;
  /** stream sink for error messages */
  private static FileOutputStream errorStream = null ;
  /** file  sink for dumps  */
  private static File  dumpFile = null ;
  /** file stream sink for dumps */
  private static FileOutputStream dumpFileStream = null ;

  /* Save settings before tampering */
  private static InputStream saveSysIn = System.in ;
  private static PrintStream saveSysOut = System.out ;
  private static PrintStream saveSysErr = System.err ;

  /** stream for doing dumps <BR>
   *  All implementations of dump should write on this stream */
  public static PrintStream dumpStream = null ;

  /** PrintStream for trace output. <BR>
   *  All implementations of tracing should write on this stream 
   */
  public static PrintStream traceStream = null ;

  /** 
   *  process command line arguments to Main program. <BR>
   *  Will accept any name as a file argument, 
   *  if the name is invalid the error  will be caught where the file is used.<BR>
   *  Sets boolean flags to activate compiler options
   * @param arguments is an array of strings containing command line arguments.
   */

    private static void commandLineArgs(String arguments[])
	{
	int length = arguments.length; //number of command line arguments passed
	int optionLen = 0;		//number of arguments passed
					//for current option

	int i, j, k;		//just a counter
	String argTmp ;		//temp argument strings for -D and -T
        final String badUsage = "Incorrect usage of command line arguments."
				+ " Please refer to the man page." ;

	if (length > 0)
	  try{		// catch arrayOutOfBoundsException for bad argument list
	    for(i = 0; i < length; i++){
		if (arguments[i].equals("-X"))
		    supressExecution = true;
		else if (arguments[i].equals("-D")) {
		    i++;	// advance to next argument
		    argTmp = arguments[ i ] ;
		    dumpAST1 = argTmp.indexOf('a') >= 0 ;
		    dumpAST2 = argTmp.indexOf('b') >= 0 ;
		    dumpCode = argTmp.indexOf('x') >= 0 ;
		    dumpSymbolTable = argTmp.indexOf('y') >= 0 ;
		    k = argTmp.length();
		    for( j = 0 ; j < k ; j++ )
			if( "abxy".indexOf( argTmp.charAt(j)) < 0 )
			   System.err.println("Invalid flag '" +
				argTmp.charAt(j) + "' for -D option (ignored)");
		}
		else if (arguments[i].equals("-T")) {
		    i++;   // advance to next argument
		    argTmp = arguments[ i ] ;
		    traceLexical = argTmp.indexOf('l') >= 0 ;
		    traceSyntax  = argTmp.indexOf('p') >= 0 ;
		    traceAST     = argTmp.indexOf('a') >= 0 ;
                    traceSemantics = argTmp.indexOf('s') >= 0 ;
                    traceSymbols = argTmp.indexOf('y') >= 0 ;
		    traceCodeGen = argTmp.indexOf('c') >= 0 ;
		    traceExecution = argTmp.indexOf('x') >= 0 ;
		    k = argTmp.length();
		    for( j = 0 ; j < k ; j++ )
			if( "lpasycx".indexOf( argTmp.charAt(j)) < 0 )
			   System.err.println("Invalid flag '" +
				argTmp.charAt(j) + "' for -T option (ignored)");
		}
		else if(arguments[i].equals("-E")){
		    i++;   // advance to next argument
		    errorFileName = new String(arguments[i]);
		}
		else if(arguments[i].equals("-O")) {
		    i++;   // advance to next argument 
		    compilerOutputFileName = new String(arguments[i]);
		}
		else if(arguments[i].equals("-R")){
		    i++;   // advance to next argument
		    compilerTraceFileName = new String(arguments[i]);
	        }
		else if(arguments[i].equals("-S")) {
		    i++;   // advance to next argument
		    executeTraceFileName  = new String(arguments[i]);
		}
		else if(arguments[i].equals("-U")) {
		    i++;   // advance to next argument
		    compilerDumpFileName = new String(arguments[i]);
		}
		else if(arguments[i].equals("-I")) {
		    i++;
		    executeInputFileName = new String(arguments[i]);
		}

		//if the argument does not begin with '-' then it must be
		//the source file
		else if(arguments[i].charAt(0) != '-') {
		    sourceFileName = new String(arguments[i]);
		    i++;
		    //i should be equal to length at this point.
		    //If not then we have an error
		    if(i != length)
			{
			System.err.println( badUsage );
			errorOccurred = true ;	
			return ;
			}
		    }
		else	// unrecognized command flag
		    {
		    System.err.println( badUsage );
		    errorOccurred = true ;
		    return ;
		    }
		}  // end for length loop
	    }
	    catch(ArrayIndexOutOfBoundsException arrayBounds ){
		    System.err.println( badUsage );
		    errorOccurred = true ;
		    return ;
	    }
	//no source file was given or the command line arguments where
	//not used correctly
	if(sourceFileName.length() == 0){
	    System.err.println( badUsage );
            errorOccurred = true;
	    return ;
	}
     }

  /** Set System.in to be the specified file
   *  @param  fileName - the input file
   */
  private static void setInputSource( String fileName ) 
    {
        if( fileName.length() == 0 )
	    return ; 	// use existing System.in

	/* set System.in to point at specifed file */
	try{
	      inputFile = new File( fileName );
        }
	catch( Exception e ) {
	    System.err.println("Unable to open file " + fileName );
	    System.err.println(e.getClass().getName()
				    + ": " + e.getMessage());
	    return ;  // continue with System.in  unchanged
        }
        try{ 
              inputStream = new FileInputStream( inputFile );
	      System.setIn( inputStream  ) ;
           }
	catch( Exception e ) {
	    System.err.println("Unable to set input stream to  file " + fileName );
	    System.err.println(e.getClass().getName()
				    + ": " + e.getMessage());
	    return ;  // continue with System.in  unchanged
        }
     }

  /** Set System.out to point at the specified file 
   *  @param  fileName  name of the output file
   */
  private static void setOutputSink( String fileName ) 
    {
	/* set System.out to point at specifed file */
	try{
	      outputFile = new File( fileName );
        }
	catch( Exception e ) {
	    System.err.println("Unable to open file " + fileName );
	    System.err.println(e.getClass().getName()
				    + ": " + e.getMessage());
	    return ;  // continue with System.out  unchanged
        }
        try{ 
              outputStream = new FileOutputStream( outputFile );
              // use autoflush for more accurate output
	      System.setOut( new PrintStream( outputStream , true ) ) ;
           }
	catch( Exception e ) {
	    System.err.println("Unable to set output stream to  file " + fileName );
	    System.err.println(e.getClass().getName()
				    + ": " + e.getMessage());
	    return ;  // continue with System.out  unchanged
        }
     }

  /** Set System.err to point at the specifed file
   *  @param fileName  name of the error file 
   */
  private static void setErrorSink( String fileName ) 
    {
	/* set System.err to point at specifed file */
	try{
	      errorFile = new File( fileName );
        }
	catch( Exception e ) {
	    System.err.println("Unable to open file " + fileName );
	    System.err.println(e.getClass().getName()
				    + ": " + e.getMessage());
	    return ;  // continue with System.err  unchanged
        }
        try{ 
              errorStream = new FileOutputStream( errorFile );
		// use autoflush for more precise output
	      System.setErr( new PrintStream( errorStream , true ) ) ;
           }
	catch( Exception e ) {
	    System.err.println("Unable to set error stream to  file " + fileName );
	    System.err.println(e.getClass().getName()
				    + ": " + e.getMessage());
	    return ;  // continue with System.err  unchanged
        }
     }

  /** set traceStream to point at specifed file 
   *  @param fileName  name of trace file
   */
  private static void setTraceStream( String fileName )
    {
        File traceFile = null ;
	FileOutputStream traceFileStream ;

        if( fileName.length() == 0 ){
             traceStream = saveSysOut ;   // trace to System.out
             return ;
        }
        // otherwise set up the file
	try{
	      traceFile = new File( fileName );
        }
	catch( Exception e ) {
	    System.err.println("Unable to open trace file " + fileName );
	    System.err.println(e.getClass().getName()
				    + ": " + e.getMessage());
        }
        try{ 
              traceFileStream = new FileOutputStream( traceFile );
              // use autoFlush for more accurate output 
	      traceStream =  new PrintStream( traceFileStream , true )  ;
           }
	catch( Exception e ) {
	    System.err.println("Unable to set trace stream to  file " + fileName );
	    System.err.println(e.getClass().getName()
				    + ": " + e.getMessage());
	    traceStream = saveSysOut ;
        }
     }

  /*-----------------------------------------------------------*/
  /*--- Main Program ------------------------------------------*/
  /*-----------------------------------------------------------*/

  /** The main driver for the system. 
   * @param argv an array of strings containing command line arguments.
   */
  public static void main(String argv[]) 
    {
	Object parserResult  ;	// the result of parsing and AST building
        Program  programAST  = null ;

	/* process user options and arguments */
	try{
	   commandLineArgs( argv );
	}
        catch( Exception e) 
	    {
		System.err.println("Exception during command line argument processing");
	        System.err.println(e.getClass().getName() + ": " + e.getMessage());
		System.exit( 90 );
	    }
	
        if( errorOccurred ){
	    System.out.println("Processing Terminated due to command line errors");
	    return ;
        }

	/* Setup files for compilation  */
  	if( errorFileName.length() > 0 )
	    setErrorSink( errorFileName );
        if( compilerOutputFileName.length() > 0 )
            setOutputSink( compilerOutputFileName );
        setTraceStream( compilerTraceFileName  );

  	/* sourceFileName must exist or commandLineArgs would have exited */

	/* Scan and Parse the program	*/
	try {
	    Parser p = new Parser(new Lexer(new FileReader(sourceFileName )));
            if(  traceSyntax )
	         parserResult = p.debug_parse().value;  //DEBUG Output
	    else
	         parserResult = p.parse().value;
	    programAST = (Program) parserResult ;
	    }
        catch (SyntaxErrorException e)
            {  // parser has already printed an error message
               errorOccurred = true ;
            }
	catch (Exception e)
	    {
	    System.err.println("Exception during Parsing and AST building");
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	    e.printStackTrace ();
	    errorOccurred = true ;
	    }
	
        if( errorOccurred ){
	    System.out.println("Processing Terminated due to errors during parsing");
	    return ;
        }

	// Dump AST after parsing if requested
	if( dumpAST1 )
	   try{
                if( compilerDumpFileName.length() > 0 ){
		    dumpFile = new File( compilerDumpFileName ) ;
                    dumpStream = new PrintStream( new FileOutputStream( dumpFile )) ;
		    programAST.printOn( dumpStream , 0 );
		    if( ! dumpAST2 )		// finished with dump stream
		    	dumpStream.close();
	        }
                else 
		    programAST.printOn( saveSysOut  , 0 );
	   }
           catch( Exception e) 
	       {
	       System.err.println("Exception during AST dump after AST building");
	       System.err.println(e.getClass().getName() + ": " + e.getMessage());
	       e.printStackTrace ();
	       System.exit(100);
	       }

	try{
	   // INSERT CODE HERE TO DO SEMANTIC ANALYSIS
           // e.g.
	   // programAST.doSemantics() ;
	   // or
	   // Semantics.doIt( programAST );
	}
        catch( Exception e) 
	    {
	    System.err.println("Exception during Semantic Analysis");
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	    e.printStackTrace ();
	    errorOccurred = true ;
	    }
	
        if( errorOccurred ){
	    System.out.println("Processing Terminated due to errors");
	    return ;
        }

	// Dump AST after semantic analysis  if requested
	if( dumpAST2 )
	   try{
                if( compilerDumpFileName.length() > 0 ){
	             if( ! dumpAST1 ) {	// dump stream wasn't opened above
		          dumpFile = new File( compilerDumpFileName ) ;
                          dumpStream = new PrintStream( new FileOutputStream( dumpFile )) ;
		     }
		     programAST.printOn( dumpStream , 0 );
		     dumpStream.close();
	        }
                else 
		    programAST.printOn( saveSysOut  , 0 );
	   }
           catch( Exception e) 
	       {
	       System.err.println("Exception during AST dump after semantic analysis");
	       System.err.println(e.getClass().getName() + ": " + e.getMessage());
	       e.printStackTrace ();
	       System.exit( 110 );
	       }

	// Initialize Machine before code generation
	try{
	    Machine.powerOn();
	}
        catch( Exception e) 
	    {
	    System.err.println("Exception during Machine initialization");
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	    System.exit( 140 );
	    }

	try{
	   // INSERT CODE HERE TO DO CODE GENERATION
           // e.g.
	   // programAST.doCodeGen() ;
	   // or
	   // codeGen.doIt( programAST );
	}
        catch( Exception e) 
	    {
	    System.err.println("Exception during Code Generation");
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	    e.printStackTrace ();
	    errorOccurred = true ;
	    }
	
        if( errorOccurred ){
	    System.out.println("Processing Terminated due to errors");
	    return ;
        }
	else
            System.out.println("End of Compilation");

        if( supressExecution ) {
            System.out.println("Program execution supressed by command line argument");
            return ;		// normal termination
        } else 
            System.out.println("Begin Execution");
	
        if( traceStream != null && traceStream != saveSysOut )
            traceStream.close();	// finish compilation trace 

	setTraceStream( executeTraceFileName );
	setInputSource( executeInputFileName );

	dumpStream = System.out ;
	try{
	     Machine.run( ) ;
	}
        catch( ExecutionException e) 
	    {
	    System.err.println("Exception during Machine Execution" 
	    		+ e.getMessage());
	    // Run error has already dumped machine state.
	    return ;
	    }
        catch( Exception e) 
	    {
	    System.err.println("Unexpected Exception during Machine Execution");
            System.err.println("Please file a Bug Report with the course instructor");
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	    e.printStackTrace ();
            System.exit( 200 );
	    }
	
        if( traceStream != null && traceStream != saveSysOut )
            traceStream.close();	// finish exceution trace 

	System.out.println("End of Execution" );

    }	// end main function

}  // end Main class
