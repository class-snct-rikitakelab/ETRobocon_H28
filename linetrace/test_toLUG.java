/*
 *  EV3waySample.java (for leJOS EV3)
 *  Created on: 2015/05/09
 *  Author: INACHI Minoru
 *  Copyright (c) 2015 Embedded Technology Software Design Robot Contest
 */
package linetrace;

import java.io.IOException;
import java.net.ServerSocket;

import hardware.Hardware;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.BasicMotorPort;
import lejos.utility.Delay;

/**
 * 2輪倒立振子ライントレースロボットの leJOS EV3 用 Java サンプルプログラム。
 */
public class test_toLUG {
    private static final int   TAIL_ANGLE_STAND_UP  = 90;   // 完全停止時の角度[度]
    private static final float P_GAIN               = 4.5F; // 完全停止用モータ制御比例係数
    private static final int   PWM_ABS_MAX          = 60;   // 完全停止用モータ制御PWM絶対最大値
    private static final int   REMOTE_COMMAND_START = 71;   // 'g'
    private static ServerSocket    server = null;
    private static int             remoteCommand = 0;
    /**
     * メイン
     */
    public static void main(String[] args) {

        LCD.drawString("Please Wait...  ", 0, 4);
        Hardware.gyro.reset();
        Hardware.sonar.enable();
        Hardware.motorPortL.setPWMMode(BasicMotorPort.PWM_BRAKE);
        Hardware.motorPortR.setPWMMode(BasicMotorPort.PWM_BRAKE);
        Hardware.motorPortT.setPWMMode(BasicMotorPort.PWM_BRAKE);
        Hardware.motorPortL.resetTachoCount();
        Hardware.motorPortR.resetTachoCount();
        Hardware.motorPortT.resetTachoCount();

        // スタート待ち
        LCD.drawString("Touch to START", 0, 4);
        boolean touchPressed = false;
        for (;;) {
            tailControl(TAIL_ANGLE_STAND_UP); // 完全停止用角度に制御
            if (Hardware.touchSensorIsPressed()) {
                touchPressed = true;          // タッチセンサが押された
            } else {
                if (touchPressed) break;      // タッチセンサが押された後に放した
            }
            if (checkRemoteCommand(REMOTE_COMMAND_START)) break;  // PC で 'g' キーが押された
            Delay.msDelay(20);
        }


        LCD.drawString("Running...", 0, 4);
        toLUG3 goToLUG = new toLUG3();
        LookUpGateEvader LUG = new LookUpGateEvader();
        Sound.beep();


        goToLUG.gotoLUG(-40.0F);
        Sound.beep();
        LCD.drawString("stop", 0, 4);
        for(float i=40;i>10;i*=0.99){
            Hardware.motorPortL.controlMotor((int)i, 1); // 左モータPWM出力セット
            Hardware.motorPortR.controlMotor((int)i, 1); // 右モータPWM出力セット
        	tailControl(TAIL_ANGLE_STAND_UP - 5);
            Delay.msDelay(20);
        }

        LCD.clear();
        LCD.drawString("Down", 0, 4);	//走行体を倒す
        LUG.LUG_down();
        LCD.clear();
        LCD.drawString("go", 0, 4);//走行体を前進させる
        LUG.LUG_go();
        LCD.clear();
        LCD.drawString("up", 0, 4);//走行体を起こす
        LUG.LUG_up();

        LCD.drawString("garage", 1, 4);	//ガレージイン
        GarageSolver.SolveGarage();

        Hardware.motorPortL.close();
        Hardware.motorPortR.close();
        Hardware.motorPortT.close();
        Hardware.colorSensor.setFloodlight(false);
        Hardware.sonar.disable();
        if (server != null) {
            try { server.close(); } catch (IOException ex) {}
        }
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

    /*
     * リモートコマンドのチェック
     */
    private static final boolean checkRemoteCommand(int command) {
        if (remoteCommand > 0) {
            if (remoteCommand == command) {
                return true;
            }
        }
        return false;
    }
}
