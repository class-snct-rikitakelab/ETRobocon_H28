package linetrace;

import java.util.Timer;
import java.util.TimerTask;

import ev3Viewer.LogSender;
import hardware.Hardware;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class TestParam {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		final TurnCalc tc = new TurnCalc();
		final WheelMotorCtrl wmc = new WheelMotorCtrl();
		final tailCtrl tail = new tailCtrl();
		final initialize initializer = new initialize();
		final ForwardCalculator fc = new ForwardCalculator();

		final LogSender ls = new LogSender();

		initializer.init();

		int count = 0;

		Sound.beep();

		ls.connect();

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
			int count = 0;
			long starttime = System.nanoTime();

			public void run(){
				tail.tailTwo();

				//forward += fc.caldelForward();

				forward = 70.0F;
				float turn = tc.calcTurn();

				wmc.setForward(forward);
				wmc.setTurn(turn);

				wmc.controlWheel();

				/*
				if(++count > 20){
					float time = (System.nanoTime()-starttime)/1000000;
					ls.addLog("hoge", 0, time);
				}*/
			}
		};

		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);

		while(true){

			if(Hardware.touchSensorIsPressed() == true){
				Hardware.motorPortL.controlMotor(0, 0);
				Hardware.motorPortR.controlMotor(0, 0);
				driveTimer.cancel();
				ls.send();
				break;
			}
			Delay.msDelay(500);
		}


	}

}
