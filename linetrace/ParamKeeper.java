package linetrace;

public class ParamKeeper {

	static float P = 0.0F;
	static float I = 0.0F;
	static float D = 0.0F;

	static void setP(float p){
		P = p;
	}

	static void setI(float i){
		I = i;
	}

	static void setD(float d){
		D = d;
	}

	static float getP(){
		return P;
	}

	static float getI(){
		return I;
	}

	static float getD(){
		return D;
	}

}
