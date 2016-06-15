import hardware.Hardware;

public class SpeedMeasure {

	private Hardware hardware;

	private int oldTachoL;
	private int oldTachoR;
	private long oldmilliSec;

	public SpeedMeasure(){
		hardware = new Hardware();
		oldTachoL = oldTachoR = 0;
		oldmilliSec = System.currentTimeMillis();
	}

	public float getSpeed() {
		float speed;
		int tachoL;
		int tachoR;
		long milliSec;
		int dTacho;
		
		tachoL = hardware.motorPortL.getTachoCount();
		tachoR = hardware.motorPortR.getTachoCount();
		milliSec = System.currentTimeMillis();
		
		dTacho = ((tachoL-oldTachoL)+(tachoR-oldTachoR))/2;
		speed = dTacho/(milliSec-oldmilliSec);

		oldTachoL = tachoL;
		oldTachoR = tachoR;
		oldmilliSec = milliSec;

		return speed;
	}

}
