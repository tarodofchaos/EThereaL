package org.chaos.ethereal.helper;

import java.util.ArrayList;
import java.util.List;

import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.Hero;
import org.chaos.ethereal.persistence.Monster;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

public class ArmyHelper {

	AmazonDynamoDB client;
	DynamoDBMapper mapper;
	
	private void initClient() {
		client = AmazonDynamoDBClientBuilder.standard().withRegion("eu-west-1").withCredentials(new EnvironmentVariableCredentialsProvider()).build();
		mapper = new DynamoDBMapper(client);
	}
	
	public Army createArmy(int size) {
		initClient();
		Army army = new Army();
		List<Hero> heroes;
		List<Monster> dbMonsters;
		List<Monster> armyMonsters = new ArrayList<>();
		Monster currentMonster;
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		
		heroes = mapper.scan(Hero.class, scanExpression);
		dbMonsters = mapper.scan(Monster.class, scanExpression);
		
		//FIXME set dynamic id or remove
		army.setId(3);
		army.setHeroes(heroes);
		for (int i = 0; i < size; i++) {
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
}
