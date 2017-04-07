package SICXE.src;

public class Machine {
	
	private int A, X, L, B, S, T, PC, SW;
	private double F;
	public final int A_NO = 0;
	public final int X_NO = 1;
	public final int L_NO = 2;
	public final int B_NO = 3;
	public final int S_NO = 4;
	public final int T_NO = 5;
	public final int F_NO = 6;
	public final int MAX_ADDRESS = 0xFFFFF;
	protected byte[] mem = new byte[MAX_ADDRESS];
	private Device[] devices = new Device[256];
	ProcessorClk clk;
	
	public Machine() {
		A = X = L = B = S = T = PC = SW = 0;
		devices[0] = new InputDevice(System.in);	// standardni vhod
		devices[1] = new OutputDevice(System.out);	// standardni izhod
		devices[2] = new OutputDevice(System.err);	// standardni izhod za napake
		for (int i = 3; i < devices.length; i++) {
			devices[i] = new FileDevice(Integer.toHexString(i) + ".dev");
		}
	}
	
	public byte[] getMem() {
		return mem;
	}
	
	public void setProcessorClk(ProcessorClk clk) {
		this.clk = clk;
	}
	
	public ProcessorClk getProcessorClk() {
		return clk;
	}
	
	public int getA() {
		return A;
	}
	public void setA(int val) {
		A = val & 0xFFFFFF;
	}
	
	public int getX() {
		return X;
	}
	public void setX(int val) {
		X = val & 0xFFFFFF;
	}
	
	public int getL() {
		return L;
	}
	public void setL(int val) {
		L = val & 0xFFFFFF;
	}
	
	public int getB() {
		return B;
	}
	public void setB(int val) {
		B = val & 0xFFFFFF;
	}
	
	public int getS() {
		return S;
	}
	public void setS(int val) {
		S = val & 0xFFFFFF;
	}
	
	public int getT() {
		return T;
	}
	public void setT(int val) {
		T = val & 0xFFFFFF;
	}
	
	public int getPC() {
		return PC;
	}
	public void setPC(int val) {
		PC = val & MAX_ADDRESS;
	}
	public int nextPC() {
		PC++;
		return PC;
	}
	
	public int getSW() {
		return SW;
	}
	public void setSW(int val) {
		if (val == -1){
			SW = 0x40;
		} else if (val == 0){
			SW = 0x0;
		} else if (val == 1){
			SW = 0x80;
		}
	}
	
	public double getF() {
		return F;
	}
	public void setF(double val) {
		F = val;
	}
	
	public int getReg(int regno) {
		switch (regno) {
			case A_NO:
				return getA();
			case X_NO:
				return getX();
			case L_NO:
				return getL();
			case B_NO:
				return getB();
			case S_NO:
				return getS();
			case T_NO:
				return getT();
			case F_NO:
				return (int)getF();
			default:
				registerNotFound(regno);
				return -1;
		}
	}
	public void setReg(int regno, int val) {
		switch (regno) {
			case A_NO:
				setA(val);
				break;
			case X_NO:
				setX(val);
				break;
			case L_NO:
				setL(val);
				break;
			case B_NO:
				setB(val);
				break;
			case S_NO:
				setS(val);
				break;
			case T_NO:
				setT(val);
				break;
			case F_NO:
				setF(val);
				break;
			default:
				registerNotFound(regno);
		}
	}
	public String getRegName(int regno) {
		switch (regno) {
			case A_NO:
				return "A";
			case X_NO:
				return "X";
			case L_NO:
				return "L";
			case B_NO:
				return "B";
			case S_NO:
				return "S";
			case T_NO:
				return "T";
			case F_NO:
				return "F";
			default:
				registerNotFound(regno);
				return "";
		}
	}
	
	public int getByte(int addr) {
		if (addr >= 0 && addr <= MAX_ADDRESS){
			return ((int)mem[addr]) & 0xFF;
		}
		
		invalidAddressing();
		return -1;
	}
	public void setByte(int addr, int val) {
		if (addr >= 0 && addr <= MAX_ADDRESS){
			mem[addr] = (byte)(val & 0xFF);
			if(addr >= TextualScreen.TEXT_SCREEN_START && addr < TextualScreen.TEXT_SCREEN_END) {
				TextualScreen.updateTextualScreen(addr, this);
			}
			return;
		}
		
		invalidAddressing();
	}
	
	public int getWord(int addr) {
		int word = 0x0;
		for(int i = 0; i < 3; i++){
			word = word << 8;
			word = word | getByte(addr + i);
		}
		return word;
	}
	public void setWord(int addr, int val) {
		for(int i = 2; i >= 0; i--){
			setByte(addr + i, val);
			val = val >> 8;
		}
	}
	
	public Device getDevice(int num) {
		return devices[num];
	}
	public void setDevice(int num, Device device) {
		devices[num] = device;
	}
	
