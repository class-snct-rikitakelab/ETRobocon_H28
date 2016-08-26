package drive_control;

import area_param.AreaParamSelecter;

public class ForwardSelecter {

	private AreaParamSelecter aps;

	private float forward;

	public ForwardSelecter(){
		aps = new AreaParamSelecter();
		forward = 0.0F;
	}

	public float SelectForward(){
		return forward;
	}

	public void updateForward(float distance){
		forward = aps.getSpeed(distance);
	}

}
