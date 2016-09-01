package linetrace;

import area_param.DistanceMeasure;
import drive_control.BrightMeasure;
import drive_control.BrightTargetKeeper;
import hardware.Hardware;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import look_up_gate.LookUpGateEvader;
import look_up_gate.toLUG3;
import starter.Starter;

public class TestLineTrace {

	public static int START_COMMAND = 71;


	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		final LineTracer lt = new LineTracer();
		Starter start = new Starter();
		final DistanceMeasure dm = new DistanceMeasure();
		float distance = 0.0F;
		toLUG3 goToLUG = new toLUG3();
		LookUpGateEvader LUG = new LookUpGateEvader();

		calibration();

		Sound.beep();

		start.start();

		//Sound.beep();

		goToLUG.gotoLUG(40.0F);
		LUG.LUG_down();
		LUG.LUG_go();
		LUG.LUG_up();
		//GarageSolver.SolveGarage();

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
