package drive_control;

import area_param.AreaParamKeeper;
import area_param.AreaParamSelecter;

public class TurnCalc {

	private BrightMeasure bm;
	private BrightTargetKeeper btk;
	private AreaParamSelecter aps;

	float currentDiff;
	float prevDiff;
	float integral = 0.0F;

	private float Kp,Ki,Kd;

	public TurnCalc(){
		bm = new BrightMeasure();
		btk = new BrightTargetKeeper();
		aps = new AreaParamSelecter();

		currentDiff = bm.measureBrightness() - btk.getTarget();
		prevDiff = currentDiff;
		Kp = 0.0F;
		Ki = 0.0F;
		Kd = 0.0F;
	}

	public float calcTurn() {

		float bright = bm.measureBrightness();

		currentDiff = bright - btk.getTarget();

		float turn = Kp*currentDiff;

		turn += Kd*(currentDiff - prevDiff);
		prevDiff = currentDiff;

		integral += Ki * ((currentDiff + prevDiff) / 2.0F);

		//turn += D*(currentDiff - prevDiff) / DELTA;
		//integral += I * ((currentDiff + prevDiff) / 2.0F) *DELTA;
		//turn += integral;

		return turn;
	}

	public void updateParams(float distance){
		AreaParamKeeper buff = aps.getParams(distance);

		Kp = buff.getP();
		Ki = buff.getI();
		Kd = buff.getD();
	}

}
