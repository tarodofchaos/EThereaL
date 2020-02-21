package org.chaos.ethereal.helper;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;

public class UtilHelper {
	
	public static Integer getRandomNumberInRange(Integer min, Integer max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	public static Set<Integer> getUniqueRandomNumberInRange(Integer totalArmySize, Integer maxArmySize) {
		Set<Integer> rngList = new HashSet<>();
		while (rngList.size()<maxArmySize) {
			rngList.add(getRandomNumberInRange(0,totalArmySize));
		}
		
		return rngList; 
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
	
	public static InputStream downloadObject(String bucket, String path, String fileName) {
		String key = path + "/" + fileName;

		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
		S3Object object = s3Client.getObject(new GetObjectRequest(bucket, key));

		return object.getObjectContent();
	}
	
	public static InputStream downloadObject(String bucket, String key) {
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
		S3Object object = s3Client.getObject(new GetObjectRequest(bucket, key));

		return object.getObjectContent();
	}
	
}
