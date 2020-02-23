package org.chaos.ethereal.helper;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class UtilHelper {
	
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
		diceNumber = Integer.valueOf(die.substring(0,die.indexOf("d")));
		if (die.contains("+")) {
			dieSize = Integer.valueOf(die.substring(die.indexOf("d")+1, die.indexOf("+")));
			modifier = Integer.valueOf(die.substring(die.indexOf("+")+1));
		} else {
			dieSize = Integer.valueOf(die.substring(die.indexOf("d")+1));
			modifier = 0;
		}
		
		for (int i = 0; i < diceNumber; i++) {
			result += (r.nextInt(dieSize  + 1));
		}
		result += modifier;
		
		return result;
	}
	
}
