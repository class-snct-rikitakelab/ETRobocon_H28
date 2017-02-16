package hardware;

import lejos.hardware.sensor.SensorMode;

public class UltrasonicSensor {

	SensorMode sonar;
	float[] sampleSonar;

	public UltrasonicSensor(SensorMode sonar){
		this.sonar = sonar;
		sampleSonar = new float[sonar.sampleSize()];
	}

	public float getDistance(){
		sonar.fetchSample(sampleSonar, 0);
		return sampleSonar[0];
	}

}
