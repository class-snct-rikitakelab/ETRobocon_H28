package linetrace;

import java.util.Timer;
import java.util.TimerTask;

import hardware.Hardware;
import lejos.hardware.Battery;
import lejos.hardware.port.BasicMotorPort;
import lejos.utility.Delay;

public class TestLineTrace {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		TurnCalc tc = new TurnCalc();
		WheelMotorCtrl wmc = new WheelMotorCtrl();

		Hardware.gyro.reset();
		Hardware.sonar.enable();
		Hardware.motorPortL.setPWMMode(BasicMotorPort.PWM_BRAKE);
		Hardware.motorPortR.setPWMMode(BasicMotorPort.PWM_BRAKE);
		Hardware.motorPortT.setPWMMode(BasicMotorPort.PWM_BRAKE);

		for(int i=0; i<1500 ; i++){
			Hardware.motorPortL.controlMotor(0, 0);
			Hardware.motorPortR.controlMotor(0, 0);

			float[] sample = new float[Hardware.redMode.sampleSize()];
			Hardware.redMode.fetchSample(sample, 0);

			sample = new float[Hardware.gyro.sampleSize()];
			Hardware.gyro.fetchSample(sample, 0);

			Battery.getVoltageMilliVolt();
			Balancer.Balancer.control(0.0F, 0.0F, 0.0F, 0.0F, 0.0F,0.0F, 8000);
		}

		Delay.msDelay(10000);

		Hardware.motorPortL.controlMotor(0, 0);
		Hardware.motorPortR.controlMotor(0, 0);
		Hardware.motorPortT.controlMotor(0, 0);
		Hardware.motorPortL.resetTachoCount();
		Hardware.motorPortR.resetTachoCount();
		Hardware.motorPortT.resetTachoCount();
		Balancer.Balancer.init();

		int count = 0;

		while(true){
			count ++;

			tailControl(90);

			Delay.msDelay(20);

			if(count == 500){
				break;
			}
		}

		Timer driveTimer = new Timer();
		TimerTask driveTask = new TimerTask(){

			public void run(){
				tailControl(90);

				float forward = 50.0F;
				float turn = tc.calcTurn();

				wmc.setForward(forward);
				wmc.setTurn(turn);

				wmc.controlWheel();
			}
		};

		driveTimer.scheduleAtFixedRate(driveTask, 0, 4);


	}

	private static final void tailControl(int angle) {
        float pwm = (float)(angle - Hardware.motorPortT.getTachoCount()) * 2.5F; // 比例制御
        // PWM出力飽和処理
        if (pwm > 60) {
            pwm = 60;
        } else if (pwm < -60) {
            pwm = -60;
        }
        Hardware.motorPortT.controlMotor((int)pwm, 1);
    }

}
