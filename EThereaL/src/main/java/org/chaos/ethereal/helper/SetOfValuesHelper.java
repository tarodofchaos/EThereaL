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

	public List<SetOfValues> retrieveAllPossibleValues(LambdaLogger logger) throws Exception{
		List<SetOfValues> set;
		List<SetOfValues> result = new ArrayList<>();
		logger.log("Attempting to read the item...");
		DynamoDBMapper mapper = new DynamoDBMapper(this.client);
		set = mapper.scan(SetOfValues.class, new DynamoDBScanExpression());
		set.stream().forEach(s -> result.add((SetOfValues)s));

		return result;
	}
}
