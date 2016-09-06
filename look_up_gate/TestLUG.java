package look_up_gate;

import hardware.Hardware;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.BasicMotorPort;
import lejos.utility.Delay;
import motor_control.tailCtrl;

public class TestLUG {

	static float black = 0.0F;
	static float white = 0.0F;

	public static void main(String[] args) {

		Hardware.gyro.reset();
	    Hardware.sonar.enable();
	    Hardware.motorPortL.setPWMMode(BasicMotorPort.PWM_BRAKE);
	    Hardware.motorPortR.setPWMMode(BasicMotorPort.PWM_BRAKE);
	    Hardware.motorPortT.setPWMMode(BasicMotorPort.PWM_BRAKE);

		Hardware.motorPortR.resetTachoCount();
		Hardware.motorPortL.resetTachoCount();
		Hardware.motorPortR.controlMotor(0,1);
		Hardware.motorPortL.controlMotor(0,1);
		Hardware.motorPortT.resetTachoCount();
		Hardware.motorPortT.controlMotor(0, 0);

		calibration();

		LookUpGateEvader LUG = new LookUpGateEvader();
		GarageSolver garage = new GarageSolver(66,30,black,white,0.3F);

		Sound.beep();
		Delay.msDelay(5000);

		LCD.clear();
		//LCD.drawString("gotoLUG", 0, 4);
		//goToLUG.gotoLUG(-40.0F);
		Hardware.motorPortR.controlMotor(0,1);
		Hardware.motorPortL.controlMotor(0,1);
		LCD.clear();
		LUG.setBlack(black);
		LUG.setWhite(white);
		Sound.beep();
		LCD.drawString("LUG_down", 0, 4);
		LUG.LUG_down();
		LCD.clear();
		Sound.beep();
		LCD.drawString("LUG_go", 0, 4);
		LUG.LUG_go();
		Hardware.motorPortR.controlMotor(0,1);
		Hardware.motorPortL.controlMotor(0,1);
		LCD.clear();
		Sound.beep();
		LCD.drawString("LUG_up", 0, 4);
		LUG.LUG_up();
		Hardware.motorPortR.resetTachoCount();
		Hardware.motorPortL.resetTachoCount();
		LCD.clear();
		Sound.beep();
		LCD.drawString("SolveGarage", 0, 4);
		garage.SolveGarage();

	}

	static void calibration(){

		tailCtrl tail =  new tailCtrl();

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

			tail.tailRotate(70);
			Delay.msDelay(100);
		}
		black = Hardware.getBrightness();
		LCD.clear();

		Sound.beep();

		flag = false;

		LCD.drawString("Detect WHITE", 0, 0);
		while(true){
			if(Hardware.touchSensorIsPressed() == true){
				flag = true;
			}else{
				if(flag == true){
					break;
				}
			}
			tail.tailRotate(70);
			Delay.msDelay(100);
		}
		white = Hardware.getBrightness();
		LCD.clear();

		Sound.beep();
	}

}
