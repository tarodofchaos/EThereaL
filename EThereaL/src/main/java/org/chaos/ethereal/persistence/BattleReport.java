package org.chaos.ethereal.persistence;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="ethereal_battle_report")
public class BattleReport {

	private Integer id;
	private List<Hero> heroCasualties;
	private String monsterCasualties;
	private String mostKillingHero;
	private String mostKillingMonster;
	private String battleTime;
	private String hardestBlowHero;
	private String hardestBlowHeroNo;
	private String hardestBlowMonster;
	private String hardestBlowMonsterNo;
	private String phase;
	private String biggestHorde;
	
	@DynamoDBHashKey(attributeName="id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@DynamoDBAttribute(attributeName="hero_casualties")
	public List<Hero> getHeroCasualties() {
		return heroCasualties;
	}
	public void setHeroCasualties(List<Hero> heroCasualties) {
		this.heroCasualties = heroCasualties;
	}
	
	@DynamoDBAttribute(attributeName="monster_casualties")
	public String getMonsterCasualties() {
		return monsterCasualties;
	}
	public void setMonsterCasualties(String monsterCasualties) {
		this.monsterCasualties = monsterCasualties;
	}
	
	@DynamoDBAttribute(attributeName="most_killing_hero")
	public String getMostKillingHero() {
		return mostKillingHero;
	}
	public void setMostKillingHero(String mostKillingHero) {
		this.mostKillingHero = mostKillingHero;
	}
	
	@DynamoDBAttribute(attributeName="most_killing_monster")
	public String getMostKillingMonster() {
		return mostKillingMonster;
	}
	public void setMostKillingMonster(String mostKillingMonster) {
		this.mostKillingMonster = mostKillingMonster;
	}
	
	@DynamoDBAttribute(attributeName="battle_time")
	public String getBattleTime() {
		return battleTime;
	}
	public void setBattleTime(String battleTime) {
		this.battleTime = battleTime;
	}
	
	@DynamoDBAttribute(attributeName="hardest_blow_hero_no")
	public String getHardestBlowHeroNo() {
		return hardestBlowHeroNo;
	}
	public void setHardestBlowHeroNo(String hardestBlowHeroNo) {
		this.hardestBlowHeroNo = hardestBlowHeroNo;
	}
	
	@DynamoDBAttribute(attributeName="phase")
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	
	@DynamoDBAttribute(attributeName="hardest_blow_hero")
	public String getHardestBlowHero() {
		return hardestBlowHero;
	}
	public void setHardestBlowHero(String hardestBlowHero) {
		this.hardestBlowHero = hardestBlowHero;
	}
	
	@DynamoDBAttribute(attributeName="biggest_horde")
	public String getBiggestHorde() {
		return biggestHorde;
	}
	public void setBiggestHorde(String biggestHorde) {
		this.biggestHorde = biggestHorde;
	}
	
	@DynamoDBAttribute(attributeName="hardest_blow_monster")
	public String getHardestBlowMonster() {
		return hardestBlowMonster;
	}
	public void setHardestBlowMonster(String hardestBlowMonster) {
		this.hardestBlowMonster = hardestBlowMonster;
	}
	
	@DynamoDBAttribute(attributeName="hardest_blow_monster_no")
	public String getHardestBlowMonsterNo() {
		return hardestBlowMonsterNo;
	}
	public void setHardestBlowMonsterNo(String hardestBlowMonsterNo) {
		this.hardestBlowMonsterNo = hardestBlowMonsterNo;
	}
}
