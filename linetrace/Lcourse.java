package linetrace;

import hardware.Hardware;

import java.util.Timer;
import java.util.TimerTask;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import ev3Viewer.LogSender;

public class Lcourse{

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		final TurnCalc tc = new TurnCalc();
		final WheelMotorCtrl wmc = new WheelMotorCtrl();
		final tailCtrl tail = new tailCtrl();
		final initialize initializer = new initialize();
		final ForwardCalculator fc = new ForwardCalculator();

		final LogSender ls = new LogSender();
		final AreaParamSelecter apk = new AreaParamSelecter();
		final GarageSolver garage = new GarageSolver(36, 90, 60);
		final establish esta = new establish();
		final toLUG3 goToLUG = new toLUG3();
        final LookUpGateEvader LUG = new LookUpGateEvader();
        

		initializer.init_test(ls);

		while(true){
			int count = 0;

			calibration();

			Sound.beep();

			//ls.connect();

			while(true){
				count ++;

				esta.esta();
				tail.tailThree();
				if(esta.checkRemoteCommand(71))break;
				Delay.msDelay(20);
				/*if(count == 500){
					break;
				}*/
			}

			Timer driveTimer = new Timer();
			TimerTask driveTask = new TimerTask(){
				float forward = 0.0F;
				int count = 0;
				long starttime = System.nanoTime();

				public void run(){
					tail.tailTwo();

					//forward += fc.caldelForward();
					if(count>20){
						apk.setParams();
					}

					forward = apk.sk.getTarget();
					float turn = -tc.calcTurn();

					wmc.setForward(forward);
					wmc.setTurn(turn);

					wmc.controlWheel();


					if(++count > 50){
						/*float time = (System.nanoTime()-starttime)/1000000;
						ls.addLog("P", tc.P, time);
						ls.addLog("I", tc.I, time);
						ls.addLog("D", tc.D, time);*/
						count = 0;
					}
				}
			};

			driveTimer.scheduleAtFixedRate(driveTask, 0, 4);

			int distc = 0;
			while(true){
				if(apk.dis>7.98F/*7.98F*/){
					if(++distc>10){
						distc = -99999999;
						driveTimer.cancel();
						goToLUG.gotoLUG(-40.0F);
						LCD.clear();
				        LCD.drawString("Down", 0, 4);	//走行体を倒す
				        LUG.LUG_down();
				        LCD.clear();
				        LCD.drawString("go", 0, 4);//走行体を前進させる
				        LUG.LUG_go();
				        LCD.clear();
				        LCD.drawString("up", 0, 4);//走行体を起こす
				        LUG.LUG_up();
				        LCD.drawString("garage", 1, 4);	//ガレージイン
				        garage.SolveGarage();


					}
				}

				if(Hardware.touchSensorIsPressed() == true){
					driveTimer.cancel();
					Hardware.motorPortL.controlMotor(0, 0);
					Hardware.motorPortR.controlMotor(0, 0);
					ls.send();
					break;
				}
				Delay.msDelay(100);
			}

			boolean flag = false;

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

			initializer.reset();
			ls.clear();
			//ls.disconnect();

		}


	}

	//黒と白と階段の輝度値を取得して記録しておく
	private static void calibration(){
		BrightTargetKeeper tk = new BrightTargetKeeper();
		BrightMeasure bright = new BrightMeasure();

		boolean flag = false;

		LCD.clear();
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
