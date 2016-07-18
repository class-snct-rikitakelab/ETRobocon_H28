package steps;

import java.util.Timer;
import java.util.TimerTask;

import lejos.hardware.Battery;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.BasicMotorPort;
import lejos.utility.Delay;

public class StepSolver {
	//定数
	private final float GYRO_OFFSET          = 0.0F;
	private final int   TAIL_ANGLE_STAND_UP  = 94;
	private final int   TAIL_ANGLE_BALANCE     = 3;
	private final double WHEEL_DISTANCE = 0.175; // 車輪間の距離
	private final double WHEEL_DIAMETER = 0.083; // 車輪の直径
	private final float P_GAIN               = 2.5F;
	private final int   PWM_ABS_MAX          = 60;
	private final float	K_P				= 2.5F;
	private final int	STAND_ANGLE 	= 90;
	private final int	START_ANGLE		= 76;

	private final float	GYRO_LIMIT		= 80.0F;
	private final int	TACHO_LIMIT		= 450;

	private EV3Body body = new EV3Body();
	//private LogSender sender = new LogSender();

	private float pwmL;
	private float pwmR;

	private float gyroNow;
	private int thetaL;
	private int thetaR;
	private int thetaI;
	private int battery;

	private int lastEncR = 0;
	private int lastEncL = 0;
	// 走行状態
	private double distanceDefault = 0;
	private int timeDefault = 0;
	private double turnDefault = 0;

	/** 走行速度 */
	float speed = 0;
	/** 回転速度     +が右回転 */
	float turn = 0;
	/** 尻尾角度 */
	private int tail = 0;
	/** ライントレースしきい値 */
	private float LTthreshold = 0.25f;
	/** ライントレース乗数 */
	private float LTmultiplier = (-100);

	// 直進検知用
	private final int VALUE_STRAIGHT = 50;
	private final float THRESHOLD_TURN = 1f;
	private float turnValue[] = new float[VALUE_STRAIGHT];
	private char turnPoint  = 0;

	// 経過時間関連
	private long time4ms;
	long starttime;
	private char waitint = 0;
	private char progress = 0;
	private float keepRotationDefault = 0;

	private boolean isEnd = false;

	public void main(String[] args){
		init();
		LCD.drawString("ready", 0, 0);
		Sound.beep();
		boolean touchPressed = false;
		for (;;) {
			tailControl(TAIL_ANGLE_STAND_UP); //complete stop for angle control
			if (body.touchSensorIsPressed()) {
				touchPressed = true;          // touch sensor is pressed
			} else {
				if (touchPressed) break;      // touch sensor I was released after being pressed
			}
			Delay.msDelay(20);
		}
		EV3Body.motorPortL.resetTachoCount();   // left motor encoder reset
		EV3Body.motorPortR.resetTachoCount();   // right motor encoder reset
		Balancer.init();            // inverted pendulum control initialization

		starttime = System.nanoTime();

		checkDistanceInit();
		parallelToStep();
		climbStep();
		parallelToStep2();
		climbStep2();
		goCenter();
		down();

		speed = 0;
		turn = 0;
		runByTail();

		//sender.send();
	}



/** 低速で走行し、ライントレースで向きを板と合わせる */
	public void parallelToStep(){
		isEnd = false;
		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask() {
			int i = 0;
			boolean isFirst = true;
			@Override
			public void run() {
				switch(i){
				case 0://最初に静止する
					if(isFirst){
						checkTimeInit();
						speed = 0;
						isFirst = false;
					}
					setTurnByLT();
					runByBalance();
					if(checkTime(5000)){
						++i;
						isFirst = true;
					}
					break;
				case 1://LTで前に一定距離進む
					if(isFirst){
						speed = 30;
						checkStraightInit();
						isFirst = false;
					}
					setTurnByLT();
					runByBalance();
					if(checkDistance(100)){//なるべく真っ直ぐな状態で停止するように
						++i;
						isFirst = true;
					}
					break;
				case 2://減速しながら進んで向きを合わせる
					if(isFirst){
						speed = 10;
						checkStraightInit();
						isFirst = false;
					}
					setTurnByLT();
					runByBalance();
					if((checkStraight()&&checkDistance(150))||checkDistance(200)){//なるべく真っ直ぐな状態で停止するように
						++i;
						isFirst = true;
					}
					break;
				case 3://停止して加速に備える
					if(isFirst){
						speed = 0;
						turn = 0;
						checkTimeInit();
						keepRotationInit();
						isFirst = false;
					}
					runByBalance();
					if(checkTime(2000)){
						++i;
						isEnd = true;
					}
					break;
				}
			}
		};
		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);
		while(true){
			if(isEnd){
				driveTask.cancel();
				isEnd = false;
				break;
			}
			Delay.msDelay(4);
		}
	}

