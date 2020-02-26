package org.chaos.ethereal.utils;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class UtilHelper {
	
	/**
	 * For Sonar purposes. Code smells
	 */
	private UtilHelper() {
	}

	public static Integer getRandomNumberInRange(Integer min, Integer max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	public static Set<Integer> getUniqueRandomNumberInRange(Integer totalArmySize, Integer maxArmySize) {
		Set<Integer> rngList = new HashSet<>();
		while (rngList.size()<maxArmySize) {
			rngList.add(getRandomNumberInRange(0,totalArmySize));
		}
		
		return rngList; 
	}
	
	public static Integer rollDie(String die) {
		Integer result = 0;
		Integer diceNumber;
		Integer dieSize;
		Integer modifier;
		Random r = new Random();
		diceNumber = Integer.valueOf(die.substring(0,die.indexOf('d')));
		if (die.contains("+")) {
			dieSize = Integer.valueOf(die.substring(die.indexOf('d')+1, die.indexOf('+')));
			modifier = Integer.valueOf(die.substring(die.indexOf('+')+1));
		} else {
			dieSize = Integer.valueOf(die.substring(die.indexOf('d')+1));
			modifier = 0;
		}
		
		for (int i = 0; i < diceNumber; i++) {
			result += (r.nextInt(dieSize  + 1));
		}
		result += modifier;
		
		return result;
	}
	
	public static String getSecondsAndMillisecondsDelta(Date d1, Date d2) {
		long seconds = getSecondsDelta(d1,d2); 
		return seconds + " secs. " +  (getMillisecondsDelta(d1,d2) - (seconds * 1000)) + " millis.";
	}	
	
	public static long getSecondsDelta(Date d1, Date d2) {
		return (d2.getTime() - d1.getTime()) / 1000;
	}
	
	public static long getMillisecondsDelta(Date d1, Date d2) {
		return d2.getTime() - d1.getTime();
	}
	
}
