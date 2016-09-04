package drive_control;

import hardware.Hardware;

public class BrightMeasure {
	public float measureBrightness() {

		return Hardware.getBrightness();
	}
	
	public float measureNormalizedBrightness(float White,float Black) { 
		//ˆø”:”’‚¨‚æ‚Ñ•‚Ì‹P“x’lŠî€
		float brightness;
		brightness = Hardware.getBrightness();
		return (White - brightness)/(White - Black);	//”’‚ğ1A•‚ğ0‚Æ‚µ‚Ä³‹K‰»
	}
}
