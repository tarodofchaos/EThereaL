package org.chaos.ethereal.persistence.utils;

import org.chaos.ethereal.utils.AppConstants;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

public class DynamoDBClientBuilder {
	public static AmazonDynamoDB build() {
		return build(AppConstants.DYNAMODB_DEFAULT_NODE, AppConstants.DYNAMODB_DEFAULT_REGION);
	}

	public static AmazonDynamoDB build(String node, String region) {
		return (( AmazonDynamoDBClientBuilder
				.standard().withCredentials(new DefaultAWSCredentialsProviderChain()))
						.withEndpointConfiguration(new EndpointConfiguration(node, region))).build();
	}

	/**
	 * For Sonar purposes
	 */
	private DynamoDBClientBuilder() {
		super();
	}
}
