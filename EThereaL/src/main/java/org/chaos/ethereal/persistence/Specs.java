package org.chaos.ethereal.persistence;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="ethereal_specs")
public class Specs {

	private Integer id;
	private String fieldName;
	private Integer length;
	private String mandatory;
	private String recordType;
	private String setOfValues;
	private String type;
	
	@DynamoDBHashKey(attributeName="id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@DynamoDBAttribute(attributeName = "field_name")
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	@DynamoDBAttribute(attributeName = "length")
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	
	@DynamoDBAttribute(attributeName = "mandatory")
	public String getMandatory() {
		return mandatory;
	}
	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}
	
	@DynamoDBAttribute(attributeName = "record_type")
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	
	@DynamoDBAttribute(attributeName = "set_of_values")
	public String getSetOfValues() {
		return setOfValues;
	}
	public void setSetOfValues(String setOfValues) {
		this.setOfValues = setOfValues;
	}
	
	@DynamoDBAttribute(attributeName = "type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
