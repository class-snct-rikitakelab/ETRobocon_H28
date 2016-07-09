package linetrace;

import java.util.Timer;
import java.util.TimerTask;

import lejos.utility.Delay;

public class TestLineTrace {


	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		final TurnCalc tc = new TurnCalc();
		final WheelMotorCtrl wmc = new WheelMotorCtrl();
		final tailCtrl tail = new tailCtrl();
		final initialize initializer = new initialize();
		final ForwardCalculator fc = new ForwardCalculator();


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

		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask(){
			float forward = 0.0F;

			public void run(){
				tail.tailTwo();

				forward += fc.caldelForward();
				float turn = 0.0F;

				wmc.setForward(forward);
				wmc.setTurn(turn);

				wmc.controlWheel();
			}
		};

		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);


	}


}
