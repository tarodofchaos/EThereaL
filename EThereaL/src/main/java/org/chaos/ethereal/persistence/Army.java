package org.chaos.ethereal.persistence;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="ethereal_armies")
public class Army {

	private Integer id;
	private Integer battleId;
	private String originalArmyName;
	private String resultingArmyName;
	private List<Hero> heroes;
	private List<Monster> monsters;
	
	@DynamoDBHashKey(attributeName="id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@DynamoDBAttribute(attributeName="battle_id")
	public Integer getBattleId() {
		return battleId;
	}
	public void setBattleId(Integer battleId) {
		this.battleId = battleId;
	}
	
	@DynamoDBIgnore
	public List<Hero> getHeroes() {
		return heroes;
	}
	public void setHeroes(List<Hero> heroes) {
		this.heroes = heroes;
	}
	
	@DynamoDBIgnore
	public List<Monster> getMonsters() {
		return monsters;
	}
	public void setMonsters(List<Monster> monsters) {
		this.monsters = monsters;
	}
	
	@DynamoDBAttribute(attributeName="original_army_name")
	public String getOriginalArmyName() {
		return originalArmyName;
	}
	public void setOriginalArmyName(String originalArmyName) {
		this.originalArmyName = originalArmyName;
	}
	
	@DynamoDBAttribute(attributeName="resulting_army_name")
	public String getResultingArmyName() {
		return resultingArmyName;
	}
	public void setResultingArmyName(String resultingArmyName) {
		this.resultingArmyName = resultingArmyName;
	}
	
		
	
}
