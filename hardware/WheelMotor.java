package hardware;

import lejos.hardware.port.BasicMotorPort;
import lejos.hardware.port.TachoMotorPort;

public class WheelMotor {

	private TachoMotorPort left;
	private TachoMotorPort right;

	public WheelMotor(TachoMotorPort left,TachoMotorPort right){
		this.left = left;
		this.right = right;

		left.setPWMMode(BasicMotorPort.PWM_BRAKE);
		right.setPWMMode(BasicMotorPort.PWM_BRAKE);
	}

	public void controlWheel(int leftPWM, int rightPWM){
		left.controlMotor(leftPWM, 1);
		right.controlMotor(rightPWM, 1);
	}

}
