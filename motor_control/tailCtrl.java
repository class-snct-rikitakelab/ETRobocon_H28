package motor_control;

import hardware.TailMotor;

public class tailCtrl {

    private static final int   TAIL_ANGLE_STAND_UP  = 92;   // 完全停止時の角度[度]
    private static final int   TAIL_ANGLE_DRIVE     = 3;    // バランス走行時の角度[度]
    private static final int   TAIL_ANGLE_START     = 97;   // スタート時の前傾の目標角度
    private static final float S_P_GAIN             = 10.0F;// スタート時のモータ制御比例係数

    TailMotor tail;

    public tailCtrl(TailMotor tail){
    	this.tail = tail;
    }

	public void tailTwo() {
		tail.controlMotor(TAIL_ANGLE_DRIVE);
    }

	public void tailThree() {
		tail.controlMotor(TAIL_ANGLE_STAND_UP);
    }

	public void tailStart() {

		tail.controlMotor(TAIL_ANGLE_START,S_P_GAIN);
	}

}
