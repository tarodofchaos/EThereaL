package org.chaos.ethereal.helper;

import java.util.ArrayList;
import java.util.List;

import org.chaos.ethereal.persistence.Specs;
import org.chaos.ethereal.persistence.utils.DynamoDBClientBuilder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class SpecsHelper {
	private AmazonDynamoDB client = DynamoDBClientBuilder.build();

	public List<Specs> retrieveAllFileSpecs(LambdaLogger logger) {
		Object result = new ArrayList<>();

		try {
			logger.log("Attempting to read the item...");
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			result = mapper.scan(Specs.class, new DynamoDBScanExpression());
		} catch (Exception e) {
			//TODO EXCEPTIONS!!!!
		}

		return (List) result;
	}
}
