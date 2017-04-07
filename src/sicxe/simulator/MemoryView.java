package sicxe.simulator;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MemoryView {
	
	private static JScrollPane memScroll;
	private static JTextArea mem;

	public static void buildMemoryView(JPanel memoryPanel) {
		mem = new JTextArea();
		mem.setEditable(false);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		memScroll = new JScrollPane(mem, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		memScroll.setPreferredSize(new Dimension(430, 330));
		memoryPanel.add(memScroll, gbc);
	}
	
	public static String initMemoryView(Machine m) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < Utils.getProgramLen(); i++){
			if(i % 16 == 0) {
				sb.append("\n");
				sb.append(String.format("%5S", Integer.toHexString(i)).replace(' ', '0'));
				sb.append("  ");
			}
			sb.append(String.format("%2S", Integer.toHexString(m.getByte(i))).replace(' ', '0'));
			sb.append(" ");
		}
		
		return sb.toString();
	}
	
	public static String initRestOfMemory() {
		StringBuilder sb = new StringBuilder();
		for(int i = Utils.getProgramLen(); i < 0xFFFFF; i++){
			if(i % 16 == 0) {
				sb.append("\n");
				sb.append(String.format("%5S", Integer.toHexString(i)).replace(' ', '0'));
				sb.append("  ");
			}
			sb.append("00");
			sb.append(" ");
		}
		
		return sb.toString();
	}
	
	public static void updateMemory(Machine m) {
		String newMemView = initMemoryView(m);
		mem.replaceRange(newMemView, 0, newMemView.length());
	}
	
	public static void setMemory(String memoryStr) {
		mem.setText(memoryStr);
	}
	
}
