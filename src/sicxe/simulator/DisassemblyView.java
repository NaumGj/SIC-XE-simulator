package sicxe.simulator;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class DisassemblyView {
	
	private static JTable disAsmTable;
	private static JScrollPane disAsmScroll;
	private static Machine m;
	private static int virtualPC = 0;

	public static void buildDisassemblyView(JPanel disAsmPanel, Machine machine) {
		m = machine;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		disAsmTable = new JTable(Utils.getProgramLen(), 4);
		disAsmTable.setTableHeader(null);
		disAsmTable.getColumnModel().getColumn(0).setPreferredWidth(60);
		disAsmTable.getColumnModel().getColumn(2).setPreferredWidth(60);
		fillDisAsmTable();
		disAsmScroll = new JScrollPane(disAsmTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		disAsmScroll.setPreferredSize(new Dimension(330, 200));
		disAsmPanel.add(disAsmScroll, gbc);
	}
	
	private static void fillDisAsmTable() {
		int i = 0;
		while (virtualPC < Utils.getProgramLen()) {
			fillDisAsmTableRow(i);
			i++;
		}
		
	}
	
	private static String getArgFromAddressing(int operand, Flags flags) {
		if(flags.isIndirect()) {
			return Integer.toString(m.getWord(m.getWord(operand)));
		} else if (flags.isSimple()) {
			return Integer.toString(m.getWord(operand));
		} else {
			return Integer.toString(operand);		// immediate
		}
	}
	
	private static void fillDisAsmTableRow(int row) {
		disAsmTable.getModel().setValueAt(String.format("%5S\t", Integer.toHexString(virtualPC)).replace(' ', '0'), row, 0);
		int opcode = fetch();
		if(fillF1(opcode, row)){
			disAsmTable.getModel().setValueAt(String.format("%S\t", Integer.toHexString(opcode)), row, 1);
			disAsmTable.getModel().setValueAt(Opcode.opcodeToMnemonic.get(opcode), row, 2);
			return;
		}
		int op = fetch();
		if (fillF2(opcode, op, row)) {
			disAsmTable.getModel().setValueAt(String.format("%S\t", Integer.toHexString(opcode << 8 | op)), row, 1);
			disAsmTable.getModel().setValueAt(Opcode.opcodeToMnemonic.get(opcode), row, 2);
			return;
		}
		int operand;
		Flags flags = new Flags(opcode, op);
		String column4 = "";
		if(flags.isImmediate()) {
			column4 += "#";
		}
		if(flags.isIndirect()) {
			column4 += "@";
		}
		if(flags.isPCRel()) {
			column4 += "(PC)+";
		}
		if(flags.isBaseRel()) {
			column4 += "(B)+";
		}
		disAsmTable.getModel().setValueAt(String.format("%6S", Integer.toHexString(opcode << 16 | op << 8 | fetchWithoutPCIncrement(virtualPC))).replace(' ', '0'), row, 1);
		if(flags.isSic()) {
			operand = ((op & 0x7F) << 8 ) | fetch();
		} else if (flags.isF4()) {
			disAsmTable.getModel().setValueAt((disAsmTable.getModel().getValueAt(row, 1)) + String.format("%2S", Integer.toHexString(fetchWithoutPCIncrement(virtualPC + 1))).replace(' ', '0'), row, 1);
			operand = ((op & 0xF) << 16) | (fetch() << 8) | fetch();
			if(flags.isRelative()) {
				Machine.invalidAddressing();
			}
		} else {
			operand = ((op & 0xF) << 8) | fetch();
			if(flags.isBaseRel()) {
				operand += m.getB();
			} else if(flags.isPCRel()) {
				if(operand >= 2048) {
					operand -= 4096;	//odmik je negativen
				}
				operand += m.getPC();
			} else if(flags.isDirect()) {
				//operand stays same
			} else {
				// b=1 and p=1
				Machine.invalidAddressing();
			}
		}
		if(flags.isIndexed()) {
			if(flags.isSic() || flags.isSimple()) {
				operand += m.getX();
			} else {
				Machine.invalidAddressing();
			}
		}
		if(fillSICF3F4(opcode, flags, operand, row, column4)){
			disAsmTable.getModel().setValueAt(Opcode.opcodeToMnemonic.get(opcode & 0xFC), row, 2);
			if(flags.isIndexed()) {
				disAsmTable.getModel().setValueAt(disAsmTable.getModel().getValueAt(row, 3) + ",X", row, 3);
			}
			return;
		}
	}
	
	private static boolean fillF1(int opcode, int row) {
		switch(opcode){
			case Opcode.FIX:
				break;
			case Opcode.FLOAT:
				break;
			case Opcode.HIO:
				Machine.notImplemented("HIO");
				break;
			case Opcode.NORM:
				Machine.notImplemented("NORM");
				break;
			case Opcode.SIO:
				Machine.notImplemented("SIO");
				break;
			case Opcode.TIO:
				Machine.notImplemented("TIO");
				break;
			default:
				return false;
		}

		return true;
	}
	
	private static boolean fillF2(int opcode, int op, int row) {
		int firstOperand = op >> 4;
		int secondOperand = op & 0xF; 
		switch(opcode){
			case Opcode.CLEAR:
				disAsmTable.getModel().setValueAt(m.getRegName(firstOperand), row, 3);
				break;
			case Opcode.RMO:
				disAsmTable.getModel().setValueAt(m.getRegName(firstOperand) + "," + m.getRegName(secondOperand), row, 3);
				break;
			case Opcode.ADDR:
				disAsmTable.getModel().setValueAt(m.getRegName(firstOperand) + "," + m.getRegName(secondOperand), row, 3);
				break;
			case Opcode.SUBR:
				disAsmTable.getModel().setValueAt(m.getRegName(firstOperand) + "," + m.getRegName(secondOperand), row, 3);
				break;
			case Opcode.MULR:
				disAsmTable.getModel().setValueAt(m.getRegName(firstOperand) + "," + m.getRegName(secondOperand), row, 3);
				break;
			case Opcode.DIVR:
				disAsmTable.getModel().setValueAt(m.getRegName(firstOperand) + "," + m.getRegName(secondOperand), row, 3);
				break;
			case Opcode.SHIFTL:
				disAsmTable.getModel().setValueAt(m.getRegName(firstOperand) + "," + Integer.toString(secondOperand), row, 3);
				break;
			case Opcode.SHIFTR:
				disAsmTable.getModel().setValueAt(m.getRegName(firstOperand) + "," + Integer.toString(secondOperand), row, 3);
				break;
			case Opcode.COMPR:
				disAsmTable.getModel().setValueAt(m.getRegName(firstOperand) + "," + m.getRegName(secondOperand), row, 3);
				break;
			case Opcode.TIXR:
				disAsmTable.getModel().setValueAt(m.getRegName(firstOperand), row, 3);
				break;
			case Opcode.SVC:
				Machine.notImplemented("SVC");
				break;
			default:
				return false;
		}
	
		return true;
	}
	
	private static boolean fillSICF3F4(int opcode, Flags flags, int operand, int row, String column4) {
		opcode &= 0xFC;	// cut n and i flags
		switch(opcode) {
			case Opcode.ADD:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.ADDF:
				Machine.notImplemented("ADDF");
				break;
			case Opcode.SUB:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.SUBF:
				Machine.notImplemented("SUBF");
				break;
			case Opcode.MUL:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.MULF:
				Machine.notImplemented("MULF");
				break;
			case Opcode.DIV:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.DIVF:
				Machine.notImplemented("DIVF");
				break;
			case Opcode.AND:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.OR:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.COMP:
				disAsmTable.getModel().setValueAt(column4 + getArgFromAddressing(operand, flags), row, 3);
				break;
			case Opcode.COMPF:
				Machine.notImplemented("COMPF");
				break;
			case Opcode.LDA:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.LDB:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.LDCH:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.LDF:
				Machine.notImplemented("LDF");
				break;
			case Opcode.LDL:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.LDS:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.LDT:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.LDX:
				disAsmTable.getModel().setValueAt((operand < 0) ? (column4.substring(0, column4.length() - 1) + "-" + String.valueOf(operand)) : (column4 + String.valueOf(operand)), row, 3);
				break;
			case Opcode.LPS:
				Machine.notImplemented("LPS");
				break;
			case Opcode.STA:
				disAsmTable.getModel().setValueAt(column4 + Integer.toString(operand), row, 3);
				break;
			case Opcode.STB:
				disAsmTable.getModel().setValueAt(column4 + Integer.toString(operand), row, 3);
				break;
			case Opcode.STCH:
				disAsmTable.getModel().setValueAt(column4 + Integer.toString(operand), row, 3);
				break;
			case Opcode.STF:
				Machine.notImplemented("STF");
				break;
			case Opcode.STL:
				disAsmTable.getModel().setValueAt(column4 + Integer.toString(operand), row, 3);
				break;
			case Opcode.STS:
				disAsmTable.getModel().setValueAt(column4 + Integer.toString(operand), row, 3);
				break;
			case Opcode.STSW:
				disAsmTable.getModel().setValueAt(column4 + Integer.toString(operand), row, 3);
				break;
			case Opcode.STT:
				disAsmTable.getModel().setValueAt(column4 + Integer.toString(operand), row, 3);
				break;
			case Opcode.STX:
				disAsmTable.getModel().setValueAt(column4 + Integer.toString(operand), row, 3);
				break;
			case Opcode.J:
				String offset = (flags.isIndirect() ? Integer.toString(m.getWord(operand)) : Integer.toString(operand));
				if(column4.isEmpty()) {
					disAsmTable.getModel().setValueAt(offset, row, 3);
				} else {
					if(Integer.parseInt(offset) < 0) {
						disAsmTable.getModel().setValueAt((column4.substring(0, column4.length() - 1) + String.valueOf(offset)), row, 3);
					} else {
						disAsmTable.getModel().setValueAt(column4 + offset, row, 3);
					}
				}
				break;
			case Opcode.JEQ:
				offset = (flags.isIndirect() ? Integer.toString(m.getWord(operand)) : Integer.toString(operand));
				if(column4.isEmpty()) {
					disAsmTable.getModel().setValueAt(offset, row, 3);
				} else {
					if(Integer.parseInt(offset) < 0) {
						disAsmTable.getModel().setValueAt((column4.substring(0, column4.length() - 1) + String.valueOf(offset)), row, 3);
					} else {
						disAsmTable.getModel().setValueAt(column4 + offset, row, 3);
					}
				}
				break;
			case Opcode.JGT:
				offset = (flags.isIndirect() ? Integer.toString(m.getWord(operand)) : Integer.toString(operand));
				if(column4.isEmpty()) {
					disAsmTable.getModel().setValueAt(offset, row, 3);
				} else {
					if(Integer.parseInt(offset) < 0) {
						disAsmTable.getModel().setValueAt((column4.substring(0, column4.length() - 1) + String.valueOf(offset)), row, 3);
					} else {
						disAsmTable.getModel().setValueAt(column4 + offset, row, 3);
					}
				}
				break;
			case Opcode.JLT:
				offset = (flags.isIndirect() ? Integer.toString(m.getWord(operand)) : Integer.toString(operand));
				if(column4.isEmpty()) {
					disAsmTable.getModel().setValueAt(offset, row, 3);
				} else {
					if(Integer.parseInt(offset) < 0) {
						disAsmTable.getModel().setValueAt((column4.substring(0, column4.length() - 1) + String.valueOf(offset)), row, 3);
					} else {
						disAsmTable.getModel().setValueAt(column4 + offset, row, 3);
					}
				}
				break;
			case Opcode.JSUB:
				offset = (flags.isIndirect() ? Integer.toString(m.getWord(operand)) : Integer.toString(operand));
				if(column4.isEmpty()) {
					disAsmTable.getModel().setValueAt(offset, row, 3);
				} else {
					if(Integer.parseInt(offset) < 0) {
						disAsmTable.getModel().setValueAt((column4.substring(0, column4.length() - 1) + String.valueOf(offset)), row, 3);
					} else {
						disAsmTable.getModel().setValueAt(column4 + offset, row, 3);
					}
				}
				break;
			case Opcode.RSUB:
				break;
			case Opcode.TD:
				disAsmTable.getModel().setValueAt(column4 + operand, row, 3);
				break;
			case Opcode.RD:
				disAsmTable.getModel().setValueAt(column4 + operand, row, 3);
				break;
			case Opcode.WD:
				disAsmTable.getModel().setValueAt(column4 + operand, row, 3);
				break;
			case Opcode.TIX:
				disAsmTable.getModel().setValueAt(column4 + getArgFromAddressing(operand, flags), row, 3);
				break;
			case Opcode.SSK:
				Machine.notImplemented("SSK");
				break;
			default:
				return false;
		}
		
		return true;
	}
	
	private static int fetch() {
		int curByte = m.getByte(virtualPC);
	    virtualPC++;
	    return curByte;
	}
	
	private static int fetchWithoutPCIncrement(int pc) {
		return m.getByte(pc);
	}
	
}
