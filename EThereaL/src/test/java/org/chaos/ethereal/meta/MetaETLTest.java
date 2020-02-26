package org.chaos.ethereal.meta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.Hero;
import org.chaos.ethereal.persistence.Monster;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.github.javafaker.Faker;
import com.google.gson.Gson;

public class MetaETLTest {
	
	String countries[];
	double probabilities[];
	AmazonDynamoDB client;
	DynamoDBMapper mapper;
	Faker faker;
	Map<String, String> userCountry;
	
	@Before
	public void setUp() {
		client = AmazonDynamoDBClientBuilder.standard().withRegion("eu-west-1").build();
		mapper = new DynamoDBMapper(client);
		faker = new Faker();
		
	}

	@Test
	public void loadMonsterDataIntoDynamoDB() {
		Monster monster;
		List<Monster> monsterList = new ArrayList<>();
		int i = 1;
		
		try {
			File f = new File("C:\\Users\\tarod-off\\git\\EThereaL\\EThereaL\\src\\test\\resources\\monsters.txt");
			FileReader fr=new FileReader(f);
			BufferedReader br=new BufferedReader(fr);
			String line;  
			while((line=br.readLine())!=null)  
			{  
				monster = transformLineToMonster(line);
				monster.setId(i++);

				monsterList.add(monster);
			}  
			fr.close();  
			System.out.println("");
			mapper.batchSave(monsterList);
			monsterList.clear();
		} catch (Exception e) {
			
		}
	}
	
	@Test
	public void loadHeroDataIntoDynamoDB() {
		Hero hero;
		List<Hero> heroList = new ArrayList<>();
		int i = 1;
		
		try {
			File f = new File("C:\\Users\\tarod-off\\git\\EThereaL\\EThereaL\\src\\test\\resources\\heroes.txt");
			FileReader fr=new FileReader(f);
			BufferedReader br=new BufferedReader(fr);
			String line;  
			while((line=br.readLine())!=null)  
			{  
				hero = transformLineToHero(line);
				hero.setId(i++);

				heroList.add(hero);
			}  
			fr.close();  
			System.out.println("");
			mapper.batchSave(heroList);
			heroList.clear();
		} catch (Exception e) {
			
		}
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
	public void createArmyFromDynamoDB() throws FileNotFoundException, IOException {
		Army army = new Army();
		List<Hero> heroes = new ArrayList<>();
		List<Monster> dbMonsters = new ArrayList<>();
		List<Monster> armyMonsters = new ArrayList<>();
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		
		heroes = mapper.scan(Hero.class, scanExpression);
		dbMonsters = mapper.scan(Monster.class, scanExpression);
		
		army.setId(3);
		army.setHeroes(heroes);
		for (int i = 0; i < 2000000; i++) {
			armyMonsters.add(dbMonsters.get(getRandomNumberInRange(0, dbMonsters.size()-1)));
		}
		army.setMonsters(armyMonsters);
		
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
	public void fixDataType() {
		List<Monster> dbMonsters = new ArrayList<>();
		List<Monster> fixedMonsters = new ArrayList<>();
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		
		dbMonsters = mapper.scan(Monster.class, scanExpression);
		for (Monster monster : dbMonsters) {
//			monster.setArmor(monster.getArmor1());
//			monster.setArmor1(null);
			fixedMonsters.add(monster);
		}
		mapper.batchSave(fixedMonsters);
		
	}
	
	private Hero transformLineToHero(String line) {
		Hero hero = new Hero();
		String[] heroLine = line.split(",");
		hero.setName(heroLine[0].trim());
		hero.setGender(heroLine[1].trim());
		hero.setRace(heroLine[2].trim());
		hero.setClazz(heroLine[3].trim());
		hero.setStrength(Integer.parseInt(heroLine[4].trim()));
		hero.setDexterity(Integer.parseInt(heroLine[5].trim()));
		hero.setMagic(Integer.parseInt(heroLine[6].trim()));
		
		return hero;
	}

	private Monster transformLineToMonster(String line) {
		Monster monster = new Monster();
		String[] monsterLine = line.split(",");
		monster.setName(monsterLine[0]);
		monster.setLevel(getLevel(monsterLine[1].substring(0, monsterLine[1].indexOf("d"))));
		monster.setHitpoints(monsterLine[1]);
		monster.setArmor(Integer.parseInt(monsterLine[2].substring(3)));
		monster.setMainAttack(monsterLine[3].substring(monsterLine[3].indexOf("(")+1, monsterLine[3].indexOf(")")));
		if (monsterLine[3].indexOf("(",monsterLine[3].indexOf(")")) > -1 ){
			monster.setSpecialAttack(monsterLine[3].substring(monsterLine[3].lastIndexOf("(")+1, monsterLine[3].lastIndexOf(")")));
		}
		return monster;
	}

	private String getLevel(String index) {
		return String.valueOf(getRandomNumberInRange(Integer.parseInt(index), Integer.parseInt(index)+1));
	}
	
	private int getRandomNumberInRange(int min, int max) {

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
}
