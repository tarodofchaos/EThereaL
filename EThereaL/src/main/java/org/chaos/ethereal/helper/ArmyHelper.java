package org.chaos.ethereal.helper;

import java.util.ArrayList;
import java.util.List;

import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.Hero;
import org.chaos.ethereal.persistence.Monster;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

public class ArmyHelper {

	AmazonDynamoDB client;
	DynamoDBMapper mapper;
	Integer heroArmySize;
	Integer monsterArmySize;
	
	private void initClient() {
		client = AmazonDynamoDBClientBuilder.standard().withRegion("eu-west-1").build();
		mapper = new DynamoDBMapper(client);
	}
	
	public Army createArmy(Integer monstersSize, Integer heroesSize) {
		initClient();
		Army army = new Army();
		List<Hero> heroes;
		List<Monster> dbMonsters;
		List<Monster> armyMonsters = new ArrayList<>();
		Monster currentMonster;
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		
		heroes = mapper.scan(Hero.class, scanExpression);
		dbMonsters = mapper.scan(Monster.class, scanExpression);
		heroArmySize = heroesSize;
		monsterArmySize = monstersSize;
		
		heroes.stream().forEach(h->{
			h.setDamage(computeHeroStats(h.getMainStat()+h.getSecondaryStat()));
			h.setMana(computeHeroStats(h.getMagic()*2));
			h.setHitpoints(computeHeroStats(h.getMainStat()*10));
		});
		army.setHeroes(heroes);
		for (int i = 0; i < monstersSize; i++) {
			currentMonster = dbMonsters.get(UtilHelper.getRandomNumberInRange(0, dbMonsters.size()-1));
			currentMonster.setComputedHP(computeMonsterHP(currentMonster.getHitpoints()));
			armyMonsters.add(currentMonster);
		}
		army.setMonsters(armyMonsters);
		
		return army;
	}
	
	private Integer computeMonsterHP(String hp) {
		return UtilHelper.rollDie(hp);
	}
	
	private Integer computeHeroStats(Integer stat) {
		return Math.toIntExact(Math.round(stat*(monsterArmySize/heroArmySize)*0.2));
	}
}
