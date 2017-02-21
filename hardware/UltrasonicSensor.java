package hardware;

import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

public class UltrasonicSensor {

	SensorMode sonar;
	SampleProvider sampleprovider;
	float[] sampleSonar;

	public UltrasonicSensor(SensorMode sonar){
		this.sonar = sonar;
		sampleSonar = new float[sonar.sampleSize()];
	}

	public UltrasonicSensor(SampleProvider sonar){
		this.sampleprovider = sonar;;
		sampleSonar = new float[sonar.sampleSize()];
	}

	public float getDistance(){
		if(sonar != null){
			sonar.fetchSample(sampleSonar, 0);
		}else{
			sampleprovider.fetchSample(sampleSonar, 0);
		}
		return sampleSonar[0];
	}

}
