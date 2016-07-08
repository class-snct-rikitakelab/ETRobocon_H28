package linetrace;

import java.util.Timer;
import java.util.TimerTask;

import lejos.utility.Delay;

public class TestLineTrace {

	//private static LogSender sender = new LogSender();
	private static long time4ms;
	private static long starttime;

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		final TurnCalc tc = new TurnCalc();
		final WheelMotorCtrl wmc = new WheelMotorCtrl();
		final tailCtrl tail = new tailCtrl();

		final initialize initializer = new initialize();

		initializer.init();

		int count = 0;

		while(true){
			count ++;

			tail.tailThree();

			Delay.msDelay(20);

			if(count == 500){
				break;
			}
		}

		starttime = System.nanoTime();
		int waitcount = 0;
/*
		while(true){
			tailControl(0);

			float forward = 40.0F;
			float turn = tc.calcTurn();

			wmc.setForward(forward);
			wmc.setTurn(turn);

			wmc.controlWheel();

			//wait4ms();

			Delay.msDelay(4);
		}
*/

		/*
		 Timer driveTimer = new Timer();
	        TimerTask driveTask = new TimerTask() {
	        	@Override
	                public void run() {
	        		tailControl(0);

	        		float forward = 40.0F;
	        		float turn = tc.calcTurn();
	        		wmc.setForward(forward);
	        		wmc.setTurn(turn);

	        		wmc.controlWheel();

	        		if(++waitcount > 5){

	        		}
	        	}
	        };
	        driveTimer.scheduleAtFixedRate(driveTask, 0, 4);
	        */

		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask(){
			public void run(){
				tail.tailTwo();

				float forward = 40.0F;
				float turn = tc.calcTurn();

				wmc.setForward(forward);
				wmc.setTurn(turn);

				wmc.controlWheel();
			}
		};
/*
		Timer driveTimer = new Timer();
		TimerTask driveTask = new DriveTask(wmc, tc);
*/
		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);


	}

	/*
	private static void wait4ms(){
		long time = System.nanoTime();
		if((time - time4ms) > 4000000 ){
			time4ms = time;
			return;
		}
		while(true){
			time = System.nanoTime();
			if((time - time4ms) > 4000000 ){
				time4ms += 4000000;
				break;
			}
		}
	}*/

}
