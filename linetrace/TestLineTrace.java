package linetrace;

import java.util.Timer;
import java.util.TimerTask;

import hardware.Hardware;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class TestLineTrace {


	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		final TurnCalc tc = new TurnCalc();
		final WheelMotorCtrl wmc = new WheelMotorCtrl();
		final tailCtrl tail = new tailCtrl();
		final initialize initializer = new initialize();
		final ForwardCalculator fc = new ForwardCalculator();

		ParamKeeper.setP(-150.0F);
		ParamKeeper.setI(0.0F);
		ParamKeeper.setD(-30.0F);

		initializer.init();

		calibration();

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

				forward = 70.0F;
				float turn = tc.calcTurn();

				wmc.setForward(forward);
				wmc.setTurn(turn);

				wmc.controlWheel();
			}
		};

		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);
	}

	//黒と白と階段の輝度値を取得して記録しておく
	private static void calibration(){
		BrightTargetKeeper tk = new BrightTargetKeeper();
		BrightMeasure bright = new BrightMeasure();

		boolean flag = false;

		LCD.drawString("Detect BLACK", 0, 0);
		while(true){
			if(Hardware.touchSensorIsPressed() == true){
				flag = true;
			}else{
				if(flag == true){
					break;
				}
			}
			Delay.msDelay(100);
		}
		tk.setBlack(bright.measureBrightness());
		LCD.clear();
		flag = false;

		LCD.drawString("Detect WHITE", 0, 0);
		while(true){
			if(Hardware.touchSensorIsPressed() == true){
				flag =true;
			}else{
				if(flag == true){
					break;
				}
			}
			Delay.msDelay(100);
		}
		tk.setWhite(bright.measureBrightness());
		LCD.clear();
		flag = false;

		LCD.drawString("Detect GRAY", 0, 0);
		while(true){
			if(Hardware.touchSensorIsPressed() == true){
				flag = true;
			}else{
				if(flag == true){
					break;
				}
			}
		}
		tk.setGray(bright.measureBrightness());
		LCD.clear();
		flag = false;

		LCD.drawString("Detect KAIDAN", 0, 0);
		while(flag == false){
			if(Hardware.touchSensorIsPressed() == true){
				flag = true;
			}else{
				if(flag == true){
					break;
				}
			}
		}
		tk.setStep(bright.measureBrightness());
		LCD.clear();
	}


}
