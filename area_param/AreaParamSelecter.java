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

			/*

		new AreaParamKeeper(-10.0F,	0.5F,	0.0F,	0.0F,	0.0F,	100.0F),
		new AreaParamKeeper(0.5F,	100.0F,	0.0F,	0.0F,	0.0F,	30.0F)
		*/

			/*
		//学校のLコース用
		new AreaParamKeeper(-10.0F,	2.2F,	40.0F,	0.0F,	300.0F,	100.0F),
		new AreaParamKeeper(2.2F,	2.3F,	80.0F,	0.0F,	300.0F,	80.0F),
		new AreaParamKeeper(2.3F,	3.3F,	150.0F,	0.0F,	150.0F,	40.0F),
		new AreaParamKeeper(2.9F,	4.6F,	40.0F,	0.0F,	200.0F,	100.0F),
		new AreaParamKeeper(4.6F,	5.3F,	120.0F,	0.0F,	200.0F,	40.0F),
		new AreaParamKeeper(5.3F,	6.2F,	60.0F,	0.0F,	300.0F,	100.0F),
		new AreaParamKeeper(6.2F,	7.95F,	180.0F,	0.0F,	300.0F,	40.0F),
		new AreaParamKeeper(7.95F,	9.0F,	20.0F,	0.0F,	150.0F,	30.0F),
		new AreaParamKeeper(9.0F,	80.0F,	0.0F,	0.0F,	300.0F,	100.0F)
		*/

			new AreaParamKeeper(-10.0F,	2.3F,	40.0F,	0.0F,	300.0F,	100.0F),
			new AreaParamKeeper(2.3F,	3.3F,	230.0F,	0.0F,	300.0F,	40.0F),
			new AreaParamKeeper(2.9F,	4.6F,	40.0F,	0.0F,	300.0F,	100.0F),
			new AreaParamKeeper(4.6F,	5.3F,	180.0F,	0.0F,	300.0F,	40.0F),
			new AreaParamKeeper(5.3F,	6.2F,	60.0F,	0.0F,	300.0F,	100.0F),
			new AreaParamKeeper(6.2F,	7.95F,	180.0F,	0.0F,	300.0F,	40.0F),
			new AreaParamKeeper(7.95F,	9.0F,	40.0F,	0.0F,	300.0F,	30.0F),
			new AreaParamKeeper(9.0F,	80.0F,	0.0F,	0.0F,	300.0F,	100.0F)

				/*
		//8/20の試走会のパラメータLコース用
		new AreaParamKeeper(-10.0F,	2.2F,	60.0F,	0.0F,	300.0F,	100.0F),
		new AreaParamKeeper(2.2F,	3.2F,	150.0F,	0.0F,	300.0F,	50.0F),
		new AreaParamKeeper(3.2F,	4.4F,	80.0F,	0.0F,	150.0F,	100.0F),
		new AreaParamKeeper(4.4F,	5.2F,	130.0F,	0.0F,	150.0F,	60.0F),
		new AreaParamKeeper(5.2F,	6.0F,	90.0F,	0.0F,	150.0F,	80.0F),
		new AreaParamKeeper(6.0F,	7.5F,	120.0F,	0.0F,	150.0F,	60.0F),
		new AreaParamKeeper(7.5F,	8.0F,	60.0F,	0.0F,	150.0F,	50.0F),
		new AreaParamKeeper(8.0F,	80.0F,	40.0F,	0.0F,	150.0F,	30.0F)
*/

		/*
		//8/20の試走会のパラメータ Rコース用
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

	public AreaParamKeeper getParams(float dis){

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

	public float getSpeed(float dis){

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
