package linetrace
;

public class BrightTargetKeeper {

	private float White = 0.36F;

	private float Black = 0.02F;

	private float Gray;

	private float Step;

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
