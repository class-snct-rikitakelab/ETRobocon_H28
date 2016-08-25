package drive_control;

import hardware.Hardware;

public class BrightMeasure {
	public float measureBrightness() {

		return Hardware.getBrightness();
	}
}
