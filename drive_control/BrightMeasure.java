package drive_control;

import hardware.BrightSensor;

public class BrightMeasure {

	BrightSensor bright;

	public BrightMeasure(BrightSensor bright){
		this.bright = bright;
	}
	public float measureBrightness() {

		return bright.getBright();
	}

	public float measureNormalizedBrightness(float White,float Black) {
		//����:������э��̋P�x�l�
		float brightness;
		brightness = bright.getBright();
		if(White != Black){
			return (White - brightness)/(White - Black);	//����1�A����0�Ƃ��Đ��K��
		}
		else{
			return 0.5F;//�O���Z�ɂȂ�ꍇ�����O�B���C����ɂ�����̂Ƃ���
		}
	}
}
