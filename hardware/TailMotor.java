package hardware;

import lejos.hardware.port.BasicMotorPort;
import lejos.hardware.port.TachoMotorPort;

public class TailMotor {

	private TachoMotorPort tail;

	private float P_GAIN = 2.5F;
	private float PWM_ABS_MAX = 60.0F;

	public TailMotor(TachoMotorPort tail){
		this.tail =tail;
		tail.setPWMMode(BasicMotorPort.PWM_BRAKE);
	}

	public int getTailAngle(){
		return tail.getTachoCount();
	}

	public void controlMotor(int angle){
		float pwm = (float)(angle - tail.getTachoCount()) * P_GAIN; // 比例制御
        // PWM出力飽和処理
        if (pwm > PWM_ABS_MAX) {
            pwm = PWM_ABS_MAX;
        } else if (pwm < -PWM_ABS_MAX) {
            pwm = -PWM_ABS_MAX;
        }
        tail.controlMotor((int)pwm, 1);
	}

	public void controlMotor(int angle, float PGain){
		float pwm = (float)(angle - tail.getTachoCount()) * PGain; // 比例制御
        // PWM出力飽和処理
        if (pwm > PWM_ABS_MAX) {
            pwm = PWM_ABS_MAX;
        } else if (pwm < -PWM_ABS_MAX) {
            pwm = -PWM_ABS_MAX;
        }
        tail.controlMotor((int)pwm, 1);
	}
}
