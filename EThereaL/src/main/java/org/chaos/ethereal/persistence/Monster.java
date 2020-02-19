package org.chaos.ethereal.persistence;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="ethereal_monsters")
public class Monster {

	private Integer id;
	private String name;
	private String level;
	private String hitpoints;
	private String armor;
	private String mainAttack;
	private String specialAttack;
	
	@DynamoDBHashKey(attributeName="id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@DynamoDBAttribute(attributeName="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@DynamoDBAttribute(attributeName="level")
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
	@DynamoDBAttribute(attributeName="hitpoints")
	public String getHitpoints() {
		return hitpoints;
	}
	public void setHitpoints(String hitpoints) {
		this.hitpoints = hitpoints;
	}
	
	@DynamoDBAttribute(attributeName="armor")
	public String getArmor() {
		return armor;
	}
	public void setArmor(String armor) {
		this.armor = armor;
	}
	
	@DynamoDBAttribute(attributeName="main_attack")
	public String getMainAttack() {
		return mainAttack;
	}
	public void setMainAttack(String mainAttack) {
		this.mainAttack = mainAttack;
	}
	
	@DynamoDBAttribute(attributeName="special_attack")
	public String getSpecialAttack() {
		return specialAttack;
	}
	public void setSpecialAttack(String specialAttack) {
		this.specialAttack = specialAttack;
	}
	
	
}
