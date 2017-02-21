package motor_control;

import hardware.GyroSensor;
import hardware.WheelMotor;
import lejos.hardware.Battery;

public class WheelMotorCtrl {

	WheelMotor wheel;
	GyroSensor gyro;
	private float forward;
	private float turn;

	public WheelMotorCtrl(WheelMotor wheel, GyroSensor gyro){
		forward = 0.0F;
		turn = 0.0F;
		this.wheel = wheel;
		this.gyro = gyro;
	}

	public void setTurn(float turn) {
		this.turn = turn;
	}

	public void setForward(float forward) {
		this.forward = forward;
	}

	public void controlWheel() {

		Balancer.Balancer.control(forward, turn, gyro.getGyroValue() * (-1), 0.0F, wheel.getTachoL(), wheel.getTachoR(), Battery.getVoltageMilliVolt());
		wheel.controlWheel(Balancer.Balancer.getPwmL(),Balancer.Balancer.getPwmR());

	}


}