/** 高速走行して段を登り、減速する */
	public void climbStep(){
		tail = TAIL_ANGLE_BALANCE;
		isEnd = false;
		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask() {
			int i = 0;
			int stoper = 0;
			boolean isFirst = true;
			@Override
			public void run() {
				switch(i){
				case 0:
					if(isFirst){
						speed = -20;
						checkTimeInit();
						isFirst = false;
					}
					keepRotation();
					runByBalance();
					if(checkTime(300)){
						++i;
						isFirst = true;
					}
					break;
				case 1:
					if(isFirst){
						speed = 30;
						checkTimeInit();
						isFirst = false;
					}
					keepRotation();
					turn -= 5;
					if((++stoper > 1)&&(speed < 100)){
						stoper = 0;
						++speed;
					}
					runByBalance();
					if(checkDistance(250)){
						++i;
						isFirst = true;
					}
					break;
				case 2:
					if(isFirst){
						speed = 40;
						isFirst = false;
					}
					//if(++stoper > 4){
					stoper = 0;
					speed -= 1;
					//}*/
					keepRotation();
					runByBalance();
					if(speed < 0){
						++i;
						isFirst = true;
					}
					break;
				case 3:
					if(isFirst){
						speed = 0;
						isFirst = false;
					}
					keepRotation();
					runByBalance();
					if(checkTime(3000)){
						isEnd = true;
					}
					break;

				}
			}
		};
		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);
		while(true){
			if(isEnd){
				driveTask.cancel();
				isEnd = false;
				break;
			}
			Delay.msDelay(4);
		}
	}

	/** 1段目上で板と平行にする */
	public void parallelToStep2(){
		isEnd = false;
		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask() {
			int i = 0;
			boolean isFirst = true;
			@Override
			public void run() {
				switch(i){
				case 0:
					if(isFirst){
						speed = 0;
						turn = 0;
						checkTimeInit();
						isFirst = false;
					}
					keepRotation();
					runByBalance();
					speed = checkDistanceDifference(370)/3;
					speed = (float) clamp(speed,10,-10);
					if(checkTime(4000)){
						++i;
						isFirst = true;
					}
					break;
				case 1:
					if(isFirst){
						speed = 0;
						turn = 0;
						checkTimeInit();
						isFirst = false;
					}
					runByBalance();
					if(checkTime(2000)){
						++i;
						isFirst = true;
					}
					break;
				case 2:
					if(isFirst){
						speed = 10;
						isFirst = false;
					}
					setTurnByLT();
					int bal = Balancer.getPwmL()+Balancer.getPwmR();
					turn *= Math.signum(bal+10)/**(Math.abs(bal)+10)/60*/;
					runByBalance();
					if(checkDistance(400)){
						++i;
						isFirst = true;
					}
					break;
				case 3:
					if(isFirst){
						speed = -10;
						turn = 0;
						isFirst = false;
					}
					//setTurnByLT();
					runByBalance();
					if(!checkDistance(380)){
						++i;
						isFirst = true;
					}
					break;
				case 4:
					if(isFirst){
						speed = 0;
						turn = 0;
						checkTimeInit();
						isFirst = false;
					}
					runByBalance();
					if(checkTime(2000)){
						++i;
						isFirst = true;
					}
					break;
				case 5:
					if(isFirst){
						speed = 5;
						checkStraightInit();
						isFirst = false;
					}
					setTurnByLT();
					bal = Balancer.getPwmL()+Balancer.getPwmR();
					turn *= Math.signum(bal+10)/**(Math.abs(bal)+10)/60*/;
					runByBalance();
					if((checkStraight()&&checkDistance(400))||checkDistance(450)){
						++i;
						isFirst = true;
					}
					break;
				case 6:
					if(isFirst){
						speed = 0;
						turn = 0;
						checkTimeInit();
						isFirst = false;
					}
					runByBalance();
					if(checkTime(2000)){
						keepRotationInit();
						isEnd = true;
					}
					break;
				}
			}
		};
	driveTimer.scheduleAtFixedRate(driveTask, 0, 4);
		while(true){
			if(isEnd){
				driveTask.cancel();
				isEnd = false;
				break;
			}
			Delay.msDelay(4);
		}
	}

