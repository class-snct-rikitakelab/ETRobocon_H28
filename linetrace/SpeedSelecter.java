package linetrace;

public class SpeedSelecter {


	public float getSpeedtarget(){

		SpeedKeeper sk = new SpeedKeeper();
		DistanceMeasure dm = new DistanceMeasure();

		float thresholds[] = {2.3F,2.9F,4.75F,5.69F,6.922F,7.953F,8.0F,8.5F};

		float target = 0.0F;

		float dis = dm.measureDistance_Meter();
		for(int i=0; i < thresholds.length ; i++){

			if(dis > thresholds[6] && dis < thresholds[7]){
				ParamKeeper.setD(0.0F);
				ParamKeeper.setP(-10.0F);
			}

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
