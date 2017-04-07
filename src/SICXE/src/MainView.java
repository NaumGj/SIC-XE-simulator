package SICXE.src;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainView {
	
	private static JFrame sicSim;
	private static JPanel cpuPanel;
	private static JPanel buttonPanel;
	private static JPanel disAsmPanel;
	private static JPanel memoryPanel;
	
	public static void initGUI(Machine m) {
		// Frame
		sicSim = new JFrame("Sic/XE Simulator");
		sicSim.setSize(800, 450);
		sicSim.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sicSim.setLayout(new GridBagLayout());
		
		// Panels
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		cpuPanel = new JPanel();
		cpuPanel.setLayout(new GridBagLayout());
		sicSim.add(cpuPanel, gbc);
		
		gbc.gridy = 1;
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		sicSim.add(buttonPanel, gbc);
		
		gbc.gridy = 2;
		disAsmPanel = new JPanel();
		disAsmPanel.setLayout(new GridBagLayout());
		sicSim.add(disAsmPanel, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 3;
		memoryPanel = new JPanel();
		memoryPanel.setLayout(new GridBagLayout());
		sicSim.add(memoryPanel, gbc);
		
		// Build elements inside panels
		RegistersView.buildRegistersView(cpuPanel);
		ButtonsView.buildButtonsView(buttonPanel, m);
		MemoryView.buildMemoryView(memoryPanel);
		MemoryView.setMemory((MemoryView.initMemoryView(m) + MemoryView.initRestOfMemory()));
		DisassemblyView.buildDisassemblyView(disAsmPanel, m);
		
		updateGUI(m);

		sicSim.setVisible(true);
	}
	
	public static void updateGUI(Machine m) {
		RegistersView.updateRegisters(m);
		MemoryView.updateMemory(m);
	}

}
