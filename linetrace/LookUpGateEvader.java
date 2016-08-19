package linetrace;

import hardware.Hardware;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

//import Drive.EV3Body;

public class LookUpGateEvader {


    private static final int   PWM_ABS_MAX          = 60;   // 完全停止用モータ制御PWM絶対最大値
    private static final float P_GAIN               = 2.5F; // 完全停止用モータ制御比例係数

	private static int TAIL_ANGLE_GATE_IN			=67;	//ゲート通過時の尻尾の角度
	private static int TAIL_ANGLE_NOT_GATE			=90;	//完全停止時の尻尾の角度
	private static int Advance_Mileage				=1;	//前進距離
	private static int Backward_Mileage				=-100;	//後退距離
	private static int Motor_Power					=50;	//走行速度

	private static float PARAMR = 2.5F;	//右タイヤ比例制御用のパラメータ
	private static float PARAML = 2.5F;	//左タイヤ比例制御用のパラメータ

	private static float Distance;							//走行終了時の位置

    private static CheckDistance cDistance = new CheckDistance();

	private static int i;

	//走行体を倒す
	void LUG_down(){
		tailControl(TAIL_ANGLE_NOT_GATE);
		Delay.msDelay(100);
		for(i = TAIL_ANGLE_NOT_GATE; i > TAIL_ANGLE_GATE_IN ; i--){
			tailControl(i);
			Delay.msDelay(100);
		}
	}

	//走行体を直す
	void LUG_up(){
		for(i = TAIL_ANGLE_GATE_IN; i < TAIL_ANGLE_NOT_GATE ; i++){
			tailControl(i);
			Hardware.motorPortL.controlMotor(3, 1); // 左モータPWM出力セット
			Hardware.motorPortR.controlMotor(3, 1); // 右モータPWM出力セット
			Delay.msDelay(100);
		}
	}
	//一定距離進める
	void LUG_go(){
		Distance = cDistance.getDistance()+Advance_Mileage;
		do{
			int tachoL = Hardware.motorPortL.getTachoCount();
			int tachoR = Hardware.motorPortR.getTachoCount();
			int target = (tachoL + tachoR)/2;
			int PowerL = Motor_Power + (int)((target - tachoL) * PARAML);
			int PowerR = Motor_Power + (int)((target - tachoR) * PARAMR);
			Hardware.motorPortL.controlMotor(PowerL, 1); // 左モータPWM出力セット
        	Hardware.motorPortR.controlMotor(PowerR, 1); // 右モータPWM出力セット
        	LCD.drawChar((char)Distance, 1, 4);
        	tailControl(TAIL_ANGLE_GATE_IN);
		}while(cDistance.getDistance() < Distance);
		Hardware.motorPortL.controlMotor(0, 0);
		Hardware.motorPortR.controlMotor(0, 0);
	}

	//一定距離後退する
	void LUG_back(){
		Distance = cDistance.getDistance()+Backward_Mileage;
		do{
			int tachoL = Hardware.motorPortL.getTachoCount();
			int tachoR = Hardware.motorPortR.getTachoCount();
			int target = (tachoL + tachoR)/2;
			int PowerL = /*Motor_Power -20*/+ (int)((target - tachoL) * PARAML);
			int PowerR = /*Motor_Power -20*/+ (int)((target - tachoR) * PARAMR);
			Hardware.motorPortL.controlMotor(-PowerL, 1); // 左モータPWM出力セット
        	Hardware.motorPortR.controlMotor(-PowerR, 1); // 右モータPWM出力セット
        	tailControl(TAIL_ANGLE_GATE_IN);
		}while(cDistance.getDistance() > Distance);
		Hardware.motorPortL.controlMotor(0, 0);
		Hardware.motorPortR.controlMotor(0, 0);
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
