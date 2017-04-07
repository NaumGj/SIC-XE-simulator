package SICXE.src;

public class Flags {

	private boolean n;
	private boolean i;
	private boolean x;
	private boolean b;
	private boolean p;
	private boolean e;
	
	public Flags(int opcode, int op) {
		n = ((opcode >> 1) & 0x1) == 1;
		i = (opcode & 0x1) == 1;
		x = ((op >> 7) & 0x1) == 1;
		b = ((op >> 6) & 0x1) == 1;
		p = ((op >> 5) & 0x1) == 1;
		e = ((op >> 4) & 0x1) == 1;
	}
	
	public boolean isSic() {
		return !n && !i;
	}
	
	public boolean isSimple() {
		return n && i;
	}
	
	public boolean isRelative() {
		return b || p;
	}
	
	public boolean isF4() {
		return e;
	}
	
	public boolean isIndexed() {
		return x;
	}
	
	public boolean isBaseRel() {
		return b && !p;
	}
	
	public boolean isPCRel() {
		return !b && p;
	}
	
	public boolean isDirect() {
		return !b && !p;
	}
	
	public boolean isIndirect() {
		return n && !i;
	}
	
	public boolean isImmediate() {
		return !n && i;
	}
}
