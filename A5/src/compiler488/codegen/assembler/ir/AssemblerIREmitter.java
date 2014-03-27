package compiler488.codegen.assembler.ir;

import compiler488.codegen.assembler.Processor;
import compiler488.codegen.assembler.Instruction;
import compiler488.codegen.assembler.Operand.OperandType;
import compiler488.codegen.assembler.LabelNotResolvedError;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;

/**
 * @author Mike
 */
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
        emitter.emit(Machine.LT);
        emitter.emit(Machine.PUSH, (short) (topLabel + 21));
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
        emitter.emit(Machine.POP);
    }

    @Processor(target="PUTNEWLINE", operands={}, size=3)
    void emitPutNewLine(Instruction ins) throws MemoryAddressException {
        emitter.emit(Machine.PUSH, (short) '\n');
        emitter.emit(Machine.PRINTC);
    }

    @Processor(target="SETUPCALL", operands={OperandType.OPERAND_INTEGER}, size=4)
    void emitSetupCall(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
        // assuming later arguments are pushed using push, plus a push address and BR instruction
        emitter.emit(Machine.PUSH, (short) 0);
        emitter.emit(Machine.PUSH, (short) (ins.val(0) + 1));
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
