package sicxe.simulator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class Main {
	
	private static Machine m;
	
	public static void main(String[] args) {
		// Make a machine
		m = new Machine();
		// Load object file
		Reader r = null;
		try {
			r = new FileReader(args[0]);
		} catch (FileNotFoundException e) {
			fileNotFound();
		}
		// Fill memory
		if (Utils.loadSection(r, m)){
			System.out.println("Object file loaded");
		}
		// Set processor clock for the machine
		ProcessorClk clk = new ProcessorClk();
		m.setProcessorClk(clk);
		clk.setMachine(m);
		// Build GUI
		MainView.initGUI(m);
	}
	
	public static void fileNotFound() {
		System.err.println("File not found");
	}
}
