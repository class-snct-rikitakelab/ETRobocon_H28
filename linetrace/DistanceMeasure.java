package linetrace;

public class DistanceMeasure {

	public float measureDistance_Degree(){

		float rotate = hardware.Hardware.motorPortL.getTachoCount() + hardware.Hardware.motorPortR.getTachoCount();

		rotate = rotate / 2.0F;

		return rotate;
	}

	public float measureDistance_Meter(){

		float rotate = hardware.Hardware.motorPortL.getTachoCount() + hardware.Hardware.motorPortR.getTachoCount();

		rotate = rotate / 2.0F;

		return (rotate / 360.0F) * 0.262F;

	}

	public float measureDistance_CMeter(){

		float rotate = hardware.Hardware.motorPortL.getTachoCount() + hardware.Hardware.motorPortR.getTachoCount();

		rotate = rotate / 2.0F;

		return (rotate / 360.0F) * 26.2F;

	}

}
