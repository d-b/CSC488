package compiler488.runtime ;

import java.io.*;
import compiler488.compiler.Main; 

/** Machine - a pseduo machine interpreter for CSC488S Course Project <BR>
 *  The class writes to System.out and System.err and reads from System.in
 *  Those streams may be redirected by the calling program
 *  @author  Dave Wortman
 *  @see  "Main.java"
 */

public final class Machine
    {

    // This section defines public class constants that describe the machine.

    // The structure of the machine.

    /** Size of machine memory */
    public static final short memorySize = 16384 ;
    /** Size of display (maximum depth of static nesting) */
    public static final short displaySize = 16;

    // Machine constants.
    /** Hardware value for <b>true</b>   */
    public static final short MACHINE_TRUE = 1;
    /** Hardware value for <b>false</b>  */
    public static final short MACHINE_FALSE = 0;
    /** Hardware limit for largest integer value  */
    public static final short MAX_INTEGER = Short.MAX_VALUE  ;
    /** Hardware limit for smallest integer value */
    public static final short MIN_INTEGER = Short.MIN_VALUE + 1 ;
    /** Hardware value used to represent undefined  */
    public static final short UNDEFINED =   Short.MIN_VALUE ;

    // The instructions recognized by the machine.
    public static final short HALT = 0;
    public static final short ADDR = 1;
    public static final short LOAD = 2;
    public static final short STORE = 3;
    public static final short PUSH = 4;
    public static final short PUSHMT = 5;
    public static final short SETD = 6;
    public static final short POP = 7;
    public static final short POPN = 8;
    public static final short DUP = 9;
    public static final short DUPN = 10;
    public static final short BR = 11;
    public static final short BF = 12;
    public static final short NEG = 13;
    public static final short ADD = 14;
    public static final short SUB = 15;
    public static final short MUL = 16;
    public static final short DIV = 17;
    public static final short EQ = 18;
    public static final short LT = 19;
    public static final short OR = 20;
    public static final short SWAP = 21;
    public static final short READC = 22;
    public static final short PRINTC = 23;
    public static final short READI = 24;
    public static final short PRINTI = 25;
    public static final short TRON = 26;
    public static final short TROFF = 27;
    public static final short ILIMIT = 28 ;

    /** Table of instruction names. <BR>
     *  This array must stay in alignment with the instruction values.
     */
    public static final String[] instructionNames =
	{
	"HALT", "ADDR", "LOAD", "STORE", "PUSH", "PUSHMT", "SETD",
	"POP", "POPN", "DUP", "DUPN", "BR", "BF", "NEG", "ADD", "SUB",
	"MUL", "DIV", "EQ", "LT", "OR", "SWAP", "READC", "PRINTC",
	"READI", "PRINTI", "TRON", "TROFF", "ILIMIT"
	};

    /** Table of lengths for each instruction. <BR>
     * NOTE:  length of branch instructions is set to ZERO since 
     * they directly change the pc
     * NOTE:  length of HALT instruction is ZERO since once we
     * reach halt, updating the pc is meaningless
     */
    public static final short[] instructionLength = 
	{
	0,3,1,1,2,1,2,1,1,1,1,           // HALT .. DUPN
	0,0,                            // BR .. BF
	1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2 // READC .. ILIMIT
	};

    // Private instance variables for machine state. 

    /** Initial value of program counter. */
    private static short startPC ;	
    /** First empty slot in the initially empty run time stack.  */
    private static short startMSP ;	
    /** Last memory location available for run time stack  	*/
    private static short startMLP ;	

    // Machine state.

    /** Hardware display registers */
    private static int[] display = new int [displaySize];
    /** Index of top entry in the display (for checking, dumps ) */
    private static int displayMax ;
    /** Hardware memory  */
    private static short[] memory = new short [memorySize];
    /** Hardware program counter */
    private static short pc;			//program counter for the machine
    /** Hardware memory stack pointer <BR>
     *  <b>Note</b> the convention that <i>msp</i> points at the <b>first unused
     *  entry </b> in the stack
     */
    private static short msp;			
    /** Hardware memory limit pointer */
    private static short mlp;			
    /** Hardware bottom stack pointer, lowest valid entry in the stack  */
    private static short bottomOfStack;	

    /** Hardware executing flag, true while executing instructions <BR> 
     *  Set to false by runError and HALT instruction
     */
    private static boolean executing;		
    /** Hardware error flag.  Set to true is an error occurred 
     *  false at start of execution, set to true only by runError 
     */
    private static boolean errorOccurred;	
    private final static int CHARMASK = 0x7F ;	// clean input/output characters

    /** Initialize machine state <BR>
     *  this routine should be called before code generation
     *  and after Main has processed command line arguments.
     *  Initializes display and stack memory.
     */
    public final static void powerOn() {

	// Initialize display.
	for (int i = 0 ; i < displaySize ; i++)
	    display [i] = MIN_INTEGER ;
	    // forces error if unfilled display entry is used
	displayMax = -1 ;	// top of display

	// Initialize memory.
	for (int i = 0; i < memorySize; i++)
	    memory [i] = UNDEFINED;

	memory[ 0 ] = HALT ;	// just in case

        // Initialize start variables to force error if they
        // are not set by code generation
        startPC = -1 ;
	startMSP = - 1 ;
        startMLP = - 1 ;
    }

    /** Provide direct write access to machine memory to simplify code generation. <BR>
     *  <B>Policy:</B> does not check value == UNDEFINED 
     *  @param  addr  write to memory[ addr ]
     *  @param  value value to write to memory
     *  @throws MemoryAddressException 
     */
    public final static void writeMemory( short addr , short value )
       throws MemoryAddressException
       {
	  if( addr < 0 || addr >= memorySize ) 
	     // memory address out of range 
	     throw  new MemoryAddressException("  writeMemory: invalid address: "
			+ addr ) ;
          // policy: do not check for UNDEFINED
	  memory[ addr ] = value ;
       }

    /** Provide direct read access to machine memory to simplify code generation.<BR>
     *  <B>Policy:</B> does not check value == UNDEFINED
     *  @param  addr  read value in memory[ addr ]
     *  @return  memory[ addr ]
     *  @throws  MemoryAddressException
     */
    public final static short readMemory( short addr )
       throws MemoryAddressException
       {
	  if( addr < 0 || addr >= memorySize ) 
	     // memory address out of range 
	     throw  new MemoryAddressException("  readMemory: invalid address: "
			+ addr ) ;
          // policy: do not check for UNDEFINED
	  return memory[ addr ] ;
       }

     /** set initial value of program counter for machine execution <BR>
      * <B>Policy:</B> do not check addr for validity, error will be caught at
      * start of execution.
      */
     public final static void setPC( short addr ){
        startPC = addr ;
     }

     /** set value of run time stack base for program execution <BR>
      * <B>Policy:</B> do not check addr for validity, error will be caught at
      * start of execution.
      */
     public final static void setMSP( short addr ){
        startMSP = addr ;
     }

     /** set value of run time stack top for program execution <BR>
      * <B>Policy:</B> do not check addr for validity, error will be caught at
      * start of execution.
      */
     public final static void setMLP( short addr ){
        startMLP = addr ;
     }

    private static String Blanks = new 
		String("                                           ") ;

    /** pad string by appending  blanks to specified size <BR>
     *  Does nothing if toPad is already longer than toSize
     *  Assumes value of toSize is less than length of Blanks
     *  @param  toPad  string to be padded with blanks
     *  @param  toSize desired length of blank padded string
     */
    private static void padString( StringBuffer toPad , int toSize ) {
	if( toPad.length() < toSize ) 
            toPad.append( Blanks.substring( 0, toSize - toPad.length() ) );
        return ;
    }

    /** 
     *  dump all machine instructions
     *  @param dumpSink  PrintStream sink for the dump
     */
    private final static void dumpInstructions (PrintStream dumpSink)
	throws ExecutionException 
	{
	StringBuffer printThis = new StringBuffer ( 32 );
        StringBuffer temp      = new StringBuffer( 64 )  ;
        int secondColumn = 40 ;

	int addr = 0;
	while(addr < bottomOfStack ) {
	    int startInst = addr;
	    // advance addr based on length of instruction 
	    addr += formatInstruction(addr, printThis);
	    int endInst = addr - 1;
            temp = new StringBuffer( "" );
	    temp.append("memory[ " + startInst + " .. " + endInst + " ]" + " = ") ;
            temp.append( printThis );
	    // print another instruction (if any) on the same line 
	    if( addr < bottomOfStack  ) {
                padString( temp , secondColumn ) ;
                startInst = addr ;
	        // advance addr based on length of instruction
	        addr += formatInstruction(addr, printThis);
		endInst = addr - 1 ;
	        dumpSink.println(temp + "memory[ " + startInst + " .. " + endInst + " ]"
				+ " = " + printThis);
	    } else {
                dumpSink.println( temp );
	    }
        }
     }

    /** dump contents on memory locations to string buffer for dump
     * @param sink  String buffer to dump to
     * @param howMany  number of memory locations to dump
     */
    private final static
    void dumpStack( StringBuffer sink, int howMany  ) {
	//  dump up to howMany locations from top of stack to sink 
	//Dump stack memory
	if (0 <= msp && msp < memorySize)
	    {
	    int mspmin = (msp - howMany  >= 0 ? msp - howMany  : 0);
	    int mspmax = (msp < memorySize ? msp - 1 : memorySize - 1);
	    mspmin = ( mspmin < bottomOfStack ? bottomOfStack : mspmin );
	    if( mspmax < mspmin ) {
                sink.append("");	// nothing to dump
                return ;
            }
	    sink.append(
	    	"memory[" + mspmin + " .. " + mspmax + "] = "  );
	    for(int i = mspmin; i <= mspmax; i++)
                sink.append( memory[i] + "  " );
	    }
	else
	   {
	       sink.append("")  ;	// bad msp, can't dump stack
	   }
    }

    /** dump state of machine to stdout  <BR>
     *  Dumps key machine registers, active part of display <BR>
     *  top 8 (or less) entries in the hardware stack 
     *  @param  msg   prepend msg to start of output
     *  @param  pc    value of hardware program counter
     *  @param  msp   value of hardware stack pointer
     *  @param  mlp   value of hardware stack limit register
     */
    private final static
    void dumpMachineState(String msg, int pc, int msp, int mlp)
	{
	StringBuffer S = new StringBuffer("\t") ;
	System.out.print("\n" + msg);
	//dump control of registers
	System.out.print("  pc = " + pc + ", msp = " + msp + ", mlp = "+mlp+"\n");

	//Dump the active display
	if (displayMax >= 0)
	    {
	    System.out.print("\tdisplay[0 .. " + displayMax + "] = ");
	    for(int i = 0; i <= displayMax; i++)
		System.out.print(display[i] + "  ");
	    System.out.print("\n");
	    }

	//Dump stack memory
        dumpStack( S, 8 ) ;
        System.out.println( S );
	}

    /** Internal procedure to print error message and stop execution <BR>
     *  dumps machine state 
     *  @param  msg  error message to print
     *  @throws ExecutionException
     */
    private final static void runError(String msg )
	throws ExecutionException 
	{
	String msgBuff = "Execution Error -  ";
	msgBuff = msgBuff + msg + "\n" ;

	dumpMachineState(msgBuff, pc, msp, mlp);

	throw new ExecutionException( "  " + msg );
	}

    /** Formats machine instruction at memory[ addr ] <BR>
     * Output incudes the arguments of the instruction.
     * @param addr  address of instruction to format
     * @param printThis  string buffer sink for formatted instruction
     * @return  number of machine words occupied by the instruction and its arguments
     * @throws  ExecutionException
     */
    private final static  int formatInstruction (int addr, StringBuffer printThis)
	throws ExecutionException
	{
	short opCode = memory[addr];

	if (0 <= opCode && opCode < instructionNames.length)
	    {
	    switch (instructionLength [opCode])
		{
		case 0:  // The lengths for BR, BF and HALT are hacks.
		case 1:
		    printThis.replace (0, printThis.length (),
			instructionNames [opCode]);
		    return 1;

		case 2:
		    printThis.replace (0, printThis.length (),
					instructionNames [opCode]
					    + " " + memory [addr + 1]);
		    return 2;

		case 3:
		    printThis.replace (0, printThis.length (),
					instructionNames [opCode]
					    + " " + memory [addr + 1]
					    + " " + memory [addr + 2]);
		    return 3;

		default:
		    throw new ExecutionException( 
			"  formatInstruction: instructionLength [" 
			    + opCode + "] = " + instructionLength [opCode]);
		}
	    }
	else
	    {
	    printThis.replace (0, printThis.length (),
					"not an instruction: " + opCode);
	    return 1;
	    }
	}

    /** Function for validating execution
     *  This function checks that lowBound &lt= value &lt= highBound
     *  runError is called with msg if this is not true
     *
     *	@param  value 	The value to be tested
     *	@param  lowBound  Lower bound for value
     *  @param  highBound  Upper Bound for value
     *  @param  msg     Error message if bound is violated
     *  @throws executionException
     */
    private final static  void rangeCheck (int value,	// value to be checked
					int lowBound,   // lower bound
					int highBound,  // upper bound	
					String msg)     // error message
	throws ExecutionException
	{
	if (value < lowBound || value >  highBound)
	    runError ( msg );	// throws executionException
	return ;
	}

    //  Functions for manipulating the machine stack.
    // Note: the convention is that msp refers to the first UNUSED entry in
    //  the stack.
    // The TOP item on the stack is in memory[msp - 1].

    /** increment stack pointer */
    private final static  void spush()
	{
	msp++;
	}

    /** decrement stack pointer */
    private final static  void spop()
	{
	msp--;
	}

    /** value of top of stack */
    private final static  short top()
	{
	return memory[msp-1];
	}

    /** value of top of stack + 1 */
    private final static short topp1()
	{
	return memory[msp];
	}

    /** value of top of stack - 1 */
    private final static short topm1()
	{
	return memory[msp-2];
	}


    /** Interpreter for Pseudo Machine <BR>
     *  Executes the object code in the public instance array 'memory'. <BR>
     *  Implements all of the hardware instructions <BR>
     *  Changes the state of the machine
     *  ie: the private instance arrays 'memory' and 'display', and the
     *  private instance variables 'pc', and 'msp'.
     *  @throws ExecutionException
     */
    public final static void run ( )
	throws ExecutionException 
	{
	short opCode = 0;		//current instruction code
	boolean tracing = Main.traceExecution ;
	TextReader  inputSource ;	// source for all READ instructions
	int  	    intInput ;		// input for READI
					// counting, iLimit set by ILIMIT instruction
	boolean counting = false ;	// count instructions to limit execution
        int	    iCount = 0 ;	// count of instructions executed 
        int         iLimit = Integer.MAX_VALUE     ;	// instruction execution limit

	// Initialize registers.  Validate initial execution state.
	mlp = startMLP;
	rangeCheck(mlp, 0, memorySize, "Initial value of mlp out of range") ;

	msp = startMSP ;		// The first empty slot.
	rangeCheck(msp, 0, mlp-1, "Initial value of msp out of range") ;

	// Remember bottom of stack to check for stack underflow
	// Must be set before dumpInstructions is called
	bottomOfStack = startMSP ;

	pc = startPC;			// Execution starts here.
	rangeCheck(pc, 0, memorySize-1, "Initial value of pc outside memory") ;

	//  Dump instruction memory if requested.

	if ( Main.dumpCode )
	    dumpInstructions(Main.dumpStream);

	if (Main.supressExecution)
	    {
		System.out.println("Execution suppressed by control flag.\n");
		return;
	    } 
	
	dumpMachineState("Start Execution", pc, msp, mlp);

	/** Initialize for input	*/
	inputSource = new TextReader( System.in );

	//During the execution of each instruction:
	//opCode contains the instruction code and pc refers to the instruction

	errorOccurred = false;	// Only a runError can make it true.
	executing = true;	// This instance variable can be set to false
				//  by HALT or by a runError.
	while (executing)
	    {
	    //Validate current state of the machine
	    //Execute one instruction from memory

	    rangeCheck (pc,
			0, memorySize - 1, "Program counter outside memory.\n");

	    if(msp < bottomOfStack){
		runError("Run stack underflow." );
		return ;
	    }
	    if(msp >= mlp){
		runError("Run stack overflow." );
		return ;
	    }

            iCount++ ;		// Count instructions executed
            if( counting ) {
                if( iCount > iLimit ) 	// count exceeded
                    runError("Instruction execution limit (" + iLimit + ") exceeded" );
            }

	    if(tracing)
		{
		   StringBuffer printThis = new StringBuffer();
		   formatInstruction(pc, printThis);
		   printThis.insert( 0 , pc + ": " );
                   padString( printThis, 20 );
                   dumpStack( printThis, 8 );
		   Main.traceStream.println( printThis );
		}

	    /** fetch and execute the next instruction	*/
	    opCode = memory[pc];

	    switch(opCode)
		{
		//ADDR LL on: push value of display[LL] + ON to stack
		case ADDR:
		    {
		    short ll = memory[pc + 1];
		    rangeCheck(ll, 0, displaySize-1,
					"ADDR: Display index out of range.\n") ;
		    spush();
		    memory[msp-1] = (short)(display[ll] + memory[pc + 2]);
		    }
		    break;

		//LOAD: push the value of memory[TOP] to the stack
		case LOAD:
		    {
		    short adr = memory[msp-1];
		    rangeCheck(adr, 0, memorySize-1,
					"LOAD address out of range.\n") ;
		    if(memory[adr] == UNDEFINED)
			runError("Attempt to LOAD undefined value.\n" );
		    else
			memory[msp-1] = memory[adr];
		    }
		    break;

		//STORE: store a value on top of the stack in memory
		case STORE:
		    {
		    short v = memory[msp-1];
		    spop();
		    short adr = memory[msp-1];
		    spop();
		    // rangeCheck(adr, 0, memorySize-1,
		    // Disallow stores into code area.
                    rangeCheck( adr, bottomOfStack - 1 , memorySize -1 ,
					    "STORE address out of range.\n") ;
		    memory[adr] = v;
		    }
		    break;

		//PUSH V: push V to the stack
		case PUSH:
		    spush();
		    memory[msp-1] = memory[pc + 1];
		    break;

		//PUSHMT: effectively, push MT to the top of the stack
		case PUSHMT:
		    spush();
		    memory[msp-1] = (short)(msp - 1);
		    break;

		//SETD LL: set display[LL] to the top of the stack
		case SETD:
		    {
		    short adr = memory[msp-1];
		    spop();
		    short ll = memory[pc + 1];
		    rangeCheck(ll, 0, displaySize-1,
			       "SETD display index out of range.\n") ;
		    if( adr != MIN_INTEGER ){  // special case - uninitialized
			rangeCheck(adr, bottomOfStack, mlp,
				   "SETD display entry out of range.\n") ;
		    }
		    displayMax = (displayMax > ll ? displayMax : ll);
		    display[ll] = adr;
		    }
		    break;

		//POPN: do n pops, where n is the value on top of the stack
		//      Underflow error will be caught before next instruction
		case POPN:
		    msp -= memory[msp-1];
		    spop();
		    break;

		//POP: pop the top of the machine stack
		case POP:
		    spop();
		    break;

		/* DUPN: leave n copies of the next-to-the -top stack item
		* on the top of the stack, where n is the initial top of
		* the stack value
		*/
		case DUPN:
		    {
		    short n = memory[msp-1];
		    spop();
		    short v = memory[msp-1];
		    spop();
		    rangeCheck(msp+n, bottomOfStack, mlp,
						"DUPN stack overflow.\n") ;
		    for(int i=msp; i <= msp-1+n; i++)
			memory[i] = v;
		    msp += n;
		    }
		    break;

		//DUP: push the top of the stack
		case DUP:
		    spush();
		    memory[msp-1] = topm1();
		    break;

		//BR: branch to the address on the top of the stack
		case BR:
		    pc = memory[msp-1];   //BR sets pc directly
		    spop();
		    break;

		//BF: branch to address atop the stack if the next-to-the-top
		//value is MACHINE_FALSE
		case BF:
		    {
		    short adr = memory[msp-1];
		    spop();
		    short v = memory[msp-1];
		    //  rangeCheck( v , MACHINE_FALSE, MACHINE_TRUE ,
		    //     "BF argument is not a Boolean value" );
		    spop();
		    if(v == MACHINE_FALSE)
			pc = (short)adr; 
		    else  //BF sets pc directly
			pc++;
		    }
		    break;

		//NEG: arithmetic negation of top of stack
		case NEG:
		    memory[msp-1] = (short)(- memory[msp-1]);
		    if( memory[msp-1] == UNDEFINED ){
			runError("Arithmetic underflow - NEG operator" );
			return ;
		    }
		    break;

		/* ADD, SUB, MUL, DIV, EQ, LT, OR: arithmetic and logical
		* operations.  If the top of the stack is y, and the next item
		* down is x, then OP (where OP=ADD, SUB, ... , OR)
		* performs x OP y.  Some meager overflow checking is done.
		*/

		case ADD:
		    {
		    int atemp = topm1() + top();
		    rangeCheck( atemp, MIN_INTEGER, MAX_INTEGER ,
			"ADD operator overflow or underflow" ) ;
		    spop();
		    memory[msp-1] = (short) atemp ;
		    break;
		    }

		case SUB:
		    {
		    int atemp = topm1() - top() ;
		    rangeCheck( atemp, MIN_INTEGER, MAX_INTEGER ,
			"SUB operator overflow or underflow" ) ;
		    spop();
		    memory[msp-1] = (short) atemp ;
		    break;
		    }

		case MUL:
		    {
		    int atemp = topm1() * top() ;
		    rangeCheck( atemp, MIN_INTEGER, MAX_INTEGER ,
			"MUL operator overflow or underflow" ) ;
		    spop();
		    memory[msp-1] = (short) atemp ;
		    break;
		    }

		case DIV:
		    {
		    int atemp ;
		    short v = memory[msp-1];
		    spop();
		    if(v != 0)
			atemp = memory[msp-1] / v;
		    else{
			runError("Attempt to divide by zero.\n" );
			atemp = 0 ;	// keep javac happy, never executed
		    }
		    rangeCheck( atemp, MIN_INTEGER, MAX_INTEGER ,
			"DIV operator overflow or underflow" ) ;
		    memory[msp-1] = (short) atemp ;
		    break;
		    }

		case EQ:
		    spop();
		    memory[msp-1]
			= (short)(memory[msp-1] == topp1()
					    ? MACHINE_TRUE : MACHINE_FALSE);
		    break;

		case LT:
		    spop();
		    memory[msp-1]
			= (short)(memory[msp-1] < topp1()
					    ? MACHINE_TRUE : MACHINE_FALSE);
		    break;

		case OR:
		    spop();
		    rangeCheck( memory[msp-1], MACHINE_FALSE , MACHINE_TRUE ,
			"OR operand is not a Boolean value" );
		    rangeCheck( memory[msp], MACHINE_FALSE , MACHINE_TRUE ,
			"OR operand is not a Boolean value" );
		    memory[msp-1]
			= (short)((memory[msp-1] == MACHINE_TRUE
				    || memory[msp] ==  MACHINE_TRUE)
					    ? MACHINE_TRUE : MACHINE_FALSE);
		    break;

		/* SWAP: swap the top two stack items.  Quite useful in
		* implementing other arithmetic/boolean operations efficiently
		*/
		case SWAP:
		    {
		    short v = topm1();
		    memory[msp-2] = memory[msp-1];
		    memory[msp-1] = v;
		    }
		    break;

		/*READC: machine input operation.  One character of input
		* is read, and pushed to the top of the stack
		*/
		case READC:
		    spush();
		    memory[msp-1] = (short)(inputSource.readChar() & CHARMASK) ;
		    break;

		/* PRINTC: print the top of the stack as a character, and
		* pop the stack.  Used for implementing output functions.
		*/
		case PRINTC:
		    System.out.print((char)(memory[msp-1] & CHARMASK ));
		    spop();
		    break;

		/** READI: read an integer up to the next non-integer, and
		 * push this integer to the top of the stack.  See the
		 * machine description handout for more details.
		 */
		case READI:
		    intInput = inputSource.readInt() ;
		    rangeCheck(intInput , MIN_INTEGER,  MAX_INTEGER ,
			    "READI: Integer input out of range") ;
		    spush();
		    memory[msp-1] = (short) intInput ;
		    break;

		/** PRINTI: print the top of the stack as an integer, and
		 *  pop the stack  */
		case PRINTI:
		    System.out.print(memory[msp-1]);
		    spop();
		    break;

		//HALT: halt execution
		case HALT:
		    executing = false;
		    break;

		//TRON: start tracing machine execution
		case TRON:
		    tracing = true;
		    dumpMachineState("Start trace (TRON).\n", pc, msp, mlp);
		    break;

		//TROFF: stop tracing machine execution
		case TROFF:
		    tracing = false;
		    dumpMachineState("End trace (TROFF).\n", pc, msp, mlp);
		    break;

		//ILIMIT V: Set instruction count limit to V
		case ILIMIT:
		    iLimit = memory[pc + 1];
		    if( iLimit > 0 ) {
                        counting = true ;
                        iCount = 0 ;
                    }
                    else {
                        counting = false ;
		        iLimit = Integer.MAX_VALUE ;	// set to safe value
		    }
		    break;

		default:
		    runError("Illegal instruction code.\n");
		    break;
		}	
	//end of switch on instruction code

	    //update program counter to next instruction
	    pc += instructionLength[opCode];
	    }

	//End interpreter main loop

	// Clean up after execution
	dumpMachineState("End Execution.\n", pc, msp, mlp);

	return ;

    }   // Machine execute

}	// Machine class
