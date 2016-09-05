package steps;

import java.util.Timer;
import java.util.TimerTask;

import drive_control.BrightMeasure;
import hardware.Hardware;
import Balancer.Balancer;
import lejos.hardware.Battery;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.BasicMotorPort;
import lejos.utility.Delay;

public class StepSolver {
	//定数
	private float GYRO_OFFSET          = 0.0F;
	private static final int   TAIL_ANGLE_STAND_UP  = 94;
	private static final int   TAIL_ANGLE_BALANCE     = 3;
	private static final double WHEEL_DISTANCE = 0.175; // 車輪間の距離
	private static final double WHEEL_DIAMETER = 0.083; // 車輪の直径
	private final float P_GAIN               = 5.5F;//2.5F
	private final int   PWM_ABS_MAX          = 60;
	private final float	K_P				= 2.5F;
	private final int	STAND_ANGLE 	= 90;
	private final int	START_ANGLE		= 70;


	private float bright = 0;

	/** 距離補正用 階段の1段目の位置と階段システム開始位置の距離 単位はmm */
	private final int START_DISTANCE = 0;//575

	private final float	GYRO_LIMIT		= 80.0F;
	private final int	TACHO_LIMIT		= 450;

	private LogSender sender = new LogSender();

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

	public float blackColor = 0.04f;
	public float whiteColor = 0.40f;
	public float grayColor  = 0.20f;

	/** 走行速度 */
	float speed = 0;
	/** 回転速度     +が右回転 */
	float turn = 0;
	/** 尻尾角度 */
	int tail = 0;
	/** ライントレースしきい値 */
	private float LTthreshold = (blackColor+whiteColor)/2;
	/** ライントレース乗数 */
	private static float LTmultiplier = 100;

	private int grayCount = 0;
	private int grayTraceCount = 0;
	private float LTnormal = LTthreshold;
	private float LTtogray = (grayColor+whiteColor);
	private float LTgray = (grayColor+whiteColor)/2;
	private int blackCount = 0;

	// 段差衝突検知用
	private static final int TACHO_BUMP = 30; // 保存するエンコーダ値の数
	private static final int THRESHOLD_TACHO = -15; // 衝突したと判定するための値の差の基準値
	private static int tachoBumpL[] = new int[TACHO_BUMP];
	private static int tachoBumpR[] = new int[TACHO_BUMP];
	private static boolean isBumpL = false;
	private static boolean isBumpR = false;

	private static double avarageVoltage = 0;
	private static final int THRESHOLD_VOLTAGE = 200;//150

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

	private float lastturn = 0;
	boolean grayFlag = false;

/** テスト用 */
	public void mani(){
		init();
		LCD.drawString("connect", 0, 0);
		calibration();
		LTthreshold = LTnormal = (blackColor+whiteColor)/2;
		LTgray = (grayColor+whiteColor)/2;
		ready();
		starttime = System.nanoTime();
		solveStep();
		for(int i = 0;i < 100;++i){
			Hardware.motorPortL.controlMotor(0, 1);
			Hardware.motorPortR.controlMotor(0, 1);
			Hardware.motorPortT.controlMotor(0, 1);
		}
		logExc();
		Sound.beep();
		sender.connect();
		sender.send();
	}


/** 階段からガレージまで */
	public void solveStep(){
		starttime = System.nanoTime();
		LTthreshold = LTnormal = (blackColor+whiteColor)/2;
		LTgray = (grayColor+whiteColor)/2;
		parallelToStep();
		climbStep();
		parallelToStep2();
		climbStep2();
		goCenter();
		down();
		toGarage();
		//以下ログ用 消しても問題ない
		logExc();
		Sound.beep();
		sender.connect();
		sender.send();
	}

/** ゴール地点から階段に近づく */
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
						checkDistanceInit();
						checkTimeInit();
						speed = 0;
						turn = 0;
						isFirst = false;
					}
					if(checkTime(1000)){speed = 5;turn = -10;}
					runByBalance();
					if(checkTime(1500)){
						++i;
						isFirst = true;
					}
					break;
				case 1://LTで前に一定距離進む
					if(isFirst){
						speed = 25;
						checkBumpInit();
						isFirst = false;
					}
					checkGray();
					checkBump();
					setTurnByLT();
					runByBalance();
					////setLog(i);
					if(checkDistance(200)){
						++i;
						isFirst = true;
					}
					break;
				default:
					isEnd = true;
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
			Delay.msDelay(1);
		}
	}