/** 高速走行して２段目を登り、減速する */
	public void climbStep2(){
		tail = TAIL_ANGLE_BALANCE;
		isEnd = false;
		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask() {
			int stoper = 0;
			int i = 0;
			boolean isFirst = true;
			@Override
			public void run() {
				switch(i){
				case 0:
					if(isFirst){
						speed = -20;
						checkTimeInit();
						isFirst = false;
					}
					keepRotation();
					runByBalance();
					if(checkTime(300)){
						++i;
						isFirst = true;
					}
					break;
				case 1:
					if(isFirst){
						speed = 30;
						checkTimeInit();
						isFirst = false;
					}
					keepRotation();
					turn -= 5;
					if((++stoper > 1)&&(speed < 100)){
						stoper = 0;
						++speed;
					}
					runByBalance();
					if(checkDistance(500)){
						++i;
						isFirst = true;
					}
					break;
				case 2:
					if(isFirst){
						speed = 40;
						isFirst = false;
					}
					//if(++stoper > 4){
					stoper = 0;
					speed -= 1;
					//}*/
					keepRotation();
					runByBalance();
					if(speed < 0){
						++i;
						isFirst = true;
					}
					break;
				case 3:
					if(isFirst){
						speed = 0;
						isFirst = false;
					}
					keepRotation();
					runByBalance();
					if(checkTime(3000)){
						isEnd = true;
					}
					break;
				}
			}
		};
		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);
		while(true){
			if(isEnd){
				driveTask.cancel();
				isEnd = false;
				break;
			}
			Delay.msDelay(4);
		}
	}

/** 二段目から降りる体勢にする */
	public void goCenter(){
		isEnd = false;
		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask() {
			int i = 0;
			boolean isFirst = true;
			@Override
			public void run() {
				switch(i){
				case 0:
					if(isFirst){
						speed = 0;
						turn = 0;
						checkTimeInit();
						isFirst = false;
					}
					runByBalance();
					speed = checkDistanceDifference(550)/3;
					speed = (float) clamp(speed,10,-10);
					if(checkTime(4000)){
						++i;
						isFirst = true;
						isEnd = true;
					}
					break;
				case 1:
					if(isFirst){
						speed = 0;
						turn = 0;
						checkTimeInit();
						isFirst = false;
					}
					runByBalance();
					if(checkTime(2000)){
						++i;
						isFirst = true;
						isEnd = true;
					}
					break;
				case 2:
					if(isFirst){
						speed = 10;
						isFirst = false;
					}
					setTurnByLT();
					runByBalance();
					if(checkDistance(600)){
						++i;
						isFirst = true;
						isEnd = true;
					}
					break;
				case 3:
					if(isFirst){
						speed = 0;
						turn = 0;
						checkTimeInit();
						isFirst = false;
					}
					runByBalance();
					if(checkTime(2000)){
						++i;
						isFirst = true;
						isEnd = true;
					}
					break;
				}
			}
		};
		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);
		while(true){
			if(isEnd){
				driveTask.cancel();
				isEnd = false;
				break;
			}
		}
	}

