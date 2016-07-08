package linetrace;

public class TargetKeeper {

	private float bright_white; //��

	private float bright_black; //��

	private float bright_gray; //�D�F

	private float bright_figure; //�K�i

	public void setBrightWhite(float brightness) {
		bright_white = brightness;
		return;
	}

	public void setBrightBlack(float brightness) {
		bright_black = brightness;
		return;
	}

	public void setBrightGray(float brightness) {
		bright_gray = brightness;
		return;
	}

	public void setBrightFigure(float brightness) {
		bright_figure = brightness;
		return;
	}

	public float getBrightWhite() {
		return bright_white;
	}

	public float getBrightBlack() {
		return bright_black;
	}

	public float getBrightGray() {
		return bright_gray;
	}

	public float getBrightFigure() {
		return bright_figure;
	}
}
