package hardware;

import lejos.hardware.sensor.SensorMode;

public class BrightSensor {

	SensorMode bright;
	float[] sampleBright;

	public BrightSensor(SensorMode bright){
		this.bright = bright;
		sampleBright = new float[this.bright.sampleSize()];
	}

	public float getBright(){
		this.bright.fetchSample(sampleBright, 0);
		return sampleBright[0];
	}

}
