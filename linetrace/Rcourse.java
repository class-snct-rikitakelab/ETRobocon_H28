package linetrace;

import hardware.Hardware;

import java.util.Timer;
import java.util.TimerTask;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import starter.Starter;
import steps.StepSolver;
import area_param.DistanceMeasure;
import drive_control.BrightMeasure;
import drive_control.BrightTargetKeeper;

public class Rcourse {

	public static int START_COMMAND = 71;


	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		final LineTracer lt = new LineTracer();
		Starter start = new Starter();
		final DistanceMeasure dm = new DistanceMeasure();
		StepSolver step = new StepSolver();
		float distance = 0.0F;

		calibration();
		BrightTargetKeeper tk = new BrightTargetKeeper();
		step.blackColor = tk.getBlack();
		step.whiteColor = tk.getWhite();
		step.grayColor  = tk.getGray();

		start.start();

		//Sound.beep();


		//↓のタイマは輝度値制御でライントレースする。周期は4ms
		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask(){

			public void run(){
				lt.linetrace();
			}
		};

		//下のタイマは走行距離測定して，PID係数と速度を切り替えている。周期は100ms
		Timer distanceTimer = new Timer();
		DistanceTask distancetask = new DistanceTask(lt,dm,distance);


		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);
		distanceTimer.scheduleAtFixedRate(distancetask, 0, 100);

		//一定距離走るとループから抜ける
		while(true){
			distance = distancetask.getDistance();

			if(distance > 8.0F){
				driveTimer.cancel();
				distanceTimer.cancel();
				LCD.drawString("kaidan", 1, 1);
				step.solveStep();
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
