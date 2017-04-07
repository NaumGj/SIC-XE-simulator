package SICXE.src;

import java.io.IOException;
import java.io.InputStream;


public class InputDevice extends Device {
	
	private InputStream in;
	
	public InputDevice(InputStream in) {
		this.in = in;
	}
	
	@Override
	public byte read() {
		try {
			return (byte)(this.in.read() & 0xFF);
		} catch (IOException e) {
			ioReadException();
		}
		
		return 0;
	}
	
	public static void ioReadException() {
		System.err.println("IOException happened during reading from input device");
	}
}
