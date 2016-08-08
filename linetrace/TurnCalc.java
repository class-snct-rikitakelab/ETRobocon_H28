package linetrace;

public class TurnCalc {

	private BrightMeasure bm;
	private BrightTargetKeeper btk;

	private ParamKeeper pk;

	public float bright = 0.0F;
	public float P;
	public float I;
	public float D;

	float currentDiff;
	float prevDiff;
	float integral = 0.0F;
	float DELTA_T = 0.004f;

	TurnCalc(){
		bm = new BrightMeasure();
		btk = new BrightTargetKeeper();
		pk = new ParamKeeper();

		currentDiff = bm.measureBrightness() - btk.getTarget();
		prevDiff = currentDiff;
	}

	public float calcTurn() {
		bright = bm.measureBrightness();

		currentDiff = bright - btk.getTarget();
		
		P = pk.getP();
		float turn = P*currentDiff;

		D = pk.getD();
		turn += D*(currentDiff - prevDiff)/DELTA_T;
		
		I = pk.getI();
		integral +=I * ((currentDiff + prevDiff) / 2.0F)*DELTA_T;

		//turn += D*(currentDiff - prevDiff) / DELTA;

		//integral += I * ((currentDiff + prevDiff) / 2.0F) *DELTA;

		turn += integral;
		prevDiff = currentDiff;
		return turn;
	}

}
