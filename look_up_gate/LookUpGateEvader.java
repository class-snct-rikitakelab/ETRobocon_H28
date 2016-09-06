package look_up_gate;

import hardware.Hardware;
import lejos.utility.Delay;

//import Drive.EV3Body;

public class LookUpGateEvader {


    private static final int   PWM_ABS_MAX          = 60;   // 完全停止用モータ制御PWM絶対最大値
    private static final float P_GAIN               = 2.5F; // 完全停止用モータ制御比例係数
    private static float I_GAIN = 0.0F;						//走行体を起すときに使うI制御係数

    private static float TARGET = 0.5F;
    private static float BLACK = 0.0F;
    private static float WHITE = 0.0F;
    private static float KP = 80.0F;

    private float preDiff = 0.0F;
    private float curDiff = 0.0F;
    private float Integral = 0.0F;

	private static int TAIL_ANGLE_GATE_IN			=70;	//ゲート通過時の尻尾の角度
	private static int TAIL_ANGLE_NOT_GATE			=85;	//完全停止時の尻尾の角度
	private static float Advance_Mileage				=0.5F;	//前進距離
	private static int Backward_Mileage				=-100;	//後退距離
	private static int Motor_Power					=30;	//走行速度

	private static float PARAMR = 2.5F;	//右タイヤ比例制御用のパラメータ
	private static float PARAML = 2.5F;	//左タイヤ比例制御用のパラメータ

	private static float Distance;							//走行終了時の位置

    private static CheckDistance cDistance = new CheckDistance();

	private static int i;

	public void setBlack(float black){
		this.BLACK = black;
	}

	public void setWhite(float white){
		this.WHITE = white;
	}

	//走行体を倒す
	public void LUG_down(){

		for(int i = 0;i<200;i++){
			tailControl(TAIL_ANGLE_NOT_GATE);
			Delay.msDelay(20);
		}
		for(i = TAIL_ANGLE_NOT_GATE; i > TAIL_ANGLE_GATE_IN ; i--){
			tailControl(i);
			Delay.msDelay(100);
		}
	}

	//走行体を直す
	public void LUG_up(){

		I_GAIN = 0.0F;

		for(int i = 0; i < 600 ; i++){
			tailControl((int)(TAIL_ANGLE_GATE_IN + (TAIL_ANGLE_NOT_GATE-TAIL_ANGLE_GATE_IN)*((float)i/600.0F)) );
			Hardware.motorPortL.controlMotor(3, 1); // 左モータPWM出力セット
			Hardware.motorPortR.controlMotor(3, 1); // 右モータPWM出力セット
			Delay.msDelay(20);
		}

		I_GAIN = 0.0F;

		/*
		for(i = TAIL_ANGLE_GATE_IN; i < TAIL_ANGLE_NOT_GATE ; i++){
			tailControl(i);
			Hardware.motorPortL.controlMotor(3, 1); // 左モータPWM出力セット
			Hardware.motorPortR.controlMotor(3, 1); // 右モータPWM出力セット
			Delay.msDelay(100);
		}*/
	}

	private float getBright(float bright){

		return (bright - BLACK)/(WHITE - BLACK);
	}
	//一定距離進める
	public void LUG_go(){


		Distance = cDistance.getDistance()+Advance_Mileage;
		do{

			float bright = getBright(Hardware.getBrightness());

			float diff = TARGET - bright;

			float turn = diff * KP;

			int PowerL = Motor_Power + (int)turn;
			int PowerR = Motor_Power - (int)turn;

			Hardware.motorPortL.controlMotor(PowerL, 1); // 左モータPWM出力セット
			Hardware.motorPortR.controlMotor(PowerR, 1); // 右モータPWM出力セット

        	tailControl(TAIL_ANGLE_GATE_IN);
		}while(cDistance.getDistance() < Distance);
		Hardware.motorPortL.controlMotor(0, 0);
		Hardware.motorPortR.controlMotor(0, 0);
	}


	public void LUG_back(){
		Distance = cDistance.getDistance()+Backward_Mileage;
		do{

			float bright = Hardware.getBrightness();

			float diff = TARGET - bright;

			float turn = diff * KP;

			int PowerL = Motor_Power + (int)turn;
			int PowerR = Motor_Power - (int)turn;

			Hardware.motorPortL.controlMotor(PowerL, 1); // 左モータPWM出力セット
			Hardware.motorPortR.controlMotor(PowerR, 1); // 右モータPWM出力セット

        	tailControl(TAIL_ANGLE_GATE_IN);
		}while(cDistance.getDistance() > Distance);
		Hardware.motorPortL.controlMotor(0, 0);
		Hardware.motorPortR.controlMotor(0, 0);
	}

	//一定距離後退する
	public void LUG_back2(){
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

	private void tailControl(int angle) {
		float curDiff = (float)(angle - Hardware.motorPortT.getTachoCount());

		Integral += ((curDiff+preDiff)/2.0F) * 0.02F;
		preDiff = curDiff;

        float pwm = curDiff * P_GAIN + Integral*I_GAIN; // 比例制御
        // PWM出力飽和処理
        if (pwm > PWM_ABS_MAX) {
            pwm = PWM_ABS_MAX;
        } else if (pwm < -PWM_ABS_MAX) {
            pwm = -PWM_ABS_MAX;
        }
        Hardware.motorPortT.controlMotor((int)pwm, 1);
	    }

}
