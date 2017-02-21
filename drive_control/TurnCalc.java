package drive_control;

import area_param.AreaParamKeeper;
import area_param.AreaParamSelecter;
import hardware.BrightSensor;
import hardware.WheelMotor;

public class TurnCalc {

	private BrightMeasure bm;
	private BrightTargetKeeper btk;
	private AreaParamSelecter aps;

	float currentDiff;
	float prevDiff;
	float integral = 0.0F;

	private float Kp,Ki,Kd;

	public TurnCalc(BrightSensor bright, WheelMotor wheel){
		bm = new BrightMeasure(bright);
		btk = new BrightTargetKeeper();
		aps = new AreaParamSelecter(wheel);

		currentDiff = bm.measureBrightness() - btk.getTarget();
		prevDiff = currentDiff;
		Kp = 0.0F;
		Ki = 0.0F;
		Kd = 0.0F;
	}

	public float calcTurn() {

		//�񐳋K���ł̋P�x�l����
		float bright = bm.measureBrightness();

		currentDiff = bright - btk.getTarget();

		//���K���ł̋P�x�l����
		/*
		float bright = bm.measureNormalizedBrightness(btk.getWhite(),btk.getBlack);

		currentDiff = bright - btk.getNormalizedTarget();
		*/
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
