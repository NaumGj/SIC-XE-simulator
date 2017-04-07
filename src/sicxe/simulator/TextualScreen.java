package sicxe.simulator;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class TextualScreen {
	
	private static JTextArea textScr;
	private static JFrame textScrFrame;
	
	public static int TEXT_SCREEN_START = 0xB800;
	public static int TEXT_SCREEN_END = 0xBFD0;
	
	public static void buildTextualScreen(Machine m) {
		textScr = new JTextArea();
		textScr.setEditable(false);
		textScr.setFont(new java.awt.Font("Courier New", java.awt.Font.BOLD, 12));

		textScrFrame = new JFrame();
		textScrFrame.add(textScr);
		textScrFrame.setSize(new Dimension(642, 348));
		textScrFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		initTextualScreen(m);
		textScrFrame.setVisible(true);
	}


	public static void initTextualScreen(Machine m) {
		if (textScrFrame != null && textScr != null) {
			StringBuilder sb = new StringBuilder();
			for(int i = TEXT_SCREEN_START; i < TEXT_SCREEN_END; i++){
				if((i - TEXT_SCREEN_START) % 80 == 0 && i != TEXT_SCREEN_START) {
					sb.append("\n");
				}
				if(m.getByte(i) == 0){
					sb.append(" ");
				} else {
					sb.append((char)m.getByte(i));
				}
			}
			textScr.setText(sb.toString());
		}
	}
	
	public static void updateTextualScreen(int addr, Machine m) {
		if (textScrFrame != null && textScr != null) {
			int startAddr = (addr - TEXT_SCREEN_START) + (addr - TEXT_SCREEN_START) / 80;
			textScr.replaceRange(Character.toString((char)m.getByte(addr)), startAddr, startAddr + 1);
		}
	}
	
}
