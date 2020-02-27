package org.chaos.ethereal.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.GetFunctionRequest;
import com.amazonaws.services.lambda.model.GetFunctionResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;

public class AmazonUtils {
	
	/**
	 * For Sonar purposes. Code smells
	 */
	private AmazonUtils() {
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

	public static String getTableName(Class<? extends Object> clazz) {
		return clazz.getDeclaredAnnotation(DynamoDBTable.class).tableName();
	}
	
	public static void moveObject(String bucket, String pathFrom, String pathTo, String sourceFileName, String destFileName) {
		String keyFrom = pathFrom + "/" + sourceFileName;
		String keyTo = pathTo + "/" + destFileName;
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
		s3Client.copyObject(bucket, keyFrom, bucket, keyTo);
		s3Client.deleteObject(bucket, keyFrom);
	}
	
	 public static int getTimeoutLambda(String function) {
	    	AWSLambda client = AWSLambdaClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
	    	GetFunctionRequest request = new GetFunctionRequest().withFunctionName(function);
	    	GetFunctionResult response = client.getFunction(request);
	    	return response.getConfiguration().getTimeout().intValue();
	    }
	
	public static void uploadMultipartObject(String bucket, String path, String fileName, InputStream fileContentInputStream,
			Integer fileSize) throws IOException {
		String fullPath = path + "/" + fileName;

		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();

		// Create a list of UploadPartResponse objects. You get one of these
		// for each part upload.
		List<PartETag> partETags = new ArrayList<>();

		// Step 1: Initialize.
		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucket, fullPath);
		InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);

		long partSize = 5242880; // Set part size to 5 MB.

		try {
			// Step 2: Upload parts.
			long filePosition = 0;
			for (int i = 1; filePosition < fileSize; i++) {
				// Last part can be less than 5 MB. Adjust part size.
				partSize = Math.min(partSize, (fileSize - filePosition));

				// Create request to upload a part.
				UploadPartRequest uploadRequest = new UploadPartRequest().withBucketName(bucket).withKey(fullPath)
						.withUploadId(initResponse.getUploadId()).withPartNumber(i)
						.withInputStream(fileContentInputStream).withPartSize(partSize);

				// Upload part and add response to our list.
				boolean retry;
				int tryIndex = 0;
				do {
					retry = false;
					try {
						partETags.add(s3Client.uploadPart(uploadRequest).getPartETag());

					} catch (Exception e) {
						retry = true;
						tryIndex++;
					}
				} while (retry && tryIndex < 11);

				filePosition += partSize;
			}

			// Step 3: Complete.
			CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucket, fullPath,
					initResponse.getUploadId(), partETags);

			s3Client.completeMultipartUpload(compRequest);
		} catch (Exception e) {
			s3Client.abortMultipartUpload(
					new AbortMultipartUploadRequest(bucket, fullPath, initResponse.getUploadId()));
		} finally {
			fileContentInputStream.close();
		}
	}
	
}
