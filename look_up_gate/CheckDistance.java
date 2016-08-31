package look_up_gate;

import hardware.Hardware;

public class CheckDistance{

	private float distance;

	public float getDistance(){
		int distL = Hardware.motorPortL.getTachoCount();
		int distR = Hardware.motorPortR.getTachoCount();

		distance = (distL + distR) / 2;

		//distance = (float)(Math.PI / 36 * (distR + distL));	//車輪半径  約 20 mm

		return distance/360.0F * 0.262F;
		//return distance;
	}

}
