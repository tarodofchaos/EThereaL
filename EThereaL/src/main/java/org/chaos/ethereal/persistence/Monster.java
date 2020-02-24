package org.chaos.ethereal.persistence;

import org.chaos.ethereal.persistence.annotations.Validate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="ethereal_monsters")
public class Monster {

	private Integer id;
	@Validate(dbname="name")
	private String name;
	@Validate(dbname="level")
	private String level;
	@Validate(dbname="hitpoints")
	private String hitpoints;
	private Integer computedHP;
	@Validate(dbname="armor")
	private String armor;
	@Validate(dbname="main_attack")
	private String mainAttack;
	@Validate(dbname="special_attack")
	private String specialAttack;
	private Integer armyId;
	
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
	
	public Integer getComputedHP() {
		return computedHP;
	}
	public void setComputedHP(Integer computedHP) {
		this.computedHP = computedHP;
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
	
	public Integer getArmyId() {
		return armyId;
	}
	public void setArmyId(Integer armyId) {
		this.armyId = armyId;
	}
	
	
}
