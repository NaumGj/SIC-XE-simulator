package sicxe.simulator;

import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RegistersView {
	
	private static JLabel regA;
	private static JTextField regAField;
	private static JLabel regX;
	private static JTextField regXField;
	private static JLabel regL;
	private static JTextField regLField;
	private static JLabel regS;
	private static JTextField regSField;
	private static JLabel regT;
	private static JTextField regTField;
	private static JLabel regB;
	private static JTextField regBField;
	private static JLabel regSW;
	private static JTextField regSWField;
	private static JLabel regF;
	private static JTextField regFField;
	private static JLabel regPC;
	private static JTextField regPCField;

	public static void buildRegistersView(JPanel cpuPanel) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		regA = new JLabel();
		regA.setText("A");
		cpuPanel.add(regA, gbc);

		gbc.gridx = 1;
		regAField = new JTextField();
		regAField.setEditable(false);
		cpuPanel.add(regAField, gbc);

		gbc.gridx = 2;
		regX = new JLabel();
		regX.setText("X");
		cpuPanel.add(regX, gbc);

		gbc.gridx = 3;
		regXField = new JTextField();
		regXField.setEditable(false);
		cpuPanel.add(regXField, gbc);

		gbc.gridx = 4;
		regL = new JLabel();
		regL.setText("L");
		cpuPanel.add(regL, gbc);

		gbc.gridx = 5;
		regLField = new JTextField();
		regLField.setEditable(false);
		cpuPanel.add(regLField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		regS = new JLabel();
		regS.setText("S");
		cpuPanel.add(regS, gbc);

		gbc.gridx = 1;
		regSField = new JTextField();
		regSField.setEditable(false);
		cpuPanel.add(regSField, gbc);

		gbc.gridx = 2;
		regT = new JLabel();
		regT.setText("T");
		cpuPanel.add(regT, gbc);

		gbc.gridx = 3;
		regTField = new JTextField();
		regTField.setEditable(false);
		cpuPanel.add(regTField, gbc);

		gbc.gridx = 4;
		regB = new JLabel();
		regB.setText("B");
		cpuPanel.add(regB, gbc);

		gbc.gridx = 5;
		regBField = new JTextField();
		regBField.setEditable(false);
		cpuPanel.add(regBField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		regSW = new JLabel();
		regSW.setText("SW");
		cpuPanel.add(regSW, gbc);

		gbc.gridx = 1;
		regSWField = new JTextField();
		regSWField.setEditable(false);
		cpuPanel.add(regSWField, gbc);

		gbc.gridx = 2;
		regF = new JLabel();
		regF.setText("F");
		cpuPanel.add(regF, gbc);

		gbc.gridx = 3;
		gbc.gridwidth = 2;
		regFField = new JTextField();
		regFField.setEditable(false);
		cpuPanel.add(regFField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		regPC = new JLabel();
		regPC.setText("PC");
		cpuPanel.add(regPC, gbc);

		gbc.gridx = 1;
		regPCField = new JTextField();
		regPCField.setEditable(false);
		cpuPanel.add(regPCField, gbc);
	}
	
	public static void updateRegisters(Machine m) {
		regAField.setText(String.format("%6S\t", Integer.toHexString(m.getA())).replace(' ', '0'));
		regXField.setText(String.format("%6S\t", Integer.toHexString(m.getX())).replace(' ', '0'));
		regLField.setText(String.format("%6S\t", Integer.toHexString(m.getL())).replace(' ', '0'));
		regSField.setText(String.format("%6S\t", Integer.toHexString(m.getS())).replace(' ', '0'));
		regTField.setText(String.format("%6S\t", Integer.toHexString(m.getT())).replace(' ', '0'));
		regBField.setText(String.format("%6S\t", Integer.toHexString(m.getB())).replace(' ', '0'));
		regSWField.setText(String.format("%6S\t", Integer.toHexString(m.getSW())).replace(' ', '0'));
		regFField.setText(String.format("%11S", Integer.toHexString((int)m.getF())).replace(' ', '0'));
		regPCField.setText(String.format("%6S\t", Integer.toHexString(m.getPC())).replace(' ', '0'));
	}
	
}
