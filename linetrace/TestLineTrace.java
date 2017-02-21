package linetrace;

import java.util.Timer;
import java.util.TimerTask;

import area_param.DistanceMeasure;
import drive_control.BrightTargetKeeper;
import hardware.BrightSensor;
import hardware.GyroSensor;
import hardware.TailMotor;
import hardware.TouchSensor;
import hardware.UltrasonicSensor;
import hardware.WheelMotor;
import lejos.hardware.Sound;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.port.TachoMotorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import starter.Calibrater;
import starter.Starter;

public class TestLineTrace {

	public static int START_COMMAND = 71;


	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		 // モータ制御用オブジェクト
        // EV3LargeRegulatedMotor では PWM 制御ができないので、TachoMotorPort を利用する
        TachoMotorPort motorPortL = MotorPort.C.open(TachoMotorPort.class); // 左モータ
        TachoMotorPort motorPortR = MotorPort.B.open(TachoMotorPort.class); // 右モータ
        TachoMotorPort motorPortT = MotorPort.A.open(TachoMotorPort.class); // 尻尾モータ

     // タッチセンサ
        EV3TouchSensor touchSensor = new EV3TouchSensor(SensorPort.S1);
        SensorMode touchMode = touchSensor.getTouchMode();

        // 超音波センサ
        EV3UltrasonicSensor ultrasonicSensor = new EV3UltrasonicSensor(SensorPort.S2);
        SampleProvider distanceMode = ultrasonicSensor.getDistanceMode();  // 距離検出モード

        // カラーセンサ
        EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S3);
        SensorMode redMode = colorSensor.getRedMode();           // 輝度検出モード

        // ジャイロセンサ
        EV3GyroSensor gyroSensor = new EV3GyroSensor(SensorPort.S4);
        SampleProvider rate = gyroSensor.getRateMode();          // 角速度検出モード

        WheelMotor wheel = new WheelMotor(motorPortL, motorPortR);
        TailMotor tail = new TailMotor(motorPortT);
        GyroSensor gyro = new GyroSensor(rate);
        BrightSensor bright = new BrightSensor(redMode);
        TouchSensor touch = new TouchSensor(touchMode);
        UltrasonicSensor sonar = new UltrasonicSensor(distanceMode);


		final LineTracer lt = new LineTracer(wheel, tail, bright, gyro);
		Starter start = new Starter();
		final DistanceMeasure dm = new DistanceMeasure(wheel);
		BrightTargetKeeper tk = new BrightTargetKeeper();
		Calibrater calib = new Calibrater(bright, touch);
		float distance = 0.0F;

		Sound.beep();
		calib.calibration();
		tk.setBlack(calib.getTargets()[0]);
		tk.setWhite(calib.getTargets()[1]);
		tk.setGray(calib.getTargets()[2]);
		Sound.beep();

		ultrasonicSensor.enable();
		motorPortL.resetTachoCount();
		motorPortR.resetTachoCount();
		motorPortT.resetTachoCount();
		gyroSensor.reset();
		start.start(wheel, tail, bright, sonar, gyro, touch);


		//Sound.beep();


		//↓のタイマは輝度値制御でライントレースする。周期は4ms
		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask(){

			public void run(){
				lt.linetrace();
			}
		};

		//下のタイマは走行距離測定して，PID係数と速度を切り替えている。周期は100ms
		//Timer distanceTimer = new Timer();
		//DistanceTask distancetask = new DistanceTask(lt,dm,distance);


		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);
		//distanceTimer.scheduleAtFixedRate(distancetask, 0, 100);

		//一定距離走るとループから抜ける
		while(true){
			//distance = distancetask.getDistance();

			/*
			if(distance > 2.0F){
				driveTimer.cancel();
				//distanceTimer.cancel();
				break;
			}*/

			Delay.msDelay(20);
		}

	}

}
