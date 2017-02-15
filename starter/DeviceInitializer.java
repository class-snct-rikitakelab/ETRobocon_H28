package starter;


import lejos.hardware.Sound;
import lejos.hardware.port.TachoMotorPort;
import lejos.hardware.sensor.EV3GyroSensor;

public class DeviceInitializer {

	private TachoMotorPort rightMotor;

	private TachoMotorPort leftMotor;

	private TachoMotorPort tailMotor;

	private EV3GyroSensor gyroSensor;

	public DeviceInitializer(TachoMotorPort right,TachoMotorPort left,TachoMotorPort tail, EV3GyroSensor gyro){
		this.rightMotor = right;
		this.leftMotor = left;
		this.tailMotor = tail;
		this.gyroSensor = gyro;
	}

	public void initTireMotor() {
		for(int i=0;i<100;i++){
			this.leftMotor.controlMotor(0, 0);
			this.rightMotor.controlMotor(0, 0);
		}

		this.leftMotor.resetTachoCount();
		this.rightMotor.resetTachoCount();

	}

	public void initTailMotor() {
		for(int i=0;i<100;i++){
			this.tailMotor.controlMotor(0, 0);
		}
		this.tailMotor.resetTachoCount();
	}

	public void initGyroSensor() {
		this.gyroSensor.reset();
		Sound.beep();

	}

}
