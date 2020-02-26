package org.chaos.ethereal.helper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.Hero;
import org.chaos.ethereal.persistence.Monster;
import org.chaos.ethereal.persistence.SetOfValues;
import org.chaos.ethereal.persistence.Specs;
import org.chaos.ethereal.persistence.annotations.Validate;
import org.chaos.ethereal.utils.AmazonUtils;
import org.chaos.ethereal.utils.AppConstants;
import org.chaos.ethereal.utils.UtilHelper;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;

public class ArmyHelper {

	AmazonDynamoDB client;
	DynamoDBMapper mapper;
	Integer heroArmySize;
	Integer monsterArmySize;
	LambdaLogger logger;
	SpecsHelper specsHelper = new SpecsHelper();
	SetOfValuesHelper setOfValuesHelper = new SetOfValuesHelper();
	
	public ArmyHelper(LambdaLogger logger) {
		this.logger = logger;
	}

	private void initClient() {
		client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();
		mapper = new DynamoDBMapper(client);
	}
	
	public Army createArmy(Integer monstersSize, Integer heroesSize) throws Exception {
		validateArmyParams(monstersSize, heroesSize);
		initClient();
		Army army = new Army();
		List<Hero> dbHeroes;
		List<Hero> armyHeroes = new ArrayList<>();
		List<Monster> dbMonsters;
		List<Monster> armyMonsters = new ArrayList<>();
		Set<Integer> rngHeroes;
		Monster currentMonster;
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		
		//Getting heroes and monsters from DB. Should be grabbed from the Handler to make this Helper environment agnostic
		dbHeroes = mapper.scan(Hero.class, scanExpression);
		dbMonsters = mapper.scan(Monster.class, scanExpression);
		heroArmySize = heroesSize;
		monsterArmySize = monstersSize;
		
		//If more heroes than available are set, stop the battle
		if (heroArmySize>dbHeroes.size()) {
			throw new Exception("Not enough heroes to create the army!!");
		}
		
		//We select random heroes from all the available
		rngHeroes = UtilHelper.getUniqueRandomNumberInRange(dbHeroes.size()-1, heroesSize);
		for (Integer hero : rngHeroes) {
			armyHeroes.add(dbHeroes.get(hero));
		}
		
		//This stream+lambda expression is used to compute the values since they can be calculated in many forms and thus, should not be stored in DB, but computed each time
		armyHeroes.stream().forEach(h->{
			h.setDamage(computeHeroStats(h.getMainStat()+h.getSecondaryStat()));
			h.setMana(computeHeroStats(h.getMagic()*2));
			h.setHitpoints(computeHeroStats(h.getMainStat()*20));
		});
		army.setHeroes(armyHeroes);
		
		//For the sake of readability, sometimes a traditional for loop is better to be used
		for (int i = 0; i < monstersSize; i++) {
			currentMonster = dbMonsters.get(UtilHelper.getRandomNumberInRange(0, dbMonsters.size()-1));
			currentMonster.setComputedHP(computeMonsterHP(currentMonster.getHitpoints()));
			currentMonster.setArmyId(i);
			armyMonsters.add(currentMonster);
		}
		army.setMonsters(armyMonsters);
		
		return army;
	}
	
	private void validateArmyParams (Integer monstersSize, Integer heroesSize) throws Exception{
		if (monstersSize == null || heroesSize == null || monstersSize < 1 || heroesSize < 1) {
			throw new Exception("Params used to create army are invalid");
		}
	}
	
	public Boolean validateHeroMonster(Object armyHM, List<Specs> specs, List<SetOfValues> setOfValues) throws Exception {
		Map<String, Object> props = beanProperties(armyHM);
		Specs spec;
		Iterator<Specs> it = specs.iterator();
		while (it.hasNext()) {
			spec = it.next();
			if (props != null && !props.isEmpty()) {
				String fieldName = getField(armyHM.getClass(), spec.getFieldName());
				if (props.containsKey(fieldName) && armyHM.getClass().toGenericString().substring(armyHM.getClass().toGenericString().lastIndexOf('.')+1).equalsIgnoreCase(spec.getRecordType())) {
					Object field = props.get(fieldName);
					// Check if mandatory
					if (field == null || field.toString().isEmpty()){
						if (spec.getMandatory().equalsIgnoreCase(AppConstants.YES)){
							return false;
						}
					} else {
						// Check length
						if (field.toString().length() > spec.getLength()) {
							return false;
						}
						// Check field type
						if (spec.getType().equals(AppConstants.TYPE_NUMBER) && !isNumber(field.toString())) {
							
							return false;
						}
						if (spec.getType().equals(AppConstants.TYPE_DIE) && !isDie(field.toString())) {
							return false;
						}
						
						// Possible values check
						if (spec.getSetOfValues() != null && !spec.getSetOfValues().trim().isEmpty()) {
							try {
								SetOfValues set = setOfValues.stream().filter(
										s -> s.getSet().equalsIgnoreCase(fieldName))
										.findFirst()
										.orElse(null);
								if (!set.getValues().contains(field)) {
									return false;
								}
							} catch (Exception e) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	public void validateArmy(Army army) throws Exception {
		List<Specs> specs = specsHelper.retrieveAllFileSpecs(logger);
		List<SetOfValues> setOfValues = setOfValuesHelper.retrieveAllPossibleValues(logger);
		List<Hero> rejectedHeroes = new ArrayList<>();
		List<Monster> rejectedMonsters = new ArrayList<>();
		
		for (Hero hero : army.getHeroes()) {
			if (!validateHeroMonster(hero, specs, setOfValues)) {
				rejectedHeroes.add(hero);
			}
		}
		army.getHeroes().removeAll(rejectedHeroes);
		
		for (Monster monster : army.getMonsters()) {
			if (!validateHeroMonster(monster, specs, setOfValues)) {
				rejectedMonsters.add(monster);
			}
		}
		army.getMonsters().removeAll(rejectedMonsters);
	}
	
	private boolean isNumber(String field){
		try {
			Integer.parseInt(field);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	private boolean isDie(String field){
		try {
			String pattern = "(\\d+)?d(\\d+)([\\+\\-]\\d+)?";
			return field.matches(pattern);
		} catch (Exception e) {
			return false;
		}
	}
	
	private String getField(Class<?> clazz, String fieldName) throws Exception {

		for (Field f : clazz.getDeclaredFields()) {
			Validate annotation = f.getAnnotation(Validate.class);
			if (annotation != null && annotation.dbname().equalsIgnoreCase(fieldName)) {
				return f.getName().toLowerCase();
			}
		}
		return null;
	}
	
	public Army createArmyFromFile(String fileName) throws Exception {
		Army army;
		Gson gson = new Gson();
		InputStream is = AmazonUtils.downloadObject(AppConstants.S3_BUCKET, AppConstants.S3_ARMY_PATH, fileName);
		Reader reader = new InputStreamReader(is);
		army = gson.fromJson(reader, Army.class);
		
		return army;
	}
	
	public Army createArmyFromIS(InputStream is) throws Exception{
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
		return Math.toIntExact(Math.round(stat*((double)monsterArmySize/heroArmySize)*0.2));
	}
	
    public static Map<String, Object> beanProperties(Object bean) throws Exception {
    	Map<String, Object> result = new LinkedHashMap<>();
        Class<?> clazz = bean.getClass();
        for (final Field field : clazz.getDeclaredFields()) {
        	field.setAccessible(true);
            result.put(field.getName().toLowerCase(), field.get(bean));
            field.setAccessible(false);
        }
        return result;
    }
}
