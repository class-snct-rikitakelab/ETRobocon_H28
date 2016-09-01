package linetrace;

import java.util.TimerTask;

import area_param.DistanceMeasure;

public class DistanceTask extends TimerTask{

	private LineTracer linetracer;
	private DistanceMeasure dm;
	private float distance;

	public DistanceTask(LineTracer linetracer,DistanceMeasure dm, float distance){
		this.linetracer = linetracer;
		this.dm = dm;
		this.distance = distance;
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ

		distance = dm.measureDistance_Meter();

		linetracer.renewParams(distance);
	}

	public float getDistance(){
		return distance;
	}

}
