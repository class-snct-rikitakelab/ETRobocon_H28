package hardware;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.port.TachoMotorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

public class Hardware {

    // モータ制御用オブジェクト
    // EV3LargeRegulatedMotor では PWM 制御ができないので、TachoMotorPort を利用する
    public static TachoMotorPort motorPortL = MotorPort.C.open(TachoMotorPort.class); // 左モータ
    public static TachoMotorPort motorPortR = MotorPort.B.open(TachoMotorPort.class); // 右モータ
    public static TachoMotorPort motorPortT = MotorPort.A.open(TachoMotorPort.class); // 尻尾モータ

    //タッチセンサ
    public static EV3TouchSensor touch = new EV3TouchSensor(SensorPort.S1);
    public static SensorMode touchMode = touch.getTouchMode();

    //超音波センサ
    public static EV3UltrasonicSensor sonar = new EV3UltrasonicSensor(SensorPort.S2);
    public static SampleProvider distanceMode = sonar.getDistanceMode(); // 距離検出モード

    //カラーセンサ
    public static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S3);
    public static  SensorMode redMode = colorSensor.getRedMode();     // 輝度検出モード

    // ジャイロセンサ
    public static EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S4);
    public static SampleProvider rate = gyro.getRateMode();

}
