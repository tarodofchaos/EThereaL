package org.chaos.ethereal.persistence;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="ethereal_armies")
public class Army {

	private Integer id;
	private List<Hero> heroes;
	private List<Monster> monsters;
	
	@DynamoDBHashKey(attributeName="id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@DynamoDBAttribute(attributeName="heroes")
	public List<Hero> getHeroes() {
		return heroes;
	}
	public void setHeroes(List<Hero> heroes) {
		this.heroes = heroes;
	}
	
	@DynamoDBAttribute(attributeName="monsters")
	public List<Monster> getMonsters() {
		return monsters;
	}
	public void setMonsters(List<Monster> monsters) {
		this.monsters = monsters;
	}
	
		
	
}
