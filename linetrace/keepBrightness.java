package jp.etrobo.ev3.sample;

import lejos.hardware.lcd.LCD;

public class keepBrightness {

	private static float whiteBrightness;
	private static float blackBrightness;
	private static float grayBrightness;
	private static float stairBrightness;

	/*
	 * 白色の輝度値を取得する
	 */
	static void setWhite(float white){
		whiteBrightness = white;
		LCD.clear();
		LCD.refresh();
		System.out.println(white);
        LCD.drawString("white", 5, 2);
	}

	/*
	 * 黒色の輝度値を取得する
	 */
	static void setBlack(float black){
		blackBrightness = black;
		LCD.clear();
		System.out.println(black);
        LCD.drawString("black", 5, 2);
	}

	/*
	 * 灰色の輝度値を取得する
	 */
	static void setGray(float gray){
		grayBrightness = gray;
		LCD.clear();
		System.out.println(gray);
        LCD.drawString("gray", 5, 2);
	}

	/*
	 * 階段の輝度値を取得する
	 */
	static void setStair(float stair){
		stairBrightness = stair;
		LCD.clear();
		System.out.println(stair);
        LCD.drawString("stair", 5, 2);
	}

	/*
	 * 白色の輝度値を設定する
	 */
	static float getWhite(){
		return whiteBrightness;
	}

	/*
	 * 黒色の輝度値を設定する
	 */
	static float getBlack(){
		return blackBrightness;
	}

	/*
	 * 灰色の輝度値を設定する
	 */
	static float getGray(){
		return grayBrightness;
	}

	/*
	 * 階段の輝度値を設定する
	 */
	static float getStair(){
		return stairBrightness;
	}


}
