package drive_control;

import area_param.AreaParamSelecter;
import hardware.WheelMotor;

public class ForwardSelecter {

	private AreaParamSelecter aps;

	private float forward;

	public ForwardSelecter(WheelMotor wheel){
		aps = new AreaParamSelecter(wheel);
		forward = 0.0F;
	}

	public float SelectForward(){
		return forward;
	}

	public void updateForward(float distance){
		forward = aps.getSpeed(distance);
	}

}
