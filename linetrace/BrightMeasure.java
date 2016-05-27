package linetrace;

import hardware.Hardware;

public class BrightMeasure {
	public float measureBrightness() {
		float[] brightness = new float[Hardware.redMode.sampleSize()];

		Hardware.redMode.fetchSample(brightness, 0);

		return brightness[0];
	}
}
