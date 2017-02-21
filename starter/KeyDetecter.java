package starter;

import java.util.TimerTask;

import lejos.hardware.Button;

public class KeyDetecter extends TimerTask {

	private boolean keyGyro = false;

	private boolean keyCal = false;

	private boolean keyNext = false;

	public boolean checkFlagGyro() {
		return this.keyGyro;
	}

	public boolean checkFlagCal() {
		return this.keyCal;
	}

	public boolean checkFlagNext(){
		return this.keyNext;
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ

		int key = Button.readButtons();

		switch(key){
		case Button.ID_ENTER:
			this.keyNext = true;
			break;
		case Button.ID_LEFT:
			this.keyGyro = true;
			break;
		case Button.ID_RIGHT:
			this.keyCal = true;
			break;
		default:
			this.keyCal = false;
			this.keyGyro = false;
			this.keyNext = false;
			break;
		}
	}

}
