package org.chaos.ethereal.helper;

import java.util.Random;

import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.BattleReport;

public class BattleHelper {

	public BattleReport resolveBattle(Army army) {
		BattleReport report = new BattleReport();
		
		
		return report;
	}
	
	public Integer resolveBlow(Integer damageDelta, Integer armorDelta) {
		Integer result = 0;
		
		return result;
	}
	
	public Integer calculateDamage(String damageDice) {
		Integer result = 0;
		Integer diceNumber;
		Integer dieSize;
		Integer modifier;
		Random r = new Random();
		diceNumber = Integer.valueOf(damageDice.substring(0,damageDice.indexOf("d")));
		if (damageDice.contains("+")) {
			dieSize = Integer.valueOf(damageDice.substring(damageDice.indexOf("d")+1, damageDice.indexOf("+")));
			modifier = Integer.valueOf(damageDice.substring(damageDice.indexOf("+")+1));
		} else {
			dieSize = Integer.valueOf(damageDice.substring(damageDice.indexOf("d")+1));
			modifier = 0;
		}
		
		for (int i = 0; i < diceNumber; i++) {
			result += (r.nextInt((dieSize - 1) + 1) + 1);
		}
		result += modifier;
		
		return result;
	}
}
