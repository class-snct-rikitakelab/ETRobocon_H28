public class ForwardCalculator {
	private SpeedKeeper spKeeper = new SpeedKeeper();
	private SpeedMeasure spMeasure = new SpeedMeasure();

	public float calForward() {
		float kp = 20.0F;
		float forward;
		forward = kp*(spKeeper.getTarget() - spMeasure.getSpeed()) + 30.0F;
		return forward;
	}

}
