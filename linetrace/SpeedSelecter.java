package linetrace;

public class SpeedSelecter {

	public float getSpeedtarget(){

		SpeedKeeper sk = new SpeedKeeper();
		DistanceMeasure dm = new DistanceMeasure();

		float thresholds[] = {2.4F,2.9F,4.75F,5.99F,6.922F,7.953F,9.0F};

		float target = 0.0F;

		float dis = dm.measureDistance_Meter();
		for(int i=0; i < thresholds.length ; i++){
			if(dis < thresholds[i]){
				target = sk.getForwardTarget(i);
				break;
			}else{
				target = 0.0F;
			}
		}

		return target;
	}

}
