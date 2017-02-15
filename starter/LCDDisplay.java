package starter;

import java.util.TimerTask;

import lejos.hardware.lcd.LCD;

public class LCDDisplay extends TimerTask{

	private String displayStr="";

	public LCDDisplay(){

	}

	public void setString(String input){
		displayStr = input;
	}

	public void DisplayLCD() {
		LCD.refresh();
		LCD.drawString(displayStr,0,3);
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		DisplayLCD();
	}

}
