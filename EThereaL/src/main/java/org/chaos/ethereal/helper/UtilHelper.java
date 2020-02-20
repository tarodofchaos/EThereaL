package org.chaos.ethereal.helper;

import java.util.Random;

import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;

public class UtilHelper {
	
	public static int getRandomNumberInRange(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	public static Integer rollDie(String die) {
		Integer result = 0;
		Integer diceNumber;
		Integer dieSize;
		Integer modifier;
		Random r = new Random();
		diceNumber = Integer.valueOf(die.substring(0,die.indexOf("d")));
		if (die.contains("+")) {
			dieSize = Integer.valueOf(die.substring(die.indexOf("d")+1, die.indexOf("+")));
			modifier = Integer.valueOf(die.substring(die.indexOf("+")+1));
		} else {
			dieSize = Integer.valueOf(die.substring(die.indexOf("d")+1));
			modifier = 0;
		}
		
		for (int i = 0; i < diceNumber; i++) {
			result += (r.nextInt((dieSize - 1) + 1) + 1);
		}
		result += modifier;
		
		return result;
	}
	
	public static void sendMessageToSnsTopic(String arn, String message, String format, String subject) {		
 		AmazonSNS sns = AmazonSNSClientBuilder.standard().withCredentials(new SystemPropertiesCredentialsProvider()).build();    	
 		PublishRequest request = new PublishRequest(arn, message, subject);	        
 		request.setMessageStructure(format);
 		sns.publish(request);									
	}
	
}
