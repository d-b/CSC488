package compiler488.codegen.assembler.ir;

import java.util.HashMap;

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
	
	private HashMap<String, Short> strDedupMap;
	
	public short emit(short op) throws MemoryAddressException {
		short base = (short) (codeSec + codeCur);
		Machine.writeMemory((short) (codeSec + codeCur), op);
		codeCur++;
		return base;
	}
	public short emit(short op, short v0) throws MemoryAddressException {
		short base = emit(op); emit(v0);
		return base;
	}
	public short emit(short op, short v0, short v1) throws MemoryAddressException {
		short base = emit(op); emit(v0); emit(v0);
		return base;
	}
	
	public short addConstant(short w) throws MemoryAddressException {
		short base = (short) (dataSec + dataCur);
		Machine.writeMemory(base, w);
		dataCur++;
		return base;
	}
	public short addConstant(char constant[]) throws MemoryAddressException {
		short base = (short) (dataSec + dataCur);
		for (int i = 0; i < constant.length; i++) {
			Machine.writeMemory((short) (base + i), (short) constant[i]);
		}
		dataCur += constant.length;
		return base;
	}
	
	public short addConstant(String str) throws MemoryAddressException {
		Short addr = strDedupMap.get(str);
		if (addr != null) {
			return addr;
		}
		// OK, no strings are found in constant pool now
		// add a new one according to *Pascal Convention*.
		short base = addConstant((short) str.length());
		addConstant(str.toCharArray());
		strDedupMap.put(str, base);
		return base;
	}
	
	public void setDataSection(short offset) {
		dataSec = offset;
		// data section is ready
		strDedupMap = new HashMap<String, Short>();
	}
}
