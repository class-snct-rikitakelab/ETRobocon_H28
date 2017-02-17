package linetrace;

import drive_control.ForwardSelecter;
import drive_control.TurnCalc;
import hardware.BrightSensor;
import hardware.GyroSensor;
import hardware.TailMotor;
import hardware.WheelMotor;
import motor_control.WheelMotorCtrl;
import motor_control.tailCtrl;

public class LineTracer {

	private TurnCalc tc;
	private ForwardSelecter fs;
	private WheelMotorCtrl wmc;
	private tailCtrl tail;

	public LineTracer(WheelMotor wheel, TailMotor tail,BrightSensor bright, GyroSensor gyro){
		tc = new TurnCalc(bright,wheel);
		fs = new ForwardSelecter(wheel);
		wmc = new WheelMotorCtrl(wheel, gyro);
		this.tail = new tailCtrl(tail);

	}

	public void linetrace(){

		tail.tailTwo();

		float forward = fs.SelectForward();
		float turn = tc.calcTurn() * -1;

		wmc.setForward(forward);
		wmc.setTurn(turn);
		wmc.controlWheel();
	}

	public void renewParams(float distance){
		tc.updateParams(distance);
		fs.updateForward(distance);
	}

	public void back(){

		tail.tailThree();

		wmc.setForward(-20.0F);
		wmc.setTurn(0);
		wmc.controlWheel();
	}
}
