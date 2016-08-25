package drive_control
;

public class BrightTargetKeeper {

	private static float White = 0.36F;

	private static float Black = 0.02F;

	private static float Gray;

	private static float Step;

	public void setWhite(float white) {

		this.White = white;
	}

	public void setBlack(float black) {
		this.Black = black;
	}

	public void setGray(float gray) {

		this.Gray = gray;
	}

	public void setStep(float step) {
		this.Step = step;

	}

	public float getWhite() {
		return this.White;
	}

	public float getBlack() {
		return this.Black;
	}

	public float getGray() {
		return this.Gray;
	}

	public float getStep() {
		return this.Step;
	}

	public float getTarget(){
		return (float) ((this.White + this.Black) / 2.0);
	}

}
