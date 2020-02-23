package org.chaos.ethereal.helper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.Hero;
import org.chaos.ethereal.persistence.Monster;
import org.chaos.ethereal.persistence.SetOfValues;
import org.chaos.ethereal.persistence.Specs;

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
	
	public List<Hero> validateHero(Hero hero, List<Specs> specs, List<SetOfValues> setOfValues) throws Exception {
		Map<String, Object> armyProps = beanProperties(hero);
		List<Hero> invalidHeroes = new ArrayList<>();
		
		
		
		
		return invalidHeroes;
	}
	
	public List<Monster> validateMonster(Monster monster, List<Specs> specs, List<SetOfValues> setOfValues) throws Exception {
		Map<String, Object> armyProps = beanProperties(monster);
		List<Monster> invalidMonsters = new ArrayList<>();
		
		
		
		
		return invalidMonsters;
	}
	
	public void validateArmy(Army army) {
		List<Specs> specs = specsHelper.retrieveAllFileSpecs(logger);
		List<SetOfValues> setOfValues = setOfValuesHelper.retrieveAllPossibleValues(logger);
		
//		try {
//            List<Object> secondaryRecords = null;
//            final String mainRecordType = rulesBean.getRecordTypes().get(0);
//            final String secondaryRecordType = rulesBean.getRecordTypes().get(1);
//            final List<String> fieldSet = (List<String>)rulesBean.getFieldSet();
//            final int fileId = rulesBean.getFileId();
//            Map<String, Object> propsIn = beanProperties(army.getHeroes());
//            final int recordId = Integer.parseInt(rulesBean.getRecordId());
//            final List<FileSpec> recordFS = rulesBean.getFsMap().get("RECORD_FILE_SPEC");
//
//            for (Specs spec : recordFS) {
//                if (propsIn != null && !propsIn.isEmpty()) {
//                    final String tableName = spec.getFieldName();
//                    final String fieldName = RulesHelper.getField(origin, tableName, mainRecordType);
//                    if (!propsIn.containsKey(fieldName)) {
//                        continue;
//                    }
//                    final Object field = propsIn.get(fieldName);
//                    if (field == null || field.toString().isEmpty()) {
//                        if (!spec.getMandatory().equalsIgnoreCase("Y")) {
//                            continue;
//                        }
//                        tempRejectionDTO = reject(mainRecordType, fileId, recordId, tableName, "01", fieldSet, spec);
//                        rejectionList.add(tempRejectionDTO.getRejectionList().get(0));
//                        if (sysreject || !tempRejectionDTO.getSysReject()) {
//                            continue;
//                        }
//                        sysreject = Boolean.TRUE;
//                    }
//                    else {
//                        if (field.toString().length() > spec.getLength()) {
//                            tempRejectionDTO = reject(mainRecordType, fileId, recordId, tableName, "18", fieldSet, spec);
//                            rejectionList.add(tempRejectionDTO.getRejectionList().get(0));
//                            if (!sysreject && tempRejectionDTO.getSysReject()) {
//                                sysreject = Boolean.TRUE;
//                            }
//                        }
//                        if (spec.getType().equals("N") && !isNumber(field.toString(), spec)) {
//                            tempRejectionDTO = reject(mainRecordType, fileId, recordId, tableName, "18", fieldSet, spec);
//                            rejectionList.add(tempRejectionDTO.getRejectionList().get(0));
//                            if (!sysreject && tempRejectionDTO.getSysReject()) {
//                                sysreject = Boolean.TRUE;
//                            }
//                            if (rulesBean.getSource().equals("S3_PATH_EBDE")) {
//                                sysreject = Boolean.TRUE;
//                            }
//                        }
//                        if ("Y".equalsIgnoreCase(spec.getDateYN())) {
//                            if (fieldName.equalsIgnoreCase("arrivaldate") || fieldName.equalsIgnoreCase("departuredate")) {
//                                final String vd = RulesHelper.isValidDate(field.toString(), !origin.equals("PIONEER"), mainRecordType);
//                                if (!vd.equals("0")) {
//                                    tempRejectionDTO = reject(mainRecordType, fileId, recordId, tableName, vd, fieldSet, spec);
//                                    rejectionList.add(tempRejectionDTO.getRejectionList().get(0));
//                                    if (!sysreject && tempRejectionDTO.getSysReject()) {
//                                        sysreject = Boolean.TRUE;
//                                    }
//                                }
//                            }
//                            if (fieldName.equalsIgnoreCase("bookingdate")) {
//                                final String vd = RulesHelper.isValidDate(field.toString(), false, mainRecordType);
//                                if (!vd.equals("0")) {
//                                    tempRejectionDTO = reject(mainRecordType, fileId, recordId, tableName, vd, fieldSet, spec);
//                                    rejectionList.add(tempRejectionDTO.getRejectionList().get(0));
//                                    if (!sysreject && tempRejectionDTO.getSysReject()) {
//                                        sysreject = Boolean.TRUE;
//                                    }
//                                }
//                            }
//                        }
//                        if (!isGui || origin == null || origin.equals("VALIDSTAY") || spec.getSetOfValues() == null || spec.getSetOfValues().isEmpty() || fieldName.equalsIgnoreCase("brandcode")) {
//                            continue;
//                        }
//                        boolean found = false;
//                        try {
//                            if (rulesBean.getPvMap().containsKey(spec.getSetOfValues()) && rulesBean.getPvMap().get(spec).contains(field.toString())) {
//                                found = true;
//                            }
//                        }
//                        catch (Exception e2) {
//                            found = false;
//                        }
//                        if (found) {
//                            continue;
//                        }
//                        tempRejectionDTO = reject(mainRecordType, fileId, recordId, tableName, "18", fieldSet, spec);
//                        rejectionList.add(tempRejectionDTO.getRejectionList().get(0));
//                        if (!sysreject && tempRejectionDTO.getSysReject()) {
//                            sysreject = Boolean.TRUE;
//                        }
//                        if (!rulesBean.getSource().equals("S3_PATH_EBDE")) {
//                            continue;
//                        }
//                        sysreject = Boolean.TRUE;
//                    }
//                }
//            }
//            if (secondaryRecords != null) {
//                Iterator<FileSpec> it = txFS.iterator();
//                for (final Object subRecord : secondaryRecords) {
//                    final String subRecordId = String.valueOf(obtainSubRecordId(subRecord));
//                    final LinkedHashMap<String, Object> props = new LinkedHashMap<String, Object>(RulesHelper.beanProperties(subRecord));
//                    while (it.hasNext()) {
//                        final FileSpec fs = it.next();
//                        if (props != null && !props.isEmpty()) {
//                            final String tableName2 = fs.getFieldName();
//                            final String fieldName2 = RulesHelper.getField(origin, tableName2, secondaryRecordType);
//                            if ((rulesBean.getFieldsToCheck() != null || !props.containsKey(fieldName2)) && (rulesBean.getFieldsToCheck() == null || rulesBean.getFieldsToCheck().get(secondaryRecordType) == null || !BasicRulesService.rulesHelper.containsField((List)rulesBean.getFieldsToCheck().get(secondaryRecordType), fieldName2))) {
//                                continue;
//                            }
//                            final Object field2 = props.get(fieldName2);
//                            if (field2 == null || field2.toString().isEmpty()) {
//                                if (!fs.getMandatory().equalsIgnoreCase("Y")) {
//                                    continue;
//                                }
//                                tempRejectionDTO = reject(secondaryRecordType, fileId, recordId, subRecordId, tableName2, "01", fieldSet, fs);
//                                rejectionList.add(tempRejectionDTO.getRejectionList().get(0));
//                                if (sysreject || !tempRejectionDTO.getSysReject()) {
//                                    continue;
//                                }
//                                sysreject = Boolean.TRUE;
//                            }
//                            else {
//                                if (field2.toString().length() > fs.getLength()) {
//                                    if (!fieldName2.equals("taxpercentageused")) {
//                                        tempRejectionDTO = reject("TX", fileId, recordId, subRecordId, tableName2, "18", fieldSet, fs);
//                                        rejectionList.add(tempRejectionDTO.getRejectionList().get(0));
//                                        if (!sysreject && tempRejectionDTO.getSysReject()) {
//                                            sysreject = Boolean.TRUE;
//                                        }
//                                    }
//                                    else if (field2.toString().startsWith("-") && field2.toString().length() > fs.getLength() + 1) {
//                                        tempRejectionDTO = reject("TX", fileId, recordId, subRecordId, tableName2, "18", fieldSet, fs);
//                                        rejectionList.add(tempRejectionDTO.getRejectionList().get(0));
//                                        if (!sysreject && tempRejectionDTO.getSysReject()) {
//                                            sysreject = Boolean.TRUE;
//                                        }
//                                    }
//                                    else if (!field2.toString().startsWith("-")) {
//                                        tempRejectionDTO = reject("TX", fileId, recordId, subRecordId, tableName2, "18", fieldSet, fs);
//                                        rejectionList.add(tempRejectionDTO.getRejectionList().get(0));
//                                        if (!sysreject && tempRejectionDTO.getSysReject()) {
//                                            sysreject = Boolean.TRUE;
//                                        }
//                                    }
//                                }
//                                if (!fs.getType().equals("N")) {
//                                    continue;
//                                }
//                                if (fieldName2.equals("taxpercentageused") && !isNumberForTaxPercentageUsed(field2.toString(), fs)) {
//                                    tempRejectionDTO = reject(secondaryRecordType, fileId, recordId, subRecordId, tableName2, "18", fieldSet, fs);
//                                    rejectionList.add(tempRejectionDTO.getRejectionList().get(0));
//                                    if (!sysreject && tempRejectionDTO.getSysReject()) {
//                                        sysreject = Boolean.TRUE;
//                                    }
//                                }
//                                if (fieldName2.equals("taxpercentageused") || isNumber(field2.toString(), fs)) {
//                                    continue;
//                                }
//                                tempRejectionDTO = reject(secondaryRecordType, fileId, recordId, subRecordId, tableName2, "18", fieldSet, fs);
//                                rejectionList.add(tempRejectionDTO.getRejectionList().get(0));
//                                if (sysreject || !tempRejectionDTO.getSysReject()) {
//                                    continue;
//                                }
//                                sysreject = Boolean.TRUE;
//                            }
//                        }
//                    }
//                    it = txFS.iterator();
//                }
//            }
//            rejectionDTO = new RejectionsDTO((List)rejectionList, sysreject);
//            return rejectionDTO;
//        }
//        catch (Exception e) {
//            BasicRulesService.logger.error("RulesRest.processImportFiles: Error processing import files.", (Throwable)e);
//            throw new ServiceException(e);
//        }
//		
	}
	
	public Army createArmyFromFile(String fileName) {
		Army army;
		Gson gson = new Gson();
		InputStream is = AmazonUtils.downloadObject(AppConstants.S3_BUCKET, AppConstants.S3_ARMY_PATH, fileName);
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
	
//    private static String obtainSubRecordId(final Object clazz) throws ExceptionBase {
//        try {
//            final Method getSubRecordId = clazz.getClass().getMethod("getSubRecordId", (Class<?>[])new Class[0]);
//            return String.valueOf(getSubRecordId.invoke(clazz, new Object[0]));
//        }
//        catch (Exception e) {
//            BasicRulesService.logger.info("Exception ocurred with the sub record)");
//            throw new ServiceException(e);
//        }
//    }
//    
//    private static RejectionsDTO reject(final String fileType, final int fileId, final int recordId, final String fieldName, final String errorCode, final List<String> fieldSet, final FileSpec fs) {
//        return reject(fileType, fileId, recordId, null, fieldName, errorCode, null, fieldSet, fs);
//    }
//    
//    private static RejectionsDTO reject(final String fileType, final int fileId, final int recordId, final String subRecordId, final String fieldName, final String errorCode, final List<String> fieldSet, final FileSpec fs) {
//        return reject(fileType, fileId, recordId, subRecordId, fieldName, errorCode, null, fieldSet, fs);
//    }
//    
//    private static RejectionsDTO reject(final String fileType, final int fileId, final int recordId, final String subRecordId, final String fieldName, final String errorCode, final Boolean sysreject, final List<String> fieldSet, final FileSpec fs) {
//        Boolean isReject = Boolean.FALSE;
//        final List<RejectedDTO> rejectList = new ArrayList<RejectedDTO>();
//        final RejectedDTO reject = new RejectedDTO();
//        reject.setFileType(fileType);
//        reject.setFileId(fileId);
//        reject.setRecordId(recordId);
//        reject.setFieldName(fieldName);
//        reject.setErrorCode(BasicRulesService.rulesHelper.getErrorCode(errorCode, fs));
//        if (subRecordId != null) {
//            reject.setSubRecordId(Integer.valueOf(subRecordId));
//        }
//        if (fieldSet != null && !fieldSet.isEmpty() && !fieldSet.contains(fieldName)) {
//            isReject = Boolean.TRUE;
//        }
//        if (Boolean.TRUE.equals(sysreject)) {
//            isReject = Boolean.TRUE;
//        }
//        rejectList.add(reject);
//        final RejectionsDTO rejectionDTO = new RejectionsDTO((List)rejectList, isReject);
//        return rejectionDTO;
//    }
//    
//    private static boolean isNumber(final String field, final FileSpec fs) {
//        Integer intValue = null;
//        Double doubleValue = null;
//        try {
//            if (fs == null || fs.getDecimals() == 0) {
//                intValue = Integer.parseInt(field);
//            }
//            else {
//                doubleValue = Double.parseDouble(field);
//                final Pattern p1 = Pattern.compile("^[0-9]{1," + fs.getLength() + "}$");
//                final Matcher m1 = p1.matcher(field.trim());
//                final Pattern p1Neg = Pattern.compile("^[\\-|\\+]{1}[0-9]{1," + (fs.getLength() - 1) + "}$");
//                final Matcher m1Neg = p1Neg.matcher(field.trim());
//               final Pattern p2 = Pattern.compile("^[0-9]{0," + (fs.getLength() - fs.getDecimals()) + "}\\.[0-9]{1," + fs.getDecimals() + "}$");
//                final Matcher m2 = p2.matcher(field.trim());
//                final Pattern p2Neg = Pattern.compile("^[\\-|\\+]{1}[0-9]{0," + (fs.getLength() - fs.getDecimals() - 1) + "}\\.[0-9]{1," + fs.getDecimals() + "}$");
//                final Matcher m2Neg = p2Neg.matcher(field.trim());
//                if (!m1.find() && !m1Neg.find() && !m2.find() && !m2Neg.find()) {
//                    doubleValue = null;
//                }
//            }
//        }
//        catch (NumberFormatException e) {
//            BasicRulesService.logger.info("Field " + field + " is not a number.");
//        }
//        Boolean isNumber;
//        if (intValue != null || doubleValue != null) {
//            isNumber = Boolean.TRUE;
//        }
//        else {
//            isNumber = Boolean.FALSE;
//        }
//        return isNumber;
//    }
//    
//    private static boolean isNumberForTaxPercentageUsed(final String field, final FileSpec fs) {
//        Integer intValue = null;
//        Double doubleValue = null;
//        try {
//            if (fs == null || fs.getDecimals() == 0) {
//                intValue = Integer.parseInt(field);
//            }
//            else {
//                doubleValue = Double.parseDouble(field);
//            }
//        }
//        catch (NumberFormatException e) {
//            BasicRulesService.logger.info("Field " + field + " is not a number.");
//        }
//        Boolean isNumber;
//        if (intValue != null || doubleValue != null) {
//            isNumber = Boolean.TRUE;
//        }
//        else {
//            isNumber = Boolean.FALSE;
//        }
//        return isNumber;
//    }
    
    public static Map<String, Object> beanProperties(Object bean) throws Exception {
    	Map<String, Object> result = new LinkedHashMap<>();
    	try {
            Class<?> clazz = bean.getClass();
            for (final Field field : clazz.getFields()) {
                result.put(field.getName().toLowerCase(), field.get(bean));
            }
        }
        catch (IllegalAccessException | IllegalArgumentException ex2) {
        }
        catch (Exception e) {
        }
        return result;
    }


}
