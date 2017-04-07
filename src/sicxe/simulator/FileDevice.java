package sicxe.simulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class FileDevice extends Device {
	
	private RandomAccessFile file;
	private String filename;
	
	public FileDevice(String filename) {
		this.filename = filename;
	}
	
	@Override
	public byte read() {
		if(this.file == null) {
			try {
				this.file = new RandomAccessFile(filename, "rw");
			} catch (FileNotFoundException e) {
				System.err.println("Datoteka ne more biti ustvarjena.");
			}
		}
		try {
			return this.file.readByte();
		} catch (IOException e) {
			ioReadException();
		}
		
		return 0;
	}
	
	@Override
	public void write(byte val) {
		if(this.file == null) {
			try {
				this.file = new RandomAccessFile(filename, "rw");
			} catch (FileNotFoundException e) {
				System.err.println("Datoteka ne more biti ustvarjena.");
			}
		}
		try {
			this.file.write(val);
		} catch (IOException e) {
			ioWriteException();
		}
	}
	
	@Override
	public boolean test() {
		return true;
	}
	
	public static void ioReadException() {
		System.err.println("IOException happened during reading from file");
	}
	
	public static void ioWriteException() {
		System.err.println("IOException happened during writing to file");
	}
}
