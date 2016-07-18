package linetrace;

public class TurnCalc {

	private BrightMeasure bm;
	private BrightTargetKeeper btk;

	static float P = -250.0F;
	static float I = 0.0F;
	static float D = 0.0F;
	static float DELTA = 0.004F;

	float currentDiff;
	float prevDiff;
	float integral = 0.0F;

	TurnCalc(){
		bm = new BrightMeasure();
		btk = new BrightTargetKeeper();

		currentDiff = bm.measureBrightness() - btk.getTarget();
		prevDiff = currentDiff;
	}

	public float calcTurn() {

		float bright = bm.measureBrightness();

		currentDiff = bright - btk.getTarget();

		float turn = P*currentDiff;

		turn += D*(currentDiff - prevDiff) / DELTA;

		integral += I * ((currentDiff + prevDiff) / 2.0F) *DELTA;

		turn += integral;

		return turn;
	}

}
