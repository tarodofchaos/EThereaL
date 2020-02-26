package org.chaos.ethereal.helper;

import java.util.List;

import org.chaos.ethereal.persistence.Specs;
import org.chaos.ethereal.persistence.utils.DynamoDBClientBuilder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class SpecsHelper {
	private AmazonDynamoDB client = DynamoDBClientBuilder.build();

	public List<Specs> retrieveAllFileSpecs(LambdaLogger logger) throws Exception{
		List<Specs> result;

		logger.log("Attempting to read the item...");
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		result = mapper.scan(Specs.class, new DynamoDBScanExpression());

		return result;
	}
}
