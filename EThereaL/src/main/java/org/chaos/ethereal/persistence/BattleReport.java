package org.chaos.ethereal.persistence;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="ethereal_battle_report")
public class BattleReport {

	private Integer id;
	private List<Hero> heroCasualties;
	private Integer monsterCasualties;
	private String mostKillingHero;
	private String mostKillingMonster;
	private String battleTime;
	private String hardestBlowHero;
	private Integer hardestBlowHeroNo;
	private String hardestBlowMonster;
	private Integer hardestBlowMonsterNo;
	private String phase;
	private String biggestHorde;
	private String winner;
	private Integer startingHeroes;
	private Integer startingMonsters;
	
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
	public Integer getMonsterCasualties() {
		return monsterCasualties;
	}
	public void setMonsterCasualties(Integer monsterCasualties) {
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
	public Integer getHardestBlowHeroNo() {
		return hardestBlowHeroNo;
	}
	public void setHardestBlowHeroNo(Integer hardestBlowHeroNo) {
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
	public Integer getHardestBlowMonsterNo() {
		return hardestBlowMonsterNo;
	}
	public void setHardestBlowMonsterNo(Integer hardestBlowMonsterNo) {
		this.hardestBlowMonsterNo = hardestBlowMonsterNo;
	}
	
	@DynamoDBAttribute(attributeName="winner")
	public String getWinner() {
		return winner;
	}
	public void setWinner(String winner) {
		this.winner = winner;
	}
	
	@DynamoDBAttribute(attributeName="starting_heroes")
	public Integer getStartingHeroes() {
		return startingHeroes;
	}
	public void setStartingHeroes(Integer startingHeroes) {
		this.startingHeroes = startingHeroes;
	}
	
	@DynamoDBAttribute(attributeName="starting_monsters")
	public Integer getStartingMonsters() {
		return startingMonsters;
	}
	public void setStartingMonsters(Integer startingMonsters) {
		this.startingMonsters = startingMonsters;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Battle Status:").append(System.getProperty("line.separator")).append(System.getProperty("line.separator"))
			.append(getStartingHeroes()).append(" heroes vs ").append(getStartingMonsters()).append(" monsters").append(System.getProperty("line.separator"))
			.append("Winner: ").append(getWinner()).append(System.getProperty("line.separator"))
			.append("Hero casualties: ").append(getHeroCasualties().size()).append(System.getProperty("line.separator"))
			.append("Monster casualties: ").append(getMonsterCasualties()).append(System.getProperty("line.separator"))
			.append("Hardest hero hit: ").append(getHardestBlowHero()).append(" with ").append(getHardestBlowHeroNo()).append(" damage").append(System.getProperty("line.separator"))
			.append("Hardest monster hit: ").append(getHardestBlowMonster()).append(" with ").append(getHardestBlowMonsterNo()).append(" damage").append(System.getProperty("line.separator"))
			.append("Biggest monster horde: ").append(getBiggestHorde());
		
		return sb.toString();
	}
}
