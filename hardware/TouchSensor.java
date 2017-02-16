package hardware;

import lejos.hardware.sensor.SensorMode;

public class TouchSensor {

	SensorMode touch;
	float[] sampleTouch;

	public TouchSensor(SensorMode touch){
		this.touch = touch;
		sampleTouch = new float[touch.sampleSize()];
	}

	public boolean touchSensorIsPressed(){
		touch.fetchSample(sampleTouch, 0);
		return ((int)sampleTouch[0] != 0);
	}
}