	public int getArgFromAddressing(int operand, Flags flags) {
		if(flags.isIndirect()) {
			return getWord(getWord(operand));
		} else if (flags.isSimple()) {
			return getWord(operand);
		} else {
			return operand;		// immediate
		}
	}
	
	public int getByteArgFromAddressing(int operand, Flags flags) {
		if(flags.isIndirect()) {
			return getByte(getWord(operand));
		} else if (flags.isSimple()) {
			return getByte(operand);
		} else {
			return operand;		// immediate
		}
	}
	
	public void execute() {
		int opcode = fetch();
		if(execF1(opcode)){
			return;
		}
		int op = fetch();
		if (execF2(opcode, op)) {
			return;
		}
		int operand;
		Flags flags = new Flags(opcode, op);
		
		if(flags.isSic()) {
			operand = ((op & 0x7F) << 8 ) | fetch();
		} else if (flags.isF4()) {
			operand = ((op & 0xF) << 16) | (fetch() << 8) | fetch();
			if(flags.isRelative()) {
				invalidAddressing();
			}
		} else {
			operand = ((op & 0xF) << 8) | fetch();
			if(flags.isBaseRel()) {
				operand += getB();
			} else if(flags.isPCRel()) {
				if(operand >= 2048) {
					operand -= 4096;	//odmik je negativen
				}
				operand += getPC();
			} else if(flags.isDirect()) {
				//operand stays same
			} else {
				// b=1 and p=1
				invalidAddressing();
			}
		}
		if(flags.isIndexed()) {
			if(flags.isSic() || flags.isSimple()) {
				operand += getX();
			} else {
				invalidAddressing();
			}
		}
		if (execSICF3F4(opcode, flags, operand)) {
			return;
		}
		invalidOpcode(opcode);
	}
	
	public boolean execF1(int opcode) {
		switch(opcode){
		case Opcode.FIX:
			setA((int)getF());
			break;
		case Opcode.FLOAT:
			setF((double)getA());
			break;
		case Opcode.HIO:
			notImplemented("HIO");
			break;
		case Opcode.NORM:
			notImplemented("NORM");
			break;
		case Opcode.SIO:
			notImplemented("SIO");
			break;
		case Opcode.TIO:
			notImplemented("TIO");
			break;
		default:
			return false;
	}

	return true;
	}
	
	public boolean execF2(int opcode, int op) {
		int firstOperand = op >> 4;
		int secondOperand = op & 0xF; 
		switch(opcode){
			case Opcode.CLEAR:
				setReg(firstOperand, 0);
				break;
			case Opcode.RMO:
				setReg(secondOperand, getReg(firstOperand));
				break;
			case Opcode.ADDR:
				setReg(secondOperand, getReg(secondOperand) + getReg(firstOperand));
				break;
			case Opcode.SUBR:
				setReg(secondOperand, getReg(secondOperand) - getReg(firstOperand));
				break;
			case Opcode.MULR:
				setReg(secondOperand, getReg(secondOperand) * getReg(firstOperand));
				break;
			case Opcode.DIVR:
				setReg(secondOperand, getReg(secondOperand) / getReg(firstOperand));
				break;
			case Opcode.SHIFTL:
				setReg(firstOperand, getReg(firstOperand) << (secondOperand + 1) | getReg(firstOperand) >> (24 - secondOperand - 1));
				break;
			case Opcode.SHIFTR:
				setReg(firstOperand, getReg(firstOperand) >> (secondOperand + 1));
				if((getReg(firstOperand) >> 23) == 1) {
					int and = 0;
					for (int i = 0; i < secondOperand + 1; i++) {
						and++;
						and = and << 1;
					}
					setReg(firstOperand, getReg(firstOperand) | and << (24 - secondOperand - 2));
				}
				break;
			case Opcode.COMPR:
				int swVal = 0;
				if(getReg(firstOperand) < getReg(secondOperand)) {
					swVal--;
				} else if (getReg(firstOperand) > getReg(secondOperand)) {
					swVal++;
				}
				setSW(swVal);
				break;
			case Opcode.TIXR:
				setX(getX() + 1);
				swVal = 0;
				if(getX() < getReg(secondOperand)) {
					swVal++;
				} else if (getX() > getReg(secondOperand)) {
					swVal--;
				}
				setSW(swVal);
				break;
			case Opcode.SVC:
				notImplemented("SVC");
				break;
			default:
				return false;
		}
	
		return true;
	}
	
