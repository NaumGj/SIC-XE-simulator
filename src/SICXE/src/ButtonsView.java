package SICXE.src;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonsView {
	
	private static JButton start;
	private static JButton stop;
	private static JButton step;
	private static JButton textScrBtn;
	
	
	public static void buildButtonsView(JPanel buttonPanel, final Machine m) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!m.getProcessorClk().isRunning()) {
					m.getProcessorClk().start();
				}
			}
		});
		buttonPanel.add(start, gbc);
		
		gbc.gridx = 1;
		stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(m.getProcessorClk().isRunning()){
					m.getProcessorClk().stop();
				}
			}
		});
		buttonPanel.add(stop, gbc);
		
		gbc.gridx = 2;
		step = new JButton("Step");
		step.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!m.getProcessorClk().isRunning()) {
					m.getProcessorClk().step();
				}
			}
		});
		buttonPanel.add(step, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		textScrBtn = new JButton("Textual screen");
		textScrBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				TextualScreen.buildTextualScreen(m);
			}
		});
		buttonPanel.add(textScrBtn, gbc);
	}
	
	
}
