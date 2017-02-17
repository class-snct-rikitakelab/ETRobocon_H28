package hardware;

import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

public class GyroSensor {

	SensorMode gyro;
	SampleProvider sampleprovider;
	float[] sampleGyro;

	public GyroSensor(SensorMode gyro){
		this.gyro = gyro;
		sampleGyro = new float[gyro.sampleSize()];
	}

	public GyroSensor(SampleProvider gyro){
		sampleprovider = gyro;
		sampleGyro = new float[gyro.sampleSize()];
	}

	public float getGyroValue(){
		if(gyro!=null){
			gyro.fetchSample(sampleGyro, 0);
		}else{
			sampleprovider.fetchSample(sampleGyro, 0);
		}
		return sampleGyro[0];
	}
}
