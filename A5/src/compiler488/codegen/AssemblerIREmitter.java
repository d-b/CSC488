package compiler488.codegen;

import compiler488.codegen.Operand.OperandType;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;

class AssemblerMachineEmitter {
	protected Emitter emitter;

	public Emitter getEmitter() {
		return emitter;
	}

	public void setEmitter(Emitter emitter) {
		this.emitter = emitter;
	}
	
	@Processor(target="HALT", operands={}, size=1)
	public void emitHalt(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.HALT);
	}
	
	@Processor(target="ADDR", operands={OperandType.OPERAND_INTEGER, OperandType.OPERAND_INTEGER}, size=3)
	public void emitAddr(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.ADDR, ins.val(0), ins.val(1));
	}
	
	@Processor(target="LOAD", operands={}, size=1)
	public void emitLoad(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.LOAD);
	}
	
	@Processor(target="STORE", operands={}, size=1)
	public void emitStore(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.STORE);
	}
	
	@Processor(target="PUSH", operands={OperandType.OPERAND_INTEGER}, size=2)
	public void emitPush(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.PUSH, ins.val(0));
	}
		
	@Processor(target="PUSHMT", operands={}, size=1)
	public void emitPushMt(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.PUSHMT);
	}

	@Processor(target="SETD", operands={OperandType.OPERAND_INTEGER}, size=2)
	public void emitSetD(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.SETD, ins.val(0));
	}

	@Processor(target="POP", operands={}, size=1)
	public void emitPop(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.POP);
	}
	
	@Processor(target="POPN", operands={}, size=1)
	public void emitPopN(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.POPN);
	}
	
	@Processor(target="DUP", operands={}, size=1)
	public void emitDup(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.DUP);
	}
	
	@Processor(target="DUPN", operands={}, size=1)
	public void emitDupN(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.DUPN);
	}
	
	@Processor(target="BR", operands={}, size=1)
	public void emitBr(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.BR);
	}
	
	@Processor(target="BF", operands={}, size=1)
	public void emitBf(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.BF);
	}
	
	@Processor(target="NEG", operands={}, size=1)
	public void emitNeg(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.NEG);
	}
	
	@Processor(target="ADD", operands={}, size=1)
	public void emitAdd(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.ADD);
	}
	
	@Processor(target="SUB", operands={}, size=1)
	public void emitSub(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.SUB);
	}
	
	@Processor(target="MUL", operands={}, size=1)
	public void emitMul(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.MUL);
	}
	
	@Processor(target="DIV", operands={}, size=1)
	public void emitDiv(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.DIV);
	}
	
	@Processor(target="EQ", operands={}, size=1)
	public void emitEq(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.EQ);
	}
	
	@Processor(target="LT", operands={}, size=1)
	public void emitLt(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.LT);
	}
	
	@Processor(target="OR", operands={}, size=1)
	public void emitOr(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.OR);
	}
	
	@Processor(target="SWAP", operands={}, size=1)
	public void emitSwap(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.SWAP);
	}
	
	@Processor(target="READC", operands={}, size=1)
	public void emitReadC(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.READC);
	}
	
	@Processor(target="PRINTC", operands={}, size=1)
	public void emitPrintC(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.PRINTC);
	}
	
	@Processor(target="READI", operands={}, size=1)
	public void emitReadI(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.READI);
	}
	
	@Processor(target="PRINTI", operands={}, size=1)
	public void emitPrintI(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.PRINTI);
	}
	
	@Processor(target="TRON", operands={}, size=1)
	public void emitTraceOn(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.TRON);
	}
	
	@Processor(target="TROFF", operands={}, size=1)
	public void emitTraceOff(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.TROFF);
	}

	@Processor(target="ILIMIT", operands={OperandType.OPERAND_INTEGER}, size=2)
	public void emitILimit(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.ILIMIT, ins.val(0));
	}
}

public class AssemblerIREmitter extends AssemblerMachineEmitter {
	@Processor(target="PUTSTR", operands={OperandType.OPERAND_STRING}, size=23)
	void emitPutString(Instruction ins) throws MemoryAddressException {
		// strings are in Pascal Convention
		String arg = ins.str(0);
		short strAddr = emitter.addConstant(arg);
		emitter.emit(Machine.PUSH, (short) 0);
		short topLabel = emitter.emit(Machine.DUP);
		emitter.emit(Machine.PUSH, strAddr);
		emitter.emit(Machine.LOAD);
		emitter.emit(Machine.SWAP);
		emitter.emit(Machine.LT);
		emitter.emit(Machine.PUSH, (short) (topLabel + 19));
		emitter.emit(Machine.BF);
		emitter.emit(Machine.DUP);
		emitter.emit(Machine.PUSH, (short) (strAddr + 1));
		emitter.emit(Machine.ADD);
		emitter.emit(Machine.LOAD);
		emitter.emit(Machine.PRINTC);
		emitter.emit(Machine.PUSH, (short) 1);
		emitter.emit(Machine.ADD);
		emitter.emit(Machine.PUSH, (short) topLabel);
		emitter.emit(Machine.BR);
	}
	
	@Processor(target="SETUPCALL", operands={OperandType.OPERAND_INTEGER}, size=4)
	void emitSetupCall(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		// assuming later arguments are pushed using push, plus a push address and BR instruction
		short base = emitter.emit(Machine.PUSH, (short) 0);
		emitter.emit(Machine.PUSH, (short) (base + ins.val(0) * 2 + 7));
	}
	
	@Processor(target="JMP", operands={OperandType.OPERAND_INTEGER}, size=3)
	void emitJump(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.PUSH, ins.val(0));
		emitter.emit(Machine.BR);
	}
	
	@Processor(target="BFALSE", operands={OperandType.OPERAND_INTEGER}, size=3)
	void emitBFalse(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.PUSH, ins.val(0));
		emitter.emit(Machine.BF);
	}
	
	@Processor(target="NOT", operands={}, size=3)
	void emitNot(Instruction ins) throws MemoryAddressException {
		emitter.emit(Machine.PUSH, Machine.MACHINE_FALSE);
		emitter.emit(Machine.EQ);
	}
	
	@Processor(target="SAVECTX", operands={OperandType.OPERAND_INTEGER}, size=6)
	void emitSaveCtx(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.ADDR, ins.val(0), (short) 0);
		emitter.emit(Machine.PUSHMT);
		emitter.emit(Machine.SETD, ins.val(0));
	}
	
	@Processor(target="RESTORECTX", operands={OperandType.OPERAND_INTEGER, OperandType.OPERAND_INTEGER}, size=9)
	void emitRestoreCtx(Instruction ins) throws LabelNotResolvedError, MemoryAddressException {
		short LL = ins.val(0);
		short nargs = ins.val(1);
		emitter.emit(Machine.ADDR, LL, (short) 0);
		emitter.emit(Machine.SETD, LL);
		emitter.emit(Machine.POP);
		emitter.emit(Machine.PUSH, nargs);
		emitter.emit(Machine.POPN);
	}
	
	@Processor(target="RESERVE", operands={OperandType.OPERAND_INTEGER}, size=5)
	void emitReserve(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
		emitter.emit(Machine.PUSH, (short) 0);
		emitter.emit(Machine.PUSH, ins.val(0));
		emitter.emit(Machine.DUPN);
	}
}
