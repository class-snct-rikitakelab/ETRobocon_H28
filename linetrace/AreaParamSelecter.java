package linetrace;

public class AreaParamSelecter {

	private DistanceMeasure dm = new DistanceMeasure();
	private ParamKeeper pk = new ParamKeeper();
	//private SpeedKeeper sk = new SpeedKeeper();
	SpeedKeeper sk = new SpeedKeeper();

	public int i = 0;
	public float dis = 0.0F;

	public float P = 0.0F;
	public float I = 0.0F;
	public float D = 0.0F;

	// 左から、区間の始まりの距離・区間の終わりの距離・P係数・I係数・D係数・目標速度
	private AreaParamKeeper areaparams[] ={
		/*
		 * Rコース
		 */
		/*
		new AreaParamKeeper(-10.0F,	0.1F,	60.0F,	30.0F,	15.0F,	40.0F),
		new AreaParamKeeper(0.1F,	2.3F,	30.0F,	30.0F,	15.0F,	100.0F),
		new AreaParamKeeper(2.3F,	2.9F,	130.0F,	30.0F,	15.0F,	60.0F),
		new AreaParamKeeper(2.9F,	4.7F,	30.0F,	30.0F,	15.0F,	100.0F),
		new AreaParamKeeper(4.7F,	5.8F,	150.0F,	30.0F,	50.0F,	55.0F),
		new AreaParamKeeper(5.8F,	6.922F,	30.0F,	30.0F,	15.0F,	100.0F),
		new AreaParamKeeper(6.92F,	7.953F,	160.0F,	30.0F,	15.0F,	60.0F),
		new AreaParamKeeper(7.95F,	8.0F,	30.0F,	30.0F,	15.0F,	50.0F),
		new AreaParamKeeper(8.0F,	80.0F,	0.0F,	30.0F,	15.0F,	0.0F)
		*/
		/*
		 * Lコース
		 */
		new AreaParamKeeper(-10.0F,	0.1F,	60.0F,	30.0F,	15.0F,	40.0F),
		new AreaParamKeeper(0.1F,	2.3F,	30.0F,	30.0F,	15.0F,	100.0F),
		new AreaParamKeeper(2.3F,	3.2F,	150.0F,	30.0F,	30.0F,	50.0F),
		new AreaParamKeeper(3.2F,	4.4F,	80.0F,	30.0F,	15.0F,	100.0F),
		new AreaParamKeeper(4.4F,	5.1F,	130.0F,	30.0F,	15.0F,	60.0F),
		new AreaParamKeeper(5.1F,	6.0F,	50.0F,	30.0F,	15.0F,	100.0F),
		new AreaParamKeeper(6.0F,	7.5F,	120.0F,	30.0F,	15.0F,	60.0F),
		new AreaParamKeeper(7.5F,	8.0F,	60.0F,	30.0F,	15.0F,	50.0F),
		new AreaParamKeeper(8.0F,	80.0F,	0.0F,	30.0F,	15.0F,	0.0F)
	};

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

}
