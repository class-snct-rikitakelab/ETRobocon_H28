package linetrace;

public class TurnCalc {

	private BrightMeasure bm;
	private BrightTargetKeeper btk;
	private ParamKeeper pk;

	float currentDiff;
	float prevDiff;
	float integral = 0.0F;

	TurnCalc(){
		bm = new BrightMeasure();
		btk = new BrightTargetKeeper();
		pk = new ParamKeeper();

		currentDiff = bm.measureBrightness() - btk.getTarget();
		prevDiff = currentDiff;
	}

	public float calcTurn() {

		float bright = bm.measureBrightness();

		currentDiff = bright - btk.getTarget();

		float turn = pk.getP()*currentDiff;

		turn += pk.getD()*(currentDiff - prevDiff);
		prevDiff = currentDiff;

		integral += pk.getI() * ((currentDiff + prevDiff) / 2.0F);

		//turn += D*(currentDiff - prevDiff) / DELTA;

		//integral += I * ((currentDiff + prevDiff) / 2.0F) *DELTA;

		//turn += integral;

		return turn;
	}

}
