package look_up_gate;

import hardware.Hardware;


public class toLUG3 {
    private static final float LIGHT_WHITE          = 0.4F; // 白色のカラーセンサ輝度値
    private static final float LIGHT_BLACK          = 0.0F; // 黒色のカラーセンサ輝度値
    private static final float SONAR_ALERT_DISTANCE = 0.5F; // 超音波センサによる障害物検知距離[m]
    private static final int   TAIL_ANGLE_STAND_UP  = 90;   // 完全停止時の角度[度]
    private static final float P_GAIN               = 2.5F; // 完全停止用モータ制御比例係数
    private static final int   PWM_ABS_MAX          = 60;   // 完全停止用モータ制御PWM絶対最大値

    private static final float THRESHOLD = (LIGHT_WHITE+LIGHT_BLACK)/2.0F;	// ライントレースの閾値

    private static boolean         alert   = false;

	private static float diff = 0.0f;
	private static int PwmL;
	private static int PwmR;
	int   count = 0;
	int graycount = 0;

    public void gotoLUG(){

		float p;
		float KP = -40.0F;//KU  * 0.6f;	//比例定数

		while(true) {
			tailControl(TAIL_ANGLE_STAND_UP); // バランス走行用角度に制御

			if(++count > 20){
				alert = sonarAlert(); // 障害物検知
				count=0;
			}

			if (alert) {           // 障害物を検知したら停止
				break;
			}

			float forward = 30.0F;  // 前進命令

            /*
             * PID制御
             */
			diff = Hardware.getBrightness() - THRESHOLD;

			p = KP * diff;

			float turn  = p ;//+ d + i;

			if(turn > 50.0f)
				turn = 50.0f;
			else if(turn < -50.0f)
				turn = -50.0f;

            PwmL=(int) (forward + turn);
            PwmR=(int) (forward - turn);
            Hardware.motorPortL.controlMotor(PwmL, 1); // 左モータPWM出力セット
            Hardware.motorPortR.controlMotor(PwmR, 1); // 右モータPWM出力セット
        }
    }

    public void gotoLUG(float KP){

		float p;

		while(true) {
			tailControl(TAIL_ANGLE_STAND_UP); // バランス走行用角度に制御

			if(++count > 20){
				alert = sonarAlert(); // 障害物検知
				count=0;
			}

			if (alert) {           // 障害物を検知したら停止
				break;
			}

			float forward = 30.0F;  // 前進命令

            /*
             * PID制御
             */
			diff = Hardware.getBrightness() - THRESHOLD;

			p = KP * diff;

			float turn  = p ;//+ d + i;

			if(turn > 50.0f)
				turn = 50.0f;
			else if(turn < -50.0f)
				turn = -50.0f;

            PwmL=(int) (forward + turn);
            PwmR=(int) (forward - turn);
            Hardware.motorPortL.controlMotor(PwmL, 1); // 左モータPWM出力セット
            Hardware.motorPortR.controlMotor(PwmR, 1); // 右モータPWM出力セット
        }
    }

    /*
     * 超音波センサによる障害物検知
     * @return true(障害物あり)/false(障害物無し)
     */
    private static final boolean sonarAlert() {
        float distance = Hardware.getSonarDistance();
        if ((distance <= SONAR_ALERT_DISTANCE) && (distance >= 0)) {
            return true;  // 障害物を検知
        }
        return false;
    }

    /*
     * 走行体完全停止用モータの角度制御
     * @param angle モータ目標角度[度]
     */
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
