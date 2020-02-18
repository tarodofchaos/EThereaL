package org.chaos.ethereal.meta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.github.javafaker.Faker;

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
		monster.setArmor(monsterLine[2].substring(3));
		monster.setMainAttack(monsterLine[3].substring(monsterLine[3].indexOf("(")+1, monsterLine[3].indexOf(")")));
		if (monsterLine[3].indexOf("(",monsterLine[3].indexOf(")")) > -1 ){
			monster.setSpecialAttack(monsterLine[3].substring(monsterLine[3].lastIndexOf("(")+1, monsterLine[3].lastIndexOf(")")));
		}
		return monster;
	}

	private String getLevel(String index) {
		return String.valueOf(getRandomNumberInRange(Integer.parseInt(index), Integer.parseInt(index)+1));
	}

	@Test
	public void generateSampleFileFromDynamoDB() {
//		List<BandPerGenre> bandList;
//		try {
//			DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
//			
//			bandList = mapper.scan(BandPerGenre.class, scanExpression);
//			String band;
//			String country;
//			String username;
//			StringBuilder sb = new StringBuilder();
//			
//			FileWriter writer = new FileWriter("D:\\16. Dev\\Zartis\\spikyfy\\output\\load.in", true);
//			
//			for (int i = 0; i < 2000000; i++) {
//				if (i % 10000 == 0) {
//					System.out.println(new Date().toString()+" "+i+" records created");
//				}
//				band = bandList.get(getRandomNumberInRange(0, 2299)).getBandName();
//				username = getRandomUsername(getRandomNumberInRange(1, 10));
//				country = userCountry.get(username);
//				sb.append(username).append(AppConstants.SEPARATOR).append(band).append(AppConstants.SEPARATOR).append(country).append("\r\n");
//			}
//			writer.write(sb.toString());
//			writer.close();
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
	}
	
	private int getRandomNumberInRange(int min, int max) {

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	private String getRandomUsername(int type) {
		String username = "";
		switch (type) {
		case 1:
			username = faker.ancient().god();
			break;
		case 2:
			username = faker.artist().name();
			break;
		case 3:
			username = faker.backToTheFuture().character();
			break;
		case 4:
			username = faker.dragonBall().character();
			break;
		case 5:
			username = faker.dune().character();
			break;
		case 6:
			username = faker.elderScrolls().dragon();
			break;
		case 7:
			username = faker.hipster().word();
			break;
		case 8:
			username = faker.pokemon().name();
			break;
		case 9:
			username = faker.lordOfTheRings().character();
			break;
		case 10:
			username = faker.witcher().monster();
			break;
			
		default:
			break;
		}
		
		username = username.replace(" ", "").toLowerCase();
		
		if (!userCountry.containsKey(username)) {
			userCountry.put(username, countries[getRandomNumberInRange(0, 99)]);
		}		
		return username;
	}
}
