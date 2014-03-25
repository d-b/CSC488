package compiler488.codegen;

import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;

/**
 * CodeEmitter, emit the code to the Machine class. It can figure out the
 * long constants used in the IR, and construct the Constant Pool 
 * automatically.
 * 
 * @author Mike
 *
 */
public class Emitter {
	private short codeSec = 0;
	private short dataSec = -1;
	private short codeCur = 0;
	private short dataCur = 0;
	
	public void emit(short op) throws MemoryAddressException {
		Machine.writeMemory((short) (codeSec + codeCur), op);
		codeCur++;
	}
	public void emit(short op, short v0) throws MemoryAddressException {
		emit(op); emit(v0);
	}
	public void emit(short op, short v0, short v1) throws MemoryAddressException {
		emit(op); emit(v0); emit(v0);
	}
	
	public short addConstant(short constant[]) throws MemoryAddressException {
		short base = (short) (dataSec + dataCur);
		for (int i = 0; i < constant.length; i++) {
			Machine.writeMemory((short) (base + i), constant[i]);
		}
		dataCur += constant.length;
		return base;
	}
	
	public void setDataSection(short offset) {
		dataSec = offset;
	}
}