/** 一回転する  */
/*	public void turn(){
		speed =0;
		turn = 0;
		tail = TAIL_ANGLE_BALANCE;
		checkTimeInit();
		while(true){
			runByBalance();
			if(checkTime(3000))break;
			wait4ms();
		}

		speed = 0;
		turn = 30;
		tail = TAIL_ANGLE_BALANCE;
		checkTurnInit();
		while(true){
			runByBalance();
			if(checkTurn(360))break;
			wait4ms();
		}

		Sound.beep();

		speed = 0;
		turn = 0;
		tail = TAIL_ANGLE_BALANCE;
		checkTimeInit();
		while(true){
			runByBalance();
			if(checkTime(1000))break;
			wait4ms();
		}
	}*/

	public void down(){
		thetaL = body.motorPortL.getTachoCount();
		thetaR = body.motorPortR.getTachoCount();
		thetaI = (int) (distanceDefault + 600 / (Math.PI * WHEEL_DIAMETER));
		Timer driveTimer = new Timer();
		class DriveTimerTask extends TimerTask {
			float threshold = 0.15F;
			int theta_ave;
			int down_theta = 0;
			float forward;
			float turn;
			int count = 0;
			int stopcount = 0;
			private Boolean downflag;
			private DriveTimerTask(Boolean downflag){
				this.downflag = downflag;
			}
			@Override
			public void run() {

				float pwm = (float)(0 - body.motorPortT.getTachoCount()) * P_GAIN;

				if(pwm > PWM_ABS_MAX){
					pwm = PWM_ABS_MAX;
				}
				else if(pwm < -PWM_ABS_MAX){
					pwm = -PWM_ABS_MAX;
				}
				body.motorPortT.controlMotor((int)pwm, 1);

				gyroNow = body.getGyroValue();
				thetaL = body.motorPortL.getTachoCount();
				thetaR = body.motorPortR.getTachoCount();
				theta_ave = (thetaL + thetaR - thetaI)/2;
				battery = Battery.getVoltageMilliVolt();
				if(count < 500){
					forward = 5.0F;
					turn = 0.0F;
					count++;
				}else{
					if(Math.abs(gyroNow)<GYRO_LIMIT && downflag == true && theta_ave < TACHO_LIMIT ){
						forward = 50.0F;
						turn = (body.getBrightness() - threshold)*300;
						if(turn >100){
							turn = 100;
						}
						else if(turn < -100){
							turn = -100;
						}
					}else if(Math.abs(gyroNow)>GYRO_LIMIT && downflag == true && theta_ave > TACHO_LIMIT ){
						forward = 40.0F;
						turn = 0.0F;
						downflag = false;
						down_theta = theta_ave;
					}else if(stopcount < 200 && downflag == false && down_theta < theta_ave + 150) {
						forward = 40.0F;
						turn = 0.0F;
						stopcount++;
					}else{
						forward = 20.0F;
						threshold = 0.2F;
						turn = (body.getBrightness() - threshold)*300;
						if(turn >100){
							turn = 100;
						}
						else if(turn < -100){
							turn = -100;
						}
						stopcount++;
						//logSender.disconnect();
					}


				}
				Balancer.control(forward,turn,gyroNow,GYRO_OFFSET,thetaL,thetaR,battery);
				body.motorPortL.controlMotor(Balancer.getPwmL(), 1);
				body.motorPortR.controlMotor(Balancer.getPwmR(), 1);



			}

			public int getStopCount(){
				return this.stopcount;
			}
			public void setStopCount(int stopcount){
				this.stopcount = stopcount;
			}
		};

		DriveTimerTask driveTask = new DriveTimerTask(true);

		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);
		for(;;){
			//logSender.send();
			if(body.touchSensorIsPressed()){
				LCD.drawString("true", 0, 0);
			}
			else{
				LCD.drawString("false", 0, 0);
			}
			LCD.drawInt((int)body.getGyroValue(), 3, 3);
			//Delay.msDelay(100);
			LCD.clear();
			if(driveTask.getStopCount() > 350 && body.getBrightness() <0.2F){
				driveTask.cancel();
				this.tailControl(START_ANGLE);
				if(body.motorPortT.getTachoCount()*2 < START_ANGLE){
					body.motorPortL.controlMotor(80, 1);
					body.motorPortR.controlMotor(80, 1);
				}else{
					body.motorPortL.controlMotor(0, 0);
					body.motorPortR.controlMotor(0, 0);
				}
			}
			//Delay.msDelay(20);
		}
	}



	public void init(){
		EV3Body.gyro.reset();
		EV3Body.sonar.enable();
		EV3Body.motorPortL.setPWMMode(BasicMotorPort.PWM_BRAKE);
		EV3Body.motorPortR.setPWMMode(BasicMotorPort.PWM_BRAKE);
		EV3Body.motorPortT.setPWMMode(BasicMotorPort.PWM_BRAKE);

		for (int i=0; i < 1500; i++) {
			EV3Body.motorPortL.controlMotor(0, 0);
			EV3Body.motorPortR.controlMotor(0, 0);
			EV3Body.motorPortT.controlMotor(0, 0);
			body.getBrightness();
			body.getSonarDistance();
			body.getGyroValue();
			Battery.getVoltageMilliVolt();
			Balancer.control(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8000);
			System.nanoTime();
			//sender.addLog("name", 100, System.nanoTime()/100000.0f);
			//sender.addLog("name", 100.0f, System.nanoTime()/100000.0f);
			//sender.addLog("name", "100.0f", System.nanoTime()/100000.0f);
		}
		Delay.msDelay(1000);       // In another thread wait until the wonder time that HotSpot is complete.

		EV3Body.motorPortL.controlMotor(0, 0);
		EV3Body.motorPortR.controlMotor(0, 0);
		EV3Body.motorPortT.controlMotor(0, 0);
		EV3Body.motorPortL.resetTachoCount();   // left motor encoder reset
		EV3Body.motorPortR.resetTachoCount();   // right motor encoder reset
		EV3Body.motorPortT.resetTachoCount();   // tail motor encoder reset
		Balancer.init();            // inverted pendulum control initialization
		//sender.clear();
	}

	private final void tailControl(int angle) {
		float pwm = (float)(angle - EV3Body.motorPortT.getTachoCount()) * P_GAIN; // proportional control
		// PWM output saturation processing
		if (pwm > PWM_ABS_MAX) {
			pwm = PWM_ABS_MAX;
		} else if (pwm < -PWM_ABS_MAX) {
			pwm = -PWM_ABS_MAX;
		}
		EV3Body.motorPortT.controlMotor((int)pwm, 1);
	}

	/** カラーセンサの値からturn値を決定する ******************************************************************************************************/
	private void setTurnByLT(){
		turn = (LTthreshold - body.getBrightness()) * LTmultiplier/* * Math.signum(Balancer.getPwmL()+Balancer.getPwmR())*/;
	}

	/** 倒立振子を用いてモータを動作させる */
	private void runByBalance(){
		Balancer.control(speed, turn, body.getGyroValue(), GYRO_OFFSET, Encoder.encoderL(), Encoder.encoderR(), Battery.getVoltageMilliVolt());
		EV3Body.motorPortL.controlMotor(Balancer.getPwmL(), 1);
		EV3Body.motorPortR.controlMotor(Balancer.getPwmR(), 1);
		tailControl(tail);
	}

	/** 倒立振子を用いずにモータを動作させる */
	void runByTail(){
		EV3Body.motorPortL.controlMotor((int) clamp(speed-turn,100,-100), 1);
		EV3Body.motorPortR.controlMotor((int) clamp(speed+turn,100,-100), 1);
		tailControl(tail);
	}

	/** checkDistanceInit()が呼び出されてからの距離が目標距離より大きいか
	 * @param target 目標距離 必ず正
	 * @return 目標値より測った距離のほうが大きい時true */
	private boolean checkDistance(double target){
		/*if(distanceTarget > 0){
			if(((Encoder.encoderL() + Encoder.encoderR()) / 2 - distanceDefault) > distanceTarget)return true;
		} else{
			if(((Encoder.encoderL() + Encoder.encoderR()) / 2 - distanceDefault) < distanceTarget)return true;
		}*/
		if(Math.abs(calcDistance() - distanceDefault) > target)return true;
		return false;
	}

	/** 目標位置との差を計算する
	 * @return 目標位置との差
	 * 目標値より進んでいる場合は正、 後ろの場合は負*/
	private float checkDistanceDifference(float target){
		return (float) (distanceDefault + target - calcDistance());
	}

	/** 距離リセット */
	void checkDistanceInit(){
		distanceDefault = calcDistance();
	}

	private boolean checkTime(int target){
		if((System.nanoTime() / 1000000 - timeDefault) > target)return true;
		return false;
	}

	private void checkTimeInit(){
		timeDefault = (int) (System.nanoTime() / 1000000);
	}

	private boolean checkTurn(int target){
		if(0 < target)if(target < (calcRotation() - turnDefault))return true;
		//else if((calcRotation() - turnDefault) < target)return true;
		return false;
	}

	private void checkTurnInit(){
		turnDefault = calcRotation();
	}

	private boolean checkStraight(){
		return false;
		/*turnValue[turnPoint] = turn;
		if(++turnPoint>=THRESHOLD_TURN) turnPoint = 0;
		for(int i = 0;i < VALUE_STRAIGHT; ++i){
			if((turnValue[i] < -THRESHOLD_TURN)||(THRESHOLD_TURN < turnValue[i])){
				return false;
			}
		}
		return true;*/
	}

	private void checkStraightInit(){
		for(int i = 0;i < VALUE_STRAIGHT; ++i){
			turnValue[i] = 999;
		}
	}

	private void keepRotation(){
		turn = (keepRotationDefault - (float)calcRotation());
	}

	private void keepRotationInit(){
		keepRotationDefault = (float)calcRotation();
	}

