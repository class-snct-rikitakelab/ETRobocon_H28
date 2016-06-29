package steps;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;


public class StepMain {
	static StepSolver step = new StepSolver();
	
	public static void main(String[] args){
		step.init();
		LCD.drawString("ready", 0, 0);
		Sound.beep();
		//sender.setLogSize(2000);
		//sender.connect();
		step.ready();

		step.starttime = System.nanoTime();
		step.checkDistanceInit();

		step.parallelToStep();
		step.climbStep();
		step.parallelToStep2();
		step.climbStep2();
		step.goCenter();
		step.down();

		step.speed = 0;
		step.turn = 0;
		step.runByTail();

		//sender.send();
	}


}
