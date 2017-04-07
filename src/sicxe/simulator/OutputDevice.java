package sicxe.simulator;

import java.io.IOException;
import java.io.OutputStream;


public class OutputDevice extends Device {
	
	private OutputStream out;
	
	public OutputDevice(OutputStream out) {
		this.out = out;
	}
	
	@Override
	public void write(byte val) {
		try {
			out.write(val);
			out.flush();
		} catch (IOException e) {
			ioWriteException();
		}
	}
	
	public static void ioWriteException() {
		System.err.println("IOException happened during writing to device");
	}

}
