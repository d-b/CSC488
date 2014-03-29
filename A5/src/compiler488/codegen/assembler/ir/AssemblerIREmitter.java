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
    @Processor(target="PUSHSTR", operands={OperandType.OPERAND_STRING}, size=2)
    void emitPutString(Instruction ins) throws MemoryAddressException {
        short strAddr = emitter.addConstant(ins.str(0));
        emitter.emit(Machine.PUSH, strAddr);
    }

    @Processor(target="SETUPCALL", operands={OperandType.OPERAND_INTEGER}, size=4)
    void emitSetupCall(Instruction ins) throws MemoryAddressException, LabelNotResolvedError {
        // assuming later arguments are pushed using push, plus a push address and BR instruction
        emitter.emit(Machine.PUSH, (short) 0);
        emitter.emit(Machine.PUSH, (short) ins.val(0));
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

    @Processor(target="RESTORECTX", operands={OperandType.OPERAND_INTEGER, OperandType.OPERAND_INTEGER}, size=10)
    void emitRestoreCtx(Instruction ins) throws LabelNotResolvedError, MemoryAddressException {
        short LL = ins.val(0);
        short nargs = ins.val(1);
        emitter.emit(Machine.ADDR, LL, (short) -1);
        emitter.emit(Machine.LOAD);
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
