package org.chaos.ethereal;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chaos.ethereal.helper.ArmyHelper;
import org.chaos.ethereal.helper.BattleHelper;
import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.Hero;
import org.chaos.ethereal.persistence.Monster;
import org.chaos.ethereal.utils.UtilHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.javafaker.Faker;
import com.google.gson.Gson;

public class BattleTest {
	
	String countries[];
	double probabilities[];
	AmazonDynamoDB client;
	DynamoDBMapper mapper;
	Faker faker;
	Map<String, String> userCountry;
	int monstersSize = 2000000;
	int heroesSize = 130;
	LambdaLogger logger = (new TestContext()).getLogger();
	
	@Before
	public void setUp() {
		client = AmazonDynamoDBClientBuilder.standard().withRegion("eu-west-1").withCredentials(new EnvironmentVariableCredentialsProvider()).build();
		mapper = new DynamoDBMapper(client);
		faker = new Faker();
		
	}

	@Test
	public void createArmy() {
		Army army = new Army();
		List<Hero> heroes = new ArrayList<>();
		List<Monster> monsters = new ArrayList<>();
		
		Hero hero = new Hero();
		Monster monster = new Monster();
		
		hero.setId(1);
		hero.setDexterity(12);
		hero.setGender("Male");
		hero.setMagic(11);
		hero.setName("Munster Killa");
		hero.setRace("Elf");
		hero.setStrength(14);
		hero.setClazz("Cleric");
		heroes.add(hero);
		
		hero = new Hero();
		hero.setId(2);
		hero.setDexterity(8);
		hero.setGender("Female");
		hero.setMagic(5);
		hero.setName("Killa Ger");
		hero.setRace("Human");
		hero.setStrength(5);
		hero.setClazz("Cleric");
		heroes.add(hero);
		
		monster.setId(1);
		monster.setArmor(12);
		monster.setHitpoints("122");
		monster.setLevel("2");
		monster.setMainAttack("1d8");
		monster.setName("Baddie");
		monster.setSpecialAttack("4d6");
		
		monsters.add(monster);
		
		monster = new Monster();
		monster.setId(2);
		monster.setArmor(8);
		monster.setHitpoints("12");
		monster.setLevel("1");
		monster.setMainAttack("1d4");
		monster.setName("Grunt");
		monster.setSpecialAttack("2d6");
		monsters.add(monster);
		
		army.setId(1);
		army.setHeroes(heroes);
		army.setMonsters(monsters);
		
		mapper.batchSave(army);
	}
	
	@Test
	public void createArmyFromDynamoDB() throws Exception {
		ArmyHelper armyHelper = new ArmyHelper(logger);
		Army army = armyHelper.createArmy(monstersSize, heroesSize);
				
		Gson gson = new Gson();    
	    String json = gson.toJson(army);
	    File file = new File("army_"+String.valueOf(System.currentTimeMillis()));
	    String fileName = file.getAbsolutePath().toString();
	    System.out.println(fileName);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(json.getBytes());
		fos.flush();
		fos.close();
	}
	
	@Test
	public void randomBattle() throws Exception {
		ArmyHelper armyHelper = new ArmyHelper(logger);
		BattleHelper battleHelper = new BattleHelper(logger);
		Army army = armyHelper.createArmy(monstersSize, heroesSize);
		List<String> phases = new ArrayList<>();
		phases.add("a");
		phases.add("a");
		phases.add("a");
		phases.add("d");
		phases.add("d");
		phases.add("a");
		battleHelper.resolveBattle(army, phases);
		
	}
	
	@Test
	public void rollDice() {
		String diceNumber = "6";
		String dieSize = "8";
		String modifier =  "4";
		for (int i = 0; i < 100000; i++) {
			Integer result = UtilHelper.rollDie(diceNumber+"d"+dieSize+"+"+modifier);
			Assert.assertTrue(result >= Integer.parseInt(diceNumber)+Integer.parseInt(modifier) && result <= (Integer.parseInt(diceNumber)*Integer.parseInt(dieSize))+Integer.parseInt(modifier));	
		}
	}
}
