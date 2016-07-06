package jp.etrobo.ev3.sample;

import hardware.Hardware;

public class getBrightness {

	//計測値を取得する
	public float getBright() {
		return Hardware.getBrightness();
	}

}
