package org.chaos.ethereal.persistence;

import org.chaos.ethereal.helper.AppConstants;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="ethereal_heroes")
public class Hero {

	private Integer id;
	private String name;
	private String race;
	private String gender;
	private String clazz;
	private Integer strength;
	private Integer dexterity;
	private Integer magic;
	private Integer hitpoints;
	private Integer damage;
	private Integer mana;
	private String mainStat;
	private String secondaryStat;
	
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
	
	@DynamoDBAttribute(attributeName="race")
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	
	@DynamoDBAttribute(attributeName="gender")
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	@DynamoDBAttribute(attributeName="class")
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	@DynamoDBAttribute(attributeName="str")
	public Integer getStrength() {
		return strength;
	}
	public void setStrength(Integer strength) {
		this.strength = strength;
	}
	
	@DynamoDBAttribute(attributeName="dex")
	public Integer getDexterity() {
		return dexterity;
	}
	public void setDexterity(Integer dexterity) {
		this.dexterity = dexterity;
	}
	
	@DynamoDBAttribute(attributeName="mag")
	public Integer getMagic() {
		return magic;
	}
	public void setMagic(Integer magic) {
		this.magic = magic;
	}
	public Integer getHitpoints() {
		return hitpoints;
	}
	public void setHitpoints(Integer hitpoints) {
		this.hitpoints = hitpoints;
	}
	public Integer getDamage() {
		return damage;
	}
	public void setDamage(Integer damage) {
		this.damage = damage;
	}
	public Integer getMana() {
		return mana;
	}
	public void setMana(Integer mana) {
		this.mana = mana;
	}
	
	public Integer getMainStat() {
		if (getRace().equals(AppConstants.HUMAN) || getRace().equals(AppConstants.DWARF)) {
			return getStrength();
		} else if (getRace().equals(AppConstants.HALFLING)) {
			return getDexterity();
		} else {
			return getMagic();
		}
	}
	
	public Integer getSecondaryStat() {
		if (getClazz().equals(AppConstants.FIGHTER)) {
			return getStrength();
		} else if (getClazz().equals(AppConstants.ROGUE)) {
			return getDexterity();
		} else {
			return getMagic();
		}
	}
	
}
