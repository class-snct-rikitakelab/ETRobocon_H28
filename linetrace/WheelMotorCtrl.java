package linetrace;

import hardware.Hardware;
import lejos.hardware.Battery;

public class WheelMotorCtrl {

	private float forward;
	private float turn;

	WheelMotorCtrl(){
		forward = 0.0F;
		turn = 0.0F;
	}

	public void setTurn(float turn) {
		this.turn = turn;
	}

	public void setForward(float forward) {
		this.forward = forward;
	}

	public void controlWheel() {

		float[] gyrovalue = new float [Hardware.gyro.sampleSize()];
		Hardware.gyro.fetchSample(gyrovalue, 0);

		Balancer.Balancer.control(forward, turn, gyrovalue[0], 0, Hardware.motorPortL.getTachoCount(), Hardware.motorPortR.getTachoCount(), Battery.getVoltageMilliVolt());

		Hardware.motorPortL.controlMotor(Balancer.Balancer.getPwmL(), 1);
		Hardware.motorPortR.controlMotor(Balancer.Balancer.getPwmR(), 1);

	}

}
