package org.chaos.ethereal.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.BattleReport;
import org.chaos.ethereal.persistence.Hero;
import org.chaos.ethereal.persistence.Monster;

public class BattleHelper {

	BattleReport report;
	public BattleReport resolveBattle(Army army, List<String> phases) {
		report = new BattleReport();
		for (String phase : phases) {
			switch (phase) {
			case "Attack":
				resolveAttackPhase(army);
				break;
			case "Defend":
				resolveDefendPhase(army);
				break;
			case "Magic":
				resolveMagicPhase(army);
				break;
			case "Hold":
				resolveHoldPhase(army);
				break;

			default:
				break;
			}
		}
		return report;
	}
	
	private void resolveHoldPhase(Army army) {
		// TODO Auto-generated method stub
		
	}

	private void resolveMagicPhase(Army army) {
		// TODO Auto-generated method stub
		
	}

	private void resolveDefendPhase(Army army) {
		List<String> diceDmg = army.getMonsters().stream().map(Monster::getMainAttack).collect(Collectors.toList());
		List<Integer> totalDmgList = new ArrayList<>();
		List<Hero> deadHeroes = new ArrayList<>();
		diceDmg.stream().forEach(m ->{
			totalDmgList.add(UtilHelper.rollDie(m));
		});
		Integer totalDmg = totalDmgList.stream().mapToInt(Integer::intValue).sum();
		for (Hero hero : army.getHeroes()) {
			if (totalDmg > hero.getHitpoints()) {
				totalDmg -= hero.getHitpoints();
				deadHeroes.add(hero);
			}else {
				break;
			}
		}
		army.getHeroes().removeAll(deadHeroes);
	}

	private void resolveAttackPhase(Army army) {
		Integer totalDmg = army.getHeroes().stream().mapToInt(Hero::getDamage).sum();
		List<Monster> deadMonsters = new ArrayList<>();
		for (Monster monster : army.getMonsters()) {
			if (totalDmg > monster.getComputedHP()) {
				totalDmg -= monster.getComputedHP();
				deadMonsters.add(monster);
			}else {
				break;
			}
		}
		army.getMonsters().removeAll(deadMonsters);
	}

	public Integer resolveBlow(Integer damageDelta, Integer armorDelta) {
		Integer result = 0;
		
		return result;
	}
}
