package area_param;

import hardware.WheelMotor;

public class DistanceMeasure {

	WheelMotor wheel;

	public DistanceMeasure(WheelMotor wheel){
		this.wheel = wheel;

	}

	public float measureDistance_Degree(){

		float rotate = wheel.getTachoL()+wheel.getTachoR();

		rotate = rotate / 2.0F;

		return rotate;
	}

	public float measureDistance_Meter(){

		float rotate = wheel.getTachoL()+wheel.getTachoR();

		rotate = rotate / 2.0F;

		return (rotate / 360.0F) * 0.262F;

	}

	public float measureDistance_CMeter(){

		float rotate = wheel.getTachoL()+wheel.getTachoR();

		rotate = rotate / 2.0F;

		return (rotate / 360.0F) * 26.2F;

	}

}
