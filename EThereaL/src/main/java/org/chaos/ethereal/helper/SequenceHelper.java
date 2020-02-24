package org.chaos.ethereal.helper;

import java.math.BigDecimal;

import org.chaos.ethereal.persistence.utils.DynamoDBClientBuilder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

public class SequenceHelper {
	static AmazonDynamoDB client = DynamoDBClientBuilder.build();

	public static Integer getNewSeq(String tableName) {
		DynamoDB dynamoDB = new DynamoDB(client);
		Table table = dynamoDB.getTable(AppConstants.TABLE_ETHEREAL_ID_GENERATOR);
		UpdateItemSpec updateItemSpec = (new UpdateItemSpec()).withPrimaryKey("table_name", tableName)
				.withUpdateExpression("set id = id + :val").withValueMap((new ValueMap()).withNumber(":val", 1))
				.withReturnValues(ReturnValue.UPDATED_OLD);

		try {
			UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
			Integer newSeq = ((BigDecimal)outcome.getItem().get("id")).intValue();

			return newSeq;
		} catch (Exception e) {
			//TODO EXCEPTIONS!!!
			e.printStackTrace();
			return null;
		}
	}
}
