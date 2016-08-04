package linetrace;

public class SpeedKeeper {

	private static float spTarget = 10.0F;

	private static float forwardTargets[] = {100.0F,30.0F,100.0F,30.0F,100.0F,30.0F,100.0F};

	public static void setTarget(float target) {
		spTarget = target;
	}

	public float getForwardTarget(int index){
		return forwardTargets[index];
	}

	public float getTarget() {
		return spTarget;
	}

}
