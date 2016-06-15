import lejos.hardware.lcd.LCD;

public class BrightnessMeasure {

	private EV3Body body;

	public BrightnessMeasure(){
		body = new EV3Body();
	}

	public float getBrightness() {
		while(!body.touchSensorIsPressed()){
			LCD.clear();
			LCD.drawString("Push Button", 0, 0);
		}
		return body.getBrightness();
	}
}