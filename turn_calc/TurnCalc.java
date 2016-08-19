package turn_calc;

import area_param.AreaParamKeeper;
import area_param.AreaParamSelecter;

public class TurnCalc {

	private BrightMeasure bm;
	private BrightTargetKeeper btk;
	private AreaParamSelecter aps;

	float currentDiff;
	float prevDiff;
	float integral = 0.0F;

	public TurnCalc(){
		bm = new BrightMeasure();
		btk = new BrightTargetKeeper();
		aps = new AreaParamSelecter();

		currentDiff = bm.measureBrightness() - btk.getTarget();
		prevDiff = currentDiff;
	}

	public float calcTurn() {

		float bright = bm.measureBrightness();

		AreaParamKeeper buff = aps.getParams();

		currentDiff = bright - btk.getTarget();

		float turn = buff.getP()*currentDiff;

		turn += buff.getD()*(currentDiff - prevDiff);
		prevDiff = currentDiff;

		integral += buff.getI() * ((currentDiff + prevDiff) / 2.0F);

		//turn += D*(currentDiff - prevDiff) / DELTA;

		//integral += I * ((currentDiff + prevDiff) / 2.0F) *DELTA;

		//turn += integral;

		return turn;
	}

}