/** 走行距離の計算 */
	private double calcDistance(){
		return Math.PI * WHEEL_DIAMETER * (Encoder.encoderL() + Encoder.encoderR());
	}

/** 旋回角度の計算 */
	private double calcRotation(){
		return (Encoder.encoderL() - Encoder.encoderR()) * 0.245;
	}

	private double clamp(double value, double max, double min){
		return Math.max(min, Math.min(value,max));
	}

	public void ready(){
		boolean touchPressed = false;
		for (;;) {
			tailControl(TAIL_ANGLE_STAND_UP); //complete stop for angle control
			if (body.touchSensorIsPressed()) {
				touchPressed = true;          // touch sensor is pressed
			} else {
				if (touchPressed) break;      // touch sensor I was released after being pressed
			}
			Delay.msDelay(20);
		}
		EV3Body.motorPortL.resetTachoCount();   // left motor encoder reset
		EV3Body.motorPortR.resetTachoCount();   // right motor encoder reset
		Balancer.init();            // inverted pendulum control initialization
	}

}

/*
//テンプレ
		isEnd = false;
		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask() {
			int i = 0;
			boolean isFirst = true;
			@Override
			public void run() {
				switch(i){
				case 0:
					if(isFirst){
						isFirst = false;
					}
					if(){
						++i;
						isFirst = true;
					}
					break;
				case 1:
					if(isFirst){
						isFirst = false;
					}
					if(){
						++i;
						isFirst = true;
					}
					break;
				case 2:
					if(isFirst){
						isFirst = false;
					}
					if(){
						isEnd = true;
					}
					break;
				}
			}
		};
		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);
		while(true){
			if(isEnd){
				driveTask.cancel();
				isEnd = false;
				break;
			}
		}
*/


