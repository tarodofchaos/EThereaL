package org.chaos.ethereal.persistence;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="ethereal_set_of_values")
public class SetOfValues {

	private String set;
	private List<String> values;
	
	@DynamoDBHashKey(attributeName="set")
	public String getSet() {
		return set;
	}
	public void setSet(String set) {
		this.set = set;
	}
	
	@DynamoDBAttribute(attributeName = "values")
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	
}
