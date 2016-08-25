package area_param;

public class AreaParamSelecter {

	private DistanceMeasure dm = new DistanceMeasure();

	public int i = 0;
	public float dis = 0.0F;

	public float P = 0.0F;
	public float I = 0.0F;
	public float D = 0.0F;

	// 左から、区間の始まりの距離・区間の終わりの距離・P係数・I係数・D係数・目標速度
	private AreaParamKeeper areaparams[] ={
		new AreaParamKeeper(-10.0F,	0.1F,	-80.0F,	0.0F,	30.0F,	20.0F),
		new AreaParamKeeper(0.1F,	2.3F,	-80.0F,	0.0F,	30.0F,	100.0F),
		new AreaParamKeeper(2.3F,	2.9F,	-120.0F,	0.0F,	30.0F,	40.0F),
		new AreaParamKeeper(2.9F,	4.7F,	-80.0F,	0.0F,	30.0F,	100.0F),
		new AreaParamKeeper(4.7F,	5.7F,	-180.0F,	0.0F,	30.0F,	40.0F),
		new AreaParamKeeper(5.7F,	6.92F,	-80.0F,	0.0F,	30.0F,	100.0F),
		new AreaParamKeeper(6.92F,	7.95F,	-180.0F,	0.0F,	30.0F,	40.0F),
		new AreaParamKeeper(7.95F,	8.0F,	-80.0F,	0.0F,	30.0F,	50.0F),
		new AreaParamKeeper(8.0F,	80.0F,	0.0F,	0.0F,	30.0F,	0.0F)
	};
/*
	public void setParams(){

		dis = dm.measureDistance_Meter();
		//dis = 1.0F;

		for(i =0; i<areaparams.length; i++){
			if(areaparams[i].checkPos(dis) == true){

				ParamKeeper.setP(areaparams[i].getP());
				ParamKeeper.setI(areaparams[i].getI());
				ParamKeeper.setD(areaparams[i].getD());
				SpeedKeeper.setTarget(areaparams[i].getForward());
				break;
			}
		}

	}
*/
	public AreaParamKeeper getParams(){

		dis = dm.measureDistance_Meter();

		for(i=0; i<areaparams.length; i++){
			if(areaparams[i].checkPos(dis) == true){
				return areaparams[i];
			}
		}

		return null;

	}

	public float getSpeed(){

		dis = dm.measureDistance_Meter();

		for(i=0; i<areaparams.length; i++){
			if(areaparams[i].checkPos(dis) == true){
				return areaparams[i].getForward();
			}
		}

		return 0.0F;
	}

	private float measureDistance_Degree(){

		float rotate = hardware.Hardware.motorPortL.getTachoCount() + hardware.Hardware.motorPortR.getTachoCount();

		rotate = rotate / 2.0F;

		return rotate;
	}

	private float measureDistance_Meter(){

		float rotate = hardware.Hardware.motorPortL.getTachoCount() + hardware.Hardware.motorPortR.getTachoCount();

		rotate = rotate / 2.0F;

		return (rotate / 360.0F) * 0.262F;

	}

	private float measureDistance_CMeter(){

		float rotate = hardware.Hardware.motorPortL.getTachoCount() + hardware.Hardware.motorPortR.getTachoCount();

		rotate = rotate / 2.0F;

		return (rotate / 360.0F) * 26.2F;

	}

}
