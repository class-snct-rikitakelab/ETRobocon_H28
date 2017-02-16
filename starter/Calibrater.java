package starter;


import hardware.BrightSensor;
import hardware.TouchSensor;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Calibrater {

	BrightSensor bright;
	TouchSensor touch;

	private float[] target = new float[3];

	public void calibration(BrightSensor bright, TouchSensor touch) {
		this.bright = bright;
		this.touch = touch;
	}

	public void ScanCalibration(){
		boolean flag = false;

		LCD.clear();

		LCD.drawString("Detect BLACK", 0, 0);
		while(true){
			if(touch.touchSensorIsPressed() == true){
				flag = true;
			}else{
				if(flag == true){
					break;
				}
			}
			Delay.msDelay(100);
		}
		target[0] = bright.getBrightValue();
		LCD.clear();
		flag = false;


		LCD.drawString("Detect WHITE", 0, 0);
		while(true){
			if(touch.touchSensorIsPressed() == true){
				flag = true;
			}else{
				if(flag == true){
					break;
				}
			}
			Delay.msDelay(100);
		}
		target[1] = bright.getBrightValue();
		LCD.clear();
		flag = false;

		LCD.drawString("Detect GRAY", 0, 0);
		while(true){
			if(touch.touchSensorIsPressed() == true){
				flag = true;
			}else{
				if(flag == true){
					break;
				}
			}
			Delay.msDelay(100);
		}
		target[2] = bright.getBrightValue();
		LCD.clear();
		flag = false;
	}


	public float[] getTargets() {
		return this.target;

	}

}
