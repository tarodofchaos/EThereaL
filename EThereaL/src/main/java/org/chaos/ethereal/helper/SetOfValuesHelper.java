package org.chaos.ethereal.helper;

import java.util.ArrayList;
import java.util.List;

import org.chaos.ethereal.persistence.SetOfValues;
import org.chaos.ethereal.persistence.utils.DynamoDBClientBuilder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class SetOfValuesHelper {
	private AmazonDynamoDB client = DynamoDBClientBuilder.build();

	public List<SetOfValues> retrieveAllPossibleValues(LambdaLogger logger) {
		Object result = new ArrayList();
		List<SetOfValues> set = new ArrayList<>();

		try {
			logger.log("Attempting to read the item...");
			DynamoDBMapper mapper = new DynamoDBMapper(this.client);
			result = mapper.scan(SetOfValues.class, new DynamoDBScanExpression());
			((List)result).stream().forEach(s -> {
				set.add((SetOfValues)s);
			});
		} catch (Exception e) {
			//TODO EXCEPTIONS!!!!
		}

		return set;
	}
}
