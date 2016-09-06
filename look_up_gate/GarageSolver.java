package look_up_gate;

import hardware.Hardware;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class GarageSolver {

	int distanceToGarage = 40;			//ガレージに進入するために前進する距離cm単位
	int TAIL_ANGLE = 90;					//三点倒立するためのしっぽの角度
	private int Motor_Power = 60;	//モータの基礎的なPWM値
	private static float Distance;		//走行終了時の位置
	private static int BREAKING_DISTANCE = 23; //止まる際の制動距離

	private static float WHEEL_LENGH = 26.2F;	//車輪の周の長さ

	private static float PARAMR = 2.5F;	//右タイヤ比例制御用のパラメータ
	private static float PARAML = 2.5F;	//左タイヤ比例制御用のパラメータ

	private int PowerL = Motor_Power;
	private int PowerR = Motor_Power;

    private static int   PWM_ABS_MAX          = 60;
    private static float P_GAIN               = 2.5F;

    private static float TARGET = 0.4F;
    private static float BLACK = 0.0F;
    private static float WHITE = 0.0F;
    private static float KP = 50.0F;


	public GarageSolver(int distanceToGarage, int power, float black, float white, float target){
		this.distanceToGarage = distanceToGarage - BREAKING_DISTANCE;
		this.Motor_Power = power;

		this.BLACK = black;
		this.WHITE = white;
		this.TARGET = target;
	}

	public void setBlack(float black){
		this.BLACK = black;
	}

	public void setWhite(float white){
		this.WHITE = white;
	}

	public void SolveGarage(){

		Sound.beep();

		BeforeTakeInto();
		Sound.beep();
		TakeInto();
		Sound.beep();
		KeepSec();

	}


	public void BeforeTakeInto(){		//進入待ち状態にはいるためのメソッド 三点倒立で1秒間静止

		for(int i = 0; i < 150 ; i++){
			tailControl(this.TAIL_ANGLE);
			Delay.msDelay(20);
		}
	}

	public void TakeInto(){										//distanceToGarageで定めた距離だけ前進
		Distance = getDistance() + distanceToGarage;

		float start = getDistance();

		do{

			float bright = getBright(Hardware.getBrightness());

			float diff = TARGET - bright;

			float turn = diff * KP;

			int PowerL = Motor_Power + (int)turn;
			int PowerR = Motor_Power - (int)turn;

			Hardware.motorPortL.controlMotor(PowerL, 1); // 左モータPWM出力セット
			Hardware.motorPortR.controlMotor(PowerR, 1); // 右モータPWM出力セット

        	tailControl(this.TAIL_ANGLE);
		}while(getDistance() < Distance);
	}

	public void KeepSec(){	//経過待ち状態の時のメソッド 完全停止状態で3秒間維持

		int currentPower = Motor_Power;

		//float start = getDistance(body);

		int time = 0;

		while(true){
			currentPower = (int)(currentPower * 0.99);
			Hardware.motorPortR.controlMotor(currentPower, 1);
			Hardware.motorPortL.controlMotor(currentPower, 1);

			tailControl(this.TAIL_ANGLE);

			Delay.msDelay(20);

			time += 1;

			if(time == 1000) break;
		}

		//float end = getDistance(body);

		Hardware.motorPortL.controlMotor(0, 0); // 左モータPWM出力セット
		Hardware.motorPortR.controlMotor(0, 0); // 右モータPWM出力セット

		time = 0;

		while(true){
			tailControl(this.TAIL_ANGLE);
			Delay.msDelay(20);
			time += 20;
			if(time > 10000) break;
       	}

		//LCD.drawString("seidokyori:"+(end-start), 0, 4);

		//Button.waitForAnyEvent();
	}

	private float getBright(float bright){

		return (bright - BLACK)/(WHITE - BLACK);
	}

	private float getDistance(){
		int angleL = Hardware.motorPortL.getTachoCount();
		int angleR = Hardware.motorPortR.getTachoCount();

		float averageAngle = (angleL + angleR) / 2.0F;

		return averageAngle / 360.0F * WHEEL_LENGH;

	}

	private static final void tailControl(int angle) {
        float pwm = (float)(angle - Hardware.motorPortT.getTachoCount()) * P_GAIN; // 比例制御
        // PWM出力飽和処理
        if (pwm > PWM_ABS_MAX) {
            pwm = PWM_ABS_MAX;
        } else if (pwm < -PWM_ABS_MAX) {
            pwm = -PWM_ABS_MAX;
        }
        Hardware.motorPortT.controlMotor((int)pwm, 1);
    }

}