/** 高速走行して段を登り、減速する */
	public void climbStep(){
		tail = TAIL_ANGLE_BALANCE;
		isEnd = false;
		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask() {
			int i = 0;
			int counter = 0;
			boolean isFirst = true;
			@Override
			public void run() {
				switch(i){
				case 0://低速で進んで向きを合わせる
					if(isFirst){
						speed = 25;
						isFirst = false;
					}
					checkGray();
					setTurnByLT();
					runByBalance();
					//setLog(i);
					if(checkBump()){
						++i;
						isFirst = true;
					}
					break;
				case 1:
					if(isFirst){
						speed= 0;
						turn = 0;
						LTthreshold = LTnormal;
						GYRO_OFFSET = -4;
						checkDistanceInit();
						checkTimeInit();
						keepRotationInit();
						isFirst = false;
					}
					runByBalance();
					//setLog(i);
					if(!checkDistance(-60)){
						++i;
						isFirst = true;
					}
					break;
				case 2:
					if(isFirst){
						GYRO_OFFSET = -1;
						checkTimeInit();
						isFirst = false;
					}
					//if(GYRO_OFFSET<8&&++counter>10){counter = 0;++GYRO_OFFSET;}
					if(checkTime(100))GYRO_OFFSET = 10;
					keepRotation();
					runByBalance();
					//setLog(i);
					if(checkDistance(40)){
						++i;
						isFirst = true;
					}
					break;
				case 3:
					if(isFirst){
						GYRO_OFFSET = -5;
						checkTimeInit();
						isFirst = false;
					}
					keepRotation();
					runByBalance();
					//setLog(i);
					if(checkTime(500)){//400?
						++i;
						isFirst = true;
					}
					break;
				case 4:
					if(isFirst){
						GYRO_OFFSET = -1;
						isFirst = false;
					}
					keepRotation();
					runByBalance();
					//setLog(i);
					if(!checkDistance(170)){
						++i;
						isFirst = true;
					}
					break;
				case 5:
					if(isFirst){
						GYRO_OFFSET = 0;
						checkTimeInit();
						isFirst = false;
					}
					keepRotation();
					runByBalance();
					//setLog(i);
					if(checkTime(300)){
						++i;
						isFirst = true;
					}
					break;
				default:
					//Sound.beep();
					//sender.send();
					isEnd = true;
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
			Delay.msDelay(1);
		}
	}

/** 1段目上で板と平行にする */
	public void parallelToStep2(){
		isEnd = false;
		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask() {
			int i = 0;
			boolean straightFlag = false;
			boolean isFirst = true;
			@Override
			public void run() {
				switch(i){
				case 0:
					if(isFirst){
						turn = 0;
						speed = 10;
						checkTimeInit();
						isFirst = false;
					}
					if(checkTime(500)){
						speed = 20;turn = 20;
					}
					runByBalance();
					if(checkTime(800)){
						++i;
						isFirst = true;
					}
					break;
				case 1://減速しながら進んで向きを合わせる
					if(isFirst){
						speed = 25;
						checkBumpInit();
						isFirst = false;
					}
					setTurnByLT();
					checkBump();
					runByBalance();
					if(checkDistance(250)){
						++i;
						isFirst = true;
					}
					break;
				default:
					isEnd = true;
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
			Delay.msDelay(1);
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
				case 0://減速しながら進んで向きを合わせる
					if(isFirst){
						speed = 25;
						isFirst = false;
					}
					setTurnByLT();
					runByBalance();
					if(checkBump()){
						++i;
						isFirst = true;
					}
					break;
				case 1:
					if(isFirst){
						speed= 0;
						turn = 0;
						LTthreshold = LTnormal;
						GYRO_OFFSET = -4;
						checkDistanceInit();
						checkTimeInit();
						keepRotationInit();
						isFirst = false;
					}
					runByBalance();
					if(!checkDistance(-60)){
						++i;
						isFirst = true;
					}
					break;
				case 2:
					if(isFirst){
						GYRO_OFFSET = -1;
						checkTimeInit();
						isFirst = false;
					}
					if(checkTime(100))GYRO_OFFSET = 10;
					keepRotation();
					runByBalance();
					if(checkDistance(40)){
						++i;
						isFirst = true;
					}
					break;
				case 3:
					if(isFirst){
						GYRO_OFFSET = -5;
						checkTimeInit();
						isFirst = false;
					}
					keepRotation();
					runByBalance();
					if(checkTime(500)){//400?
						++i;
						isFirst = true;
					}
					break;
				case 4:
					if(isFirst){
						GYRO_OFFSET = -1;
						isFirst = false;
					}
					keepRotation();
					runByBalance();
					if(!checkDistance(170)){
						++i;
						isFirst = true;
					}
					break;
				case 5:
					if(isFirst){
						GYRO_OFFSET = 0;
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
				default:
					isEnd = true;
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
			Delay.msDelay(1);
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
						turn = 0;
						speed = 10;
						checkTimeInit();
						isFirst = false;
					}
					if(checkTime(500)){
						speed = 15;
						turn = 10;
					}
					runByBalance();
					if(checkTime(800)){
						++i;
						isFirst = true;
					}
					break;
				case 1://減速しながら進んで向きを合わせる
					if(isFirst){
						speed = 15;
						isFirst = false;
					}
					setTurnByLT();
					runByBalance();
					if(checkDistance(200)){
						++i;
						isFirst = true;
					}
					break;
				case 2:// 止まる
					if(isFirst){
						speed = 10;
						checkTimeInit();
						isFirst = false;
					}
					//speed = checkDistanceDifference(250) * 0.5f;
					setTurnByLT();
					runByBalance();
					if(checkDistance(220)){
						++i;
						isFirst = true;
					}
					break;
				default:
					//sender.send();
					isEnd = true;
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
			Delay.msDelay(1);
		}
	}

/** 一回転する  */
	public void turn(final int angle){
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
						speed =0;
						turn = 0;
						checkTimeInit();
						isFirst = false;
					}
					runByBalance();
					if(checkTime(1000)){
						++i;
						isFirst = true;
					}
					break;
				case 1:
					if(isFirst){
						speed = 0;
						if(angle > 0)turn = 30;
						else turn = -30;
						checkTurnInit();
						isFirst = false;
					}
					runByBalance();
					if(checkTurn(angle)){
						++i;
						isFirst = true;
					}
					break;
				case 2:
					if(isFirst){
						speed = 0;
						turn = 0;
						checkTimeInit();
						isFirst = false;
					}
					runByBalance();
					if(checkTime(1000)){
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
			Delay.msDelay(1);
		}

	}

/** 降りる */
	public void down(){
		thetaL = Hardware.motorPortL.getTachoCount();
		thetaR = Hardware.motorPortR.getTachoCount();
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

				float pwm = (float)(0 - Hardware.motorPortT.getTachoCount()) * P_GAIN;

				if(pwm > PWM_ABS_MAX){
					pwm = PWM_ABS_MAX;
				}
				else if(pwm < -PWM_ABS_MAX){
					pwm = -PWM_ABS_MAX;
				}
				Hardware.motorPortT.controlMotor((int)pwm, 1);

				gyroNow = -Hardware.getGyroValue();
				thetaL = Hardware.motorPortL.getTachoCount();
				thetaR = Hardware.motorPortR.getTachoCount();
				theta_ave = (thetaL + thetaR)/2;
				battery = Battery.getVoltageMilliVolt();
				float bright = Hardware.getBrightness();
				if(count < 50){
					forward = 10.0F;
					turn = (bright - threshold)*100;///0.0F;
					count++;
				}else{
					if(Math.abs(gyroNow)<GYRO_LIMIT && downflag == true/* && theta_ave < TACHO_LIMIT */){
						forward = 50.0F;
						if(count++<100)turn = -3;//(Hardware.getBrightness() - threshold)*300;
						else turn = 0;
						if(turn >100){
							turn = 100;
						}
						else if(turn < -100){
							turn = -100;
						}
					}else if(Math.abs(gyroNow)>GYRO_LIMIT && downflag == true/* && theta_ave > TACHO_LIMIT */){
						forward = 40.0F;
						turn = 0.0F;
						downflag = false;
						down_theta = theta_ave;
					}else if(downflag == false &&(theta_ave - down_theta < 200 || stopcount < 350)) {
						forward = 40.0F;
						turn = 0.0F;
						stopcount++;
					}else if((theta_ave - down_theta > 200 && stopcount > 350) && bright <0.25F){
						forward = 0;
						turn = 0;
						stopcount = 2000;
					}else{
						forward = 20.0F;
						threshold = 0.2F;
						turn = (bright - threshold)*100;
						if(turn >50){
							turn = 50;
						}
						else if(turn < -50){
							turn = -50;
						}
						stopcount++;
						//logSender.disconnect();
					}
				}
				Balancer.control(forward,-turn,gyroNow,GYRO_OFFSET,thetaL,thetaR,battery);
				Hardware.motorPortL.controlMotor(Balancer.getPwmL(), 1);
				Hardware.motorPortR.controlMotor(Balancer.getPwmR(), 1);
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
			//Delay.msDelay(100);
			LCD.clear();
			Delay.msDelay(20);
			if(driveTask.getStopCount() >= 2000){
					driveTask.cancel();
					break;
			}
		}
	}

/** 降りたところから灰色検知してガレージに入る */
	public void toGarage(){
		// 灰色からガレージ終端まで447mm
		isEnd = false;
		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask() {
			int i = 0;
			boolean isFirst = true;
			int counter = 0;
			@Override
			public void run() {
				if(i!=0&&Hardware.touchSensorIsPressed()){i = 4;isFirst = true;}
				switch(i){
				case 0://最初に静止する
					if(isFirst){
						LCD.drawString("stop", 0, 0);
						checkDistanceInit();
						checkTimeInit();
						speed = 0;
						turn = 0;
						isFirst = false;
					}
					if(checkTime(1000)){speed = -15;turn = 0;}
					runByBalance();
					if(!checkDistance(-20)){
						++i;
						isFirst = true;
					}
					break;
				case 1://LTで前に進む
					if(isFirst){
						LCD.drawString("run", 0, 1);
						speed = 10;
						turn = 0;
						checkTimeInit();
						//LTmultiplier = 70;
						LTgray = grayColor*0.90f+whiteColor*0.10f;//灰色寄りにする
						blackCount = 0;
						grayCount = 0;
						isFirst = false;
					}// 輝度と距離を両方使う
					if(checkTime(500))setTurnByLT();
					if(checkTime(1500)&&checkDistance(50)){
						blackCount = 0;
						speed = 20;
						if(checkDistance(120)){
							LTthreshold = LTnormal = blackColor*0.30f+grayColor*0.70f;//黒寄りにして誤検知を防ぐ
							checkGrayGarage();
						}
						limitTurn();
					}else{
						if(2 < blackCount)counter++;
						if(counter<2&&turn<5)turn = 5;
					}
					//LTmultiplier = 100;
					runByBalance();
					if(150 < grayCount){grayFlag = true;}//灰色検知の基準値 150-200くらい?
					if(grayFlag&&grayCount < 20){
						++i;
						isFirst = true;
						break;
					}
					/*if(checkDistance(6400)){
						++i;
						isFirst = true;
					}*/
					break;
				case 2://ガレージ前まで進む
					if(isFirst){
						LCD.drawString("3point", 0, 2);
						speed = 25;
						turn = 0;
						LTthreshold = LTnormal = blackColor*0.50f+whiteColor*0.50f;
						LTmultiplier = 100;
						checkTimeInit();
						checkDistanceInit();
						isFirst = false;
					}
					setTurnByLT();
					runByBalance();
					if(checkDistance(360)){
						++i;
						isFirst = true;
					}
					break;
				case 3://三点倒立
					if(isFirst){
						LCD.drawString("3point", 0, 2);
						speed = 40;
						turn = 0;
						LTthreshold = LTnormal;
						tail = 75;
						checkTimeInit();
						checkDistanceInit();
						isFirst = false;
					}
					if(checkTime(200))speed = 0;
					if(tail < 85 && checkTime(800+(tail-75)*400))tail++;
					runByTail();
					if(checkTime(12000)){
						++i;
						isFirst = true;
					}
					break;
				case 4://停止
					if(isFirst){
						speed = 0;
						turn = 0;
						tail = 85;
						checkTimeInit();
						isFirst = false;
					}
					runByTail();
					if(checkTime(3000)){
						++i;
						isFirst = true;
					}
					break;
				default:
					isEnd = true;
					break;
				}
				lastturn = turn;
				setLog(0);
			}
		};
		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);
		while(true){
			if(isEnd){
				driveTask.cancel();
				isEnd = false;
				break;
			}
			Delay.msDelay(1);
		}
	}


	/** センサ・モータの初期化 */
	public void init(){
		Hardware.gyro.reset();
		Hardware.motorPortL.setPWMMode(BasicMotorPort.PWM_BRAKE);
		Hardware.motorPortR.setPWMMode(BasicMotorPort.PWM_BRAKE);
		Hardware.motorPortT.setPWMMode(BasicMotorPort.PWM_BRAKE);

		for (int i=0; i < 1500; i++) {
			Hardware.motorPortL.controlMotor(0, 0);
			Hardware.motorPortR.controlMotor(0, 0);
			Hardware.motorPortT.controlMotor(0, 0);
			Hardware.getBrightness();
			Hardware.getGyroValue();
			Battery.getVoltageMilliVolt();
			Balancer.control(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8000);
			System.nanoTime();
			//sender.addLog("name", 100, System.nanoTime()/100000.0f);
			//sender.addLog("name", 100.0f, System.nanoTime()/100000.0f);
		}
		Delay.msDelay(1000);       // In another thread wait until the wonder time that HotSpot is complete.

		Hardware.motorPortL.controlMotor(0, 0);
		Hardware.motorPortR.controlMotor(0, 0);
		Hardware.motorPortT.controlMotor(0, 0);
		Hardware.motorPortL.resetTachoCount();   // left motor encoder reset
		Hardware.motorPortR.resetTachoCount();   // right motor encoder reset
		Hardware.motorPortT.resetTachoCount();   // tail motor encoder reset
		Balancer.init();            // inverted pendulum control initialization
		//sender.clear();
	}

	private final void tailControl(int angle) {
		float pwm = (float)(angle - Hardware.motorPortT.getTachoCount()) * P_GAIN; // proportional control
		// PWM output saturation processing
		if (pwm > PWM_ABS_MAX) {
			pwm = PWM_ABS_MAX;
		} else if (pwm < -PWM_ABS_MAX) {
			pwm = -PWM_ABS_MAX;
		}
		Hardware.motorPortT.controlMotor((int)pwm, 1);
	}

	/** カラーセンサの値からturn値を決定する ******************************************************************************************************/
	private void setTurnByLT(){
		bright = Hardware.getBrightness();
		turn = (LTthreshold - bright) * LTmultiplier/* * Math.signum(Balancer.getPwmL()+Balancer.getPwmR())*/;
		if(bright<(blackColor + grayColor)/2f){
			++blackCount;
			turn *= (1+blackCount/80f);
		}else blackCount /= 1.1;
	}

	/** 倒立振子を用いてモータを動作させる */
	private void runByBalance(){
		int encodeL = Hardware.motorPortL.getTachoCount();
		int encodeR = Hardware.motorPortR.getTachoCount();
		Balancer.control(speed, turn, -Hardware.getGyroValue(), GYRO_OFFSET, encodeL, encodeR, Battery.getVoltageMilliVolt());
		Hardware.motorPortL.controlMotor(Balancer.getPwmL(), 1);
		Hardware.motorPortR.controlMotor(Balancer.getPwmR(), 1);
		tailControl(tail);
	}

	/** 倒立振子を用いずにモータを動作させる */
	void runByTail(){
		Hardware.motorPortL.controlMotor((int) clamp(speed+turn,100,-100), 1);
		Hardware.motorPortR.controlMotor((int) clamp(speed-turn,100,-100), 1);
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
		if(calcDistance() - distanceDefault > target)return true;
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
		distanceDefault = calcDistance() + START_DISTANCE;
	}

	private boolean checkTime(int target){
		if((System.nanoTime() / 1000000 - timeDefault) > target)return true;
		return false;
	}

	private void checkTimeInit(){
		timeDefault = (int) (System.nanoTime() / 1000000);
	}

	private static boolean checkBump(){
		int voltage = Battery.getVoltageMilliVolt();
		avarageVoltage = (avarageVoltage + (double)voltage*0.004)/1.004;
		//if(voltage+200<avarageVoltage)return true;
		//else return false;


		for(int i = TACHO_BUMP-1;i > 0;--i){
			tachoBumpL[i] = tachoBumpL[i-1];
			tachoBumpR[i] = tachoBumpR[i-1];
		}

		tachoBumpL[0] = Hardware.motorPortL.getTachoCount();
		tachoBumpR[0] = Hardware.motorPortR.getTachoCount();

		if(tachoBumpL[0] - tachoBumpL[TACHO_BUMP-1] < THRESHOLD_TACHO){
			isBumpL = true;
		}else if(tachoBumpL[0] - tachoBumpL[TACHO_BUMP-1] > -THRESHOLD_TACHO){
			isBumpL = false;
		}

		if(tachoBumpR[0] - tachoBumpR[TACHO_BUMP-1] < THRESHOLD_TACHO){
			isBumpR = true;
		}else if(tachoBumpR[0] - tachoBumpR[TACHO_BUMP-1] > -THRESHOLD_TACHO){
			isBumpR = false;
		}

		if(isBumpR&&isBumpL&&(voltage+THRESHOLD_VOLTAGE<avarageVoltage))return true;
		else return false;

	}

	private static void checkBumpInit(){
		avarageVoltage = Battery.getVoltageMilliVolt();
		int BumpL = Hardware.motorPortL.getTachoCount();
		int BumpR = Hardware.motorPortR.getTachoCount();
		for(int i = 0;i < TACHO_BUMP;++i){
			tachoBumpL[i] = BumpL;
			tachoBumpR[i] = BumpR;
		}
		isBumpL = false;
		isBumpR = false;

	}

	private boolean checkTurn(int target){
		if(0 < target)if(target < (calcRotation() - turnDefault))return true;
		else if((calcRotation() - turnDefault) < target)return true;
		return false;
	}

	private void checkTurnInit(){
		turnDefault = calcRotation();
	}

	private boolean checkStraight(){
		turnValue[turnPoint] = turn;
		if(++turnPoint>=THRESHOLD_TURN) turnPoint = 0;
		for(int i = 0;i < VALUE_STRAIGHT; ++i){
			if((turnValue[i] < -THRESHOLD_TURN)||(THRESHOLD_TURN < turnValue[i])){
				return false;
			}
		}
		return true;
	}

	private void checkStraightInit(){
		for(int i = 0;i < VALUE_STRAIGHT; ++i){
			turnValue[i] = 999;
		}
	}

	private void checkGray(){
		if((grayColor-0.05<bright) && (bright<grayColor+0.05)){
			++grayCount;
			if(grayCount<50)LTthreshold = LTgray;
		}
		else{
			grayCount = 0;
			if(bright < blackColor+0.05f)LTthreshold = LTnormal;
		}
	}

	private void checkGrayGarage(){
		if((grayColor*0.80f+blackColor*0.20<bright) && (bright<grayColor*0.40 + whiteColor*0.60)){//bright<grayColor*1.00 + whiteColor*0.20
			if(40<grayCount){
				//speed = 15;
				LTmultiplier = 200;//130
				if(80<grayCount){
					LTthreshold = LTgray;
					if(120<grayCount)LTmultiplier = 100;
					if((grayColor*0.90f+blackColor*0.10<bright) && (bright<grayColor*0.60+whiteColor*0.40))++grayCount;
				}
				else{
					LTthreshold = LTgray + whiteColor * 0.20f;
					++grayCount;
				}
				//if(50<grayCount)
			}
			else{
				if((grayColor*0.90f+blackColor*0.10<bright) && (bright<grayColor*0.80+whiteColor*0.20))++grayCount;
			}
		}
		else if(bright <= grayColor*0.80f+blackColor*0.20){
			grayCount /= 3;//grayCount /= 1.3;
			LTthreshold = LTnormal;
			LTmultiplier = 100;//70
		}else{
			if(grayFlag&&0<grayCount) // !grayFlag&&
			//grayCount/=1.08;
			grayCount -= 2;
			LTthreshold = LTnormal;
			LTmultiplier = 100;
		}
	}

	private void keepRotation(){
		turn = (keepRotationDefault - (float)calcRotation());
	}

	private void keepRotationInit(){
		keepRotationDefault = (float)calcRotation();
	}

	private void limitTurn(){
		if(turn < lastturn-5){turn = lastturn - 5;}
		else if(lastturn+5 < turn){turn = lastturn + 5;}
		lastturn = turn;
	}

/** 走行距離の計算 単位はミリメートル*/
	private double calcDistance(){
		return Math.PI * WHEEL_DIAMETER * (Hardware.motorPortL.getTachoCount() + Hardware.motorPortR.getTachoCount()) / 2 / 360 * 1000;//Math.PI * WHEEL_DIAMETERが2πr
	}

/** 旋回角度の計算 */
	private double calcRotation(){
		return (Hardware.motorPortL.getTachoCount() - Hardware.motorPortR.getTachoCount()) * 0.245;
	}

	private double clamp(double value, double max, double min){
		return Math.max(min, Math.min(value,max));
	}

/** ボタン押すと起動  */
	public void ready(){
		boolean touchPressed = false;
		//sender.setLogSize(1000);
		//Sound.beep();
		//sender.connect();
		for (;;) {
			tailControl(TAIL_ANGLE_STAND_UP); //complete stop for angle control
			if (Hardware.touchSensorIsPressed()) {
				touchPressed = true;          // touch sensor is pressed
			} else {
				if (touchPressed) break;      // touch sensor I was released after being pressed
			}
			Delay.msDelay(20);
		}
		Hardware.motorPortL.resetTachoCount();   // left motor encoder reset
		Hardware.motorPortR.resetTachoCount();   // right motor encoder reset
		starttime = System.nanoTime();
		Balancer.init();            // inverted pendulum control initialization
	}

	private void calibration(){
		LCD.clear();
		LCD.drawString("Detect BLACK", 0, 0);
		blackColor = getBright();

		LCD.drawString("Detect WHITE", 0, 0);
		whiteColor = getBright();

		LCD.drawString("Detect GRAY ", 0, 0);
		grayColor = getBright();
		LCD.clear();
	}

	private float getBright(){
		BrightMeasure bright = new BrightMeasure();
		boolean flag = false;
		Sound.beep();
		while(true){
			if(Hardware.touchSensorIsPressed() == true){
				flag = true;
			}else{
				if(flag == true){
					break;
				}
			}
			Delay.msDelay(100);
		}
		return bright.measureBrightness();
	}

	private int lastEnc = 0;

	private float lastbright[] = new float[2500];
	private float lastgray[] = new float[2500];
	private int nowbrightnumber = 0;
	private void setLog(int i){
		if(2499<nowbrightnumber)return;
		if(++waitint == 5){
			lastbright[nowbrightnumber] = bright;
			lastgray[nowbrightnumber] = grayCount;
			++nowbrightnumber;
			waitint = 0;
		}
	}
	private void logExc(){
		for(int i = 0;i < nowbrightnumber;++i){
			sender.addLog("B", lastbright[i], i/50f);
			//sender.addLog("LB",lastbright[i]-lastbright[i-1], i/50f);
			sender.addLog("G", lastgray[i], i/50f);
		}
	}//*/
}
