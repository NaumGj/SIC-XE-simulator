package sicxe.simulator;

import java.util.Timer;
import java.util.TimerTask;

public class ProcessorClk {

	private Timer timer;
	private int speed = 1;
	private boolean isRunning = false;
	private Machine m;
	
	public void setMachine(Machine m) {
		this.m = m;
	}
	
	public void start() {
		if(!isRunning){
			timer = new Timer();
			TimerTask executeCommand = new TimerTask() {
				
				@Override
				public void run() {
//					for(int i = 0; i < 20; i++) {
						int previousPC = m.getPC();
						m.execute();
						int currentPC = m.getPC();
						if(previousPC == currentPC) {
							stop();
							this.cancel();
						}
//					}
					MainView.updateGUI(m);
				}
			};
			timer.scheduleAtFixedRate(executeCommand, 0, speed);
			isRunning = true;
		}
	}
	
	public void stop() {
		if(isRunning) {
			timer.cancel();
			timer = null;
			isRunning = false;
		}
	}
	
	public void step() {
		if(!isRunning) {
			m.execute();
			MainView.updateGUI(m);
		}
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void setSpeed(int khz) {
		speed = 1 / khz;
	}
	
}
