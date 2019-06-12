package geneticAlg;

import java.util.Random;

/**
 * Created by igorf on 02.03.2016.
 */
public class codeHelper {

	Random rnd = new Random();
	public final String[] validIDs = {"1B","0D","11","14"};

	public String getRandomID(){
		return validIDs[rnd.nextInt(validIDs.length)];
	}

	public String getRandomCode(){
		StringBuilder sb = new StringBuilder(108);
		for (int i = 0; i < 54; i++) {
			sb.append(getRandomID());
		}
		return sb.toString();
	}
	
	public String mutateGene(String code){
		StringBuilder sb = new StringBuilder();
		int pos = rnd.nextInt(53)*2;

		for (int i = 0; i < 108; i++) {
			if(i == pos){
				sb.append(getRandomID());
				i++;
			}else{
				sb.append(code.charAt(i));
			}
		}
		return sb.toString();
	}

	public String onePointCrossover(String code1, String code2){
		int pos = rnd.nextInt(53)*2;
		StringBuilder sb = new StringBuilder(108);
		for (int i = 0; i < 108; i++) {
			if(i >= pos){
				sb.append(code2.charAt(i));
			}else{
				sb.append(code1.charAt(i));
			}
		}
		return sb.toString();
	}
}
