package sicxe.simulator;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class Utils {
	
	private static int programLen;
	
	public static String readString(Reader r, int len) throws IOException {
		StringBuilder builder = new StringBuilder();
		while(len > 0){
			builder.append((char)r.read());
			len--;
		}
		return builder.toString();
	}
	
	public static int readByte(Reader r) throws IOException {
		return Integer.parseInt(readString(r, 2), 16);
	}
	
	public static int readWord(Reader r) throws IOException {
		return Integer.parseInt(readString(r, 6), 16);
	}
	
	public static boolean loadSection(Reader r, Machine m) {
		int length = 0;
		try {
			if((char)r.read() != 'H') {
				return false;
			}
			readString(r, 6);	//program name
			int startAddress = readWord(r);
			length = readWord(r);
			programLen = length;
			skipChar(r);
			
			char zapis = (char)r.read();
			while(zapis == 'T') {
				int startAddressForT = readWord(r);
				int lengthForT = readByte(r);
				String objectCode = readString(r, lengthForT * 2);
				Reader sr = new StringReader(objectCode);
				for(int i = 0; i < lengthForT; i++) {
					int memPosition = startAddressForT + i;
					int memByte = readByte(sr);
					if(memPosition < startAddress || memPosition >= startAddress + length) {
						return false;
					}
					m.setByte(memPosition, memByte);
				}
				sr.close();
				skipChar(r);
				zapis = (char)r.read();
			}
			
			while(zapis == 'M') {
				readString(r, 8);
				skipChar(r);
				zapis = (char)r.read();
			}
			
			if(zapis == 'E') {
				int firstExecCommandAddr = readWord(r);
				m.setPC(firstExecCommandAddr);
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static void skipChar(Reader r) throws IOException{
		r.read();
	}
	
	public static int getProgramLen() {
		return programLen;
	}
	
}
