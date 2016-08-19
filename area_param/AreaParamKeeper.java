package area_param;

public class AreaParamKeeper {

	private float P = 0.0F;
	private float I = 0.0F;
	private float D = 0.0F;
	private float FORWARD = 0.0F;
	private float START = 0.0F;
	private float END = 0.0F;

	AreaParamKeeper(float start, float end,float p, float i, float d, float forward){
		this.START = start;
		this.END = end;
		this.P = p;
		this.I = i;
		this.D = d;
		this.FORWARD = forward;

	}

	public boolean checkPos(float distance){
		if(distance >= START && distance < END){
			return true;
		}else{
			return false;
		}
	}

	public float getP(){
		return P;
	}

	public float getI(){
		return I;
	}

	public float getD(){
		return D;
	}

	public float getForward(){
		return FORWARD;
	}

}
