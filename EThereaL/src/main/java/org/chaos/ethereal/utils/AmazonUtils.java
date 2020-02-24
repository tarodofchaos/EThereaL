package org.chaos.ethereal.utils;

import java.io.InputStream;
import java.util.Arrays;

import org.chaos.ethereal.persistence.BattleReport;

import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class AmazonUtils {
	
	public static void sendMessageToSnsTopic(String arn, String message, String format, String subject) {		
 		AmazonSNS sns = AmazonSNSClientBuilder.standard().withCredentials(new SystemPropertiesCredentialsProvider()).build();    	
 		PublishRequest request = new PublishRequest(arn, message, subject);	        
 		request.setMessageStructure(format);
 		sns.publish(request);									
	}
	
	public static void sendMessageToSqsQueue(String url, String message) {
		AmazonSQS sqs = (AmazonSQS) ((AmazonSQSClientBuilder) AmazonSQSClientBuilder.standard()
				.withCredentials(new SystemPropertiesCredentialsProvider())).build();
		SendMessageRequest sendMessageRequest = new SendMessageRequest(url, message);
		sendMessageRequest.setMessageGroupId(Long.valueOf(System.currentTimeMillis()).toString());
		sendMessageRequest.setMessageDeduplicationId(Long.valueOf(System.currentTimeMillis()).toString());
		sqs.sendMessage(sendMessageRequest);
	}
	
	public static ReceiveMessageResult retrieveMessageFromQueue(String url, String type) {
		AmazonSQS sqs = (AmazonSQS) ((AmazonSQSClientBuilder) AmazonSQSClientBuilder.standard()
				.withCredentials(new SystemPropertiesCredentialsProvider())).build();
		ReceiveMessageRequest receive = new ReceiveMessageRequest();
		receive.setQueueUrl(url);
		receive.setMaxNumberOfMessages(1);
		return sqs.receiveMessage(receive);
	}

	public static int retrieveTotalMessageInQueue(String url, String type) {
		AmazonSQS sqs = (AmazonSQS) ((AmazonSQSClientBuilder) AmazonSQSClientBuilder.standard()
				.withCredentials(new SystemPropertiesCredentialsProvider())).build();
		GetQueueAttributesRequest attributes = new GetQueueAttributesRequest();
		attributes.setQueueUrl(url);
		attributes.setAttributeNames(Arrays.asList("ApproximateNumberOfMessages"));
		GetQueueAttributesResult result = sqs.getQueueAttributes(attributes);
		if (result.getAttributes().get("ApproximateNumberOfMessages") == null) {
			return 0;
		} else {
			return Integer.valueOf((String) result.getAttributes().get("ApproximateNumberOfMessages"));
		}
	}

	public static void deleteMessageFromQueue(String url, String type, Message message) {
		AmazonSQS sqs = (AmazonSQS) ((AmazonSQSClientBuilder) AmazonSQSClientBuilder.standard()
				.withCredentials(new SystemPropertiesCredentialsProvider())).build();
		DeleteMessageRequest deleteRequest = new DeleteMessageRequest();
		deleteRequest.setQueueUrl(url);
		deleteRequest.setReceiptHandle(message.getReceiptHandle());
		sqs.deleteMessage(deleteRequest);
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

	public static String getTableName(Class<? extends BattleReport> clazz) {
		return clazz.getDeclaredAnnotation(DynamoDBTable.class).tableName().toString();
	}
	
}
