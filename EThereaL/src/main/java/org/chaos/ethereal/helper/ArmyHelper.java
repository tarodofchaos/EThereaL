package org.chaos.ethereal.helper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.Hero;
import org.chaos.ethereal.persistence.Monster;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.google.gson.Gson;

public class ArmyHelper {

	AmazonDynamoDB client;
	DynamoDBMapper mapper;
	Integer heroArmySize;
	Integer monsterArmySize;
	
	private void initClient() {
		client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
		mapper = new DynamoDBMapper(client);
	}
	
	public Army createArmy(Integer monstersSize, Integer heroesSize) {
		initClient();
		Army army = new Army();
		List<Hero> dbheroes;
		List<Hero> armyHeroes = new ArrayList<>();
		List<Monster> dbMonsters;
		List<Monster> armyMonsters = new ArrayList<>();
		Set<Integer> rngHeroes;
		Monster currentMonster;
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		
		dbheroes = mapper.scan(Hero.class, scanExpression);
		dbMonsters = mapper.scan(Monster.class, scanExpression);
		heroArmySize = heroesSize;
		monsterArmySize = monstersSize;
		
		rngHeroes = UtilHelper.getUniqueRandomNumberInRange(dbheroes.size()-1, heroesSize);
		for (Integer hero : rngHeroes) {
			armyHeroes.add(dbheroes.get(hero));
		}
		
		armyHeroes.stream().forEach(h->{
			h.setDamage(computeHeroStats(h.getMainStat()+h.getSecondaryStat()));
			h.setMana(computeHeroStats(h.getMagic()*2));
			h.setHitpoints(computeHeroStats(h.getMainStat()*10));
		});
		army.setHeroes(armyHeroes);
		
		for (int i = 0; i < monstersSize; i++) {
			currentMonster = dbMonsters.get(UtilHelper.getRandomNumberInRange(0, dbMonsters.size()-1));
			currentMonster.setComputedHP(computeMonsterHP(currentMonster.getHitpoints()));
			armyMonsters.add(currentMonster);
		}
		army.setMonsters(armyMonsters);
		
		return army;
	}
	
	public Army createArmyFromFile(String fileName) {
		Army army;
		Gson gson = new Gson();
		InputStream is = UtilHelper.downloadObject(AppConstants.S3_BUCKET, AppConstants.S3_ARMY_PATH, fileName);
		Reader reader = new InputStreamReader(is);
		army = gson.fromJson(reader, Army.class);
		
		return army;
	}
	
	public Army createArmyFromIS(InputStream is) {
		Army army;
		Gson gson = new Gson();
		Reader reader = new InputStreamReader(is);
		army = gson.fromJson(reader, Army.class);
		
		return army;
	}
	
	private Integer computeMonsterHP(String hp) {
		return UtilHelper.rollDie(hp);
	}
	
	private Integer computeHeroStats(Integer stat) {
		return Math.toIntExact(Math.round(stat*(monsterArmySize/heroArmySize)*0.2));
	}
}
