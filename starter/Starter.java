package starter;

import java.util.Timer;
import java.util.TimerTask;

import Balancer.Balancer;
import hardware.BrightSensor;
import hardware.GyroSensor;
import hardware.TailMotor;
import hardware.TouchSensor;
import hardware.UltrasonicSensor;
import hardware.WheelMotor;
import lejos.hardware.Battery;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import motor_control.tailCtrl;

public class Starter {

	StartCommandDetecter scd;
	tailCtrl tail;

	public void start(WheelMotor wheel,TailMotor tail,BrightSensor bright,
			UltrasonicSensor sonar,GyroSensor gyro,TouchSensor touch){

		scd = new StartCommandDetecter(touch);
		this.tail = new tailCtrl(tail);


		init(wheel,tail,bright,sonar,gyro,touch);

		Timer CommandTimer = new Timer();
		TimerTask CommandTask = new TimerTask(){

			public void run(){
				scd.esta();
			}
		};

		CommandTimer.scheduleAtFixedRate(CommandTask, 0, 20);

		while(true){

			if(scd.checkCommand() == true){
				break;
			}

			this.tail.tailThree();

			Delay.msDelay(20);
		}

		CommandTimer.cancel();

		while(true){

			if(this.tail.getTailAngle() >= 96){
				this.tail.tailTwo();
				break;
			}

			this.tail.tailStart();
		}
	}

	static void init(WheelMotor wheel,TailMotor tail,BrightSensor bright,
			UltrasonicSensor sonar,GyroSensor gyro,TouchSensor touch){
		LCD.drawString("Please Wait...  ", 0, 4);
		/*Hardware.gyro.reset();
	    Hardware.sonar.enable();*/

	    // Java の初期実行性能が悪く、倒立振子に十分なリアルタイム性が得られない。
	    // 走行によく使うメソッドについて、HotSpot がネイティブコードに変換するまで空実行する。
	    // HotSpot が起きるデフォルトの実行回数は 1500。
	    for (int i=0; i < 1500; i++) {
	    	wheel.controlWheel(0, 0);
	    	tail.controlMotor(0);
	    	bright.getBright();
	    	sonar.getDistance();
	    	gyro.getGyroValue();
	    	touch.touchSensorIsPressed();
	        Battery.getVoltageMilliVolt();
	        Balancer.control(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8000);
	    }
	    Delay.msDelay(10000);       // 別スレッドで HotSpot が完了するだろう時間まで待つ。

	    /*
	    Hardware.motorPortL.controlMotor(0, 0);
	    Hardware.motorPortR.controlMotor(0, 0);
	    Hardware.motorPortT.controlMotor(0, 0);
	    Hardware.motorPortL.resetTachoCount();   // 左モータエンコーダリセット
	    Hardware.motorPortR.resetTachoCount();   // 右モータエンコーダリセット
	    Hardware.motorPortT.resetTachoCount();   // 尻尾モータエンコーダリセット
	    */
	    Balancer.init();            // 倒立振子制御初期化
	    LCD.clear();
	}

}