	public boolean execSICF3F4(int opcode, Flags flags, int operand) {
		opcode &= 0xFC;	// cut n and i flags
		switch(opcode) {
			case Opcode.ADD:
				setA(getA() + getArgFromAddressing(operand, flags));
				break;
			case Opcode.ADDF:
				notImplemented("ADDF");
				break;
			case Opcode.SUB:
				setA(getA() - getArgFromAddressing(operand, flags));
				break;
			case Opcode.SUBF:
				notImplemented("SUBF");
				break;
			case Opcode.MUL:
				setA(getA() * getArgFromAddressing(operand, flags));
				break;
			case Opcode.MULF:
				notImplemented("MULF");
				break;
			case Opcode.DIV:
				setA(getA() / getArgFromAddressing(operand, flags));
				break;
			case Opcode.DIVF:
				notImplemented("DIVF");
				break;
			case Opcode.AND:
				setA(getA() & getArgFromAddressing(operand, flags));
				break;
			case Opcode.OR:
				setA(getA() | getArgFromAddressing(operand, flags));
				break;
			case Opcode.COMP:
				setSW(getA() < getArgFromAddressing(operand, flags) ? -1 : (getA() > getArgFromAddressing(operand, flags) ? 1 : 0));
				break;
			case Opcode.COMPF:
				notImplemented("COMPF");
				break;
			case Opcode.LDA:
				setA(getArgFromAddressing(operand, flags));
				break;
			case Opcode.LDB:
				setB(getArgFromAddressing(operand, flags));
				break;
			case Opcode.LDCH:
				setA(getArgFromAddressing(operand, flags) & 0xFF);
				break;
			case Opcode.LDF:
				notImplemented("LDF");
				break;
			case Opcode.LDL:
				setL(getArgFromAddressing(operand, flags));
				break;
			case Opcode.LDS:
				setS(getArgFromAddressing(operand, flags));
				break;
			case Opcode.LDT:
				setT(getArgFromAddressing(operand, flags));
				break;
			case Opcode.LDX:
				setX(getArgFromAddressing(operand, flags));
				break;
			case Opcode.LPS:
				notImplemented("LPS");
				break;
			case Opcode.STA:
				setWord(flags.isIndirect() ? getWord(operand) : operand, getA());
				break;
			case Opcode.STB:
				setWord(flags.isIndirect() ? getWord(operand) : operand, getB());
				break;
			case Opcode.STCH:
				setByte(flags.isIndirect() ? getWord(operand) : operand, getA() & 0xFF);
				break;
			case Opcode.STF:
				notImplemented("STF");
				break;
			case Opcode.STL:
				setWord(flags.isIndirect() ? getWord(operand) : operand, getL());
				break;
			case Opcode.STS:
				setWord(flags.isIndirect() ? getWord(operand) : operand, getS());
				break;
			case Opcode.STSW:
				setWord(flags.isIndirect() ? getWord(operand) : operand, getSW());
				break;
			case Opcode.STT:
				setWord(flags.isIndirect() ? getWord(operand) : operand, getT());
				break;
			case Opcode.STX:
				setWord(flags.isIndirect() ? getWord(operand) : operand, getX());
				break;
			case Opcode.J:
				setPC(flags.isIndirect() ? getWord(operand) : operand);
				break;
			case Opcode.JEQ:
				if(getSW() == 0x0) {
					setPC(flags.isIndirect() ? getWord(operand) : operand);
				}
				break;
			case Opcode.JGT:
				if(getSW() == 0x80) {
					setPC(flags.isIndirect() ? getWord(operand) : operand);
				}
				break;
			case Opcode.JLT:
				if(getSW() == 0x40) {
					setPC(flags.isIndirect() ? getWord(operand) : operand);
				}
				break;
			case Opcode.JSUB:
				setL(getPC());
				setPC(flags.isIndirect() ? getWord(operand) : operand);
				break;
			case Opcode.RSUB:
				setPC(getL());
				break;
			case Opcode.TD:
				setSW(getDevice(getByteArgFromAddressing(operand, flags)).test() ? 1 : 0);
				break;
			case Opcode.RD:
				setA(getDevice(getByteArgFromAddressing(operand, flags)).read());
				break;
			case Opcode.WD:
				getDevice(getByteArgFromAddressing(operand, flags)).write(((byte)(getA() & 0xFF)));
				break;
			case Opcode.TIX:
				setX(getX() + 1);
				int swVal = 0;
				if(getX() < getArgFromAddressing(operand, flags)) {
					swVal++;
				} else if (getX() > getArgFromAddressing(operand, flags)) {
					swVal--;
				}
				setSW(swVal);
				break;
			case Opcode.SSK:
				notImplemented("SSK");
				break;
			default:
				return false;
		}
		
		return true;
	}
	
	public int fetch() {
		int curPC = getByte(getPC());
        nextPC();
        return curPC;
	}
	
	public static void registerNotFound(int regno) {
		System.out.println("Register with number " + regno + " does not exist");
	}
	
	public static void notImplemented(String mnemonic) {
		System.err.println("Mnemonic " + mnemonic + " is not implemented");
	}
	
	public static void invalidOpcode(int opcode) {
		System.err.println("Opcode " + opcode + " is invalid");
	}
	
	public static void invalidAddressing() {
		System.err.println("Invalid address");
	}
	
}
