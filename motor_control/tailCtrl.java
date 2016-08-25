package motor_control;

import hardware.Hardware;

public class tailCtrl {

    private static final int   TAIL_ANGLE_STAND_UP  = 92;   // 完全停止時の角度[度]
    private static final int   TAIL_ANGLE_DRIVE     = 3;    // バランス走行時の角度[度]
    private static final float P_GAIN               = 2.5F; // 完全停止用モータ制御比例係数
    private static final int   PWM_ABS_MAX          = 60;   // 完全停止用モータ制御PWM絶対最大値

	public static final void tailTwo() {
        float pwm = (float)(TAIL_ANGLE_DRIVE - Hardware.motorPortT.getTachoCount()) * P_GAIN; // 比例制御
        // PWM出力飽和処理
        if (pwm > PWM_ABS_MAX) {
            pwm = PWM_ABS_MAX;
        } else if (pwm < -PWM_ABS_MAX) {
            pwm = -PWM_ABS_MAX;
        }
        Hardware.motorPortT.controlMotor((int)pwm, 1);
    }

	public static final void tailThree() {
        float pwm = (float)(TAIL_ANGLE_STAND_UP - Hardware.motorPortT.getTachoCount()) * P_GAIN; // 比例制御
        // PWM出力飽和処理
        if (pwm > PWM_ABS_MAX) {
            pwm = PWM_ABS_MAX;
        } else if (pwm < -PWM_ABS_MAX) {
            pwm = -PWM_ABS_MAX;
        }
        Hardware.motorPortT.controlMotor((int)pwm, 1);
    }

}
