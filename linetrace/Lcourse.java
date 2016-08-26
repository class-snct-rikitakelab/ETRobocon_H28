package linetrace;

import java.util.Timer;
import java.util.TimerTask;

import drive_control.BrightMeasure;
import drive_control.BrightTargetKeeper;
import hardware.Hardware;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import starter.Starter;

public class Lcourse {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		final LineTracer lt = new LineTracer();
		Starter start = new Starter();

		calibration();

		start.start();

		Sound.beep();

		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask(){

			public void run(){

				lt.linetrace();

			}
		};

		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);

		while(true){

			if(Hardware.touchSensorIsPressed() == true){
				break;
			}

			Delay.msDelay(20);
		}



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
