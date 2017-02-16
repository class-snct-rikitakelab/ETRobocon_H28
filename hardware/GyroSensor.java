package hardware;

import lejos.hardware.sensor.SensorMode;

public class GyroSensor {

	SensorMode gyro;
	float[] sampleGyro;

	public GyroSensor(SensorMode gyro){
		this.gyro = gyro;
		sampleGyro = new float[gyro.sampleSize()];
	}

	public float getGyroValue(){
		gyro.fetchSample(sampleGyro, 0);
		return sampleGyro[0];
	}
}
