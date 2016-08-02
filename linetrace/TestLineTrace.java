package linetrace;

import java.util.Timer;
import java.util.TimerTask;

import hardware.Hardware;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class TestLineTrace {

	public static int START_COMMAND = 71;


	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		final TurnCalc tc = new TurnCalc();
		final WheelMotorCtrl wmc = new WheelMotorCtrl();
		final tailCtrl tail = new tailCtrl();
		final initialize initializer = new initialize();
		final ForwardCalculator fc = new ForwardCalculator();
		final SpeedSelecter speedselect = new SpeedSelecter();
		final establish esta = new establish();
		//DistanceMeasure dm = new DistanceMeasure();

		ParamKeeper.setP(-100.0F);
		ParamKeeper.setI(0.0F);
		ParamKeeper.setD(0.0F);

		initializer.init();

		calibration();

		Timer CommandTimer = new Timer();
		TimerTask CommandTask = new TimerTask(){

			public void run(){
				esta.esta();
			}
		};

		boolean flag = false;
		CommandTimer.schedule(CommandTask, 0, 20);

		while(true){

			if(Hardware.touchSensorIsPressed() == true){
				flag = true;
			}

			if(esta.checkRemoteCommand(START_COMMAND) == true || flag == true){
				break;
			}

			tail.tailThree();

			Delay.msDelay(20);
		}

		CommandTimer.cancel();

		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask(){
			float forward = 0.0F;
			int count = 0;

			public void run(){
				tail.tailTwo();

				forward = speedselect.getSpeedtarget();
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
