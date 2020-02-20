package org.chaos.ethereal.helper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.BattleReport;
import org.chaos.ethereal.persistence.Hero;
import org.chaos.ethereal.persistence.Monster;

public class BattleHelper {

	BattleReport report;
	public BattleReport resolveBattle(Army army, List<String> phases) {
		report = new BattleReport();
		
		army.getMonsters().stream().collect(Collectors
				.groupingBy(Monster::getName, Collectors.counting()));
		//Getting the biggest horde for this battle
//		report.setBiggestHorde(String.join(",", army.getMonsters().stream().collect(Collectors.groupingBy(Monster::getName),
//				Collectors.counting()).keySet()));
		
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
		diceDmg.stream().forEach(m -> totalDmgList.add(UtilHelper.rollDie(m)));
		Integer critical = criticalHit(totalDmgList.size());
		totalDmgList.set(critical, totalDmgList.get(critical)*20);
		report.setHardestBlowMonsterNo(String.valueOf(totalDmgList.get(critical)));
		StringBuilder sb = new StringBuilder();
		Monster hardestBlowMonster = army.getMonsters().get(critical);
		sb.append(hardestBlowMonster.getName());
		report.setHardestBlowMonster(sb.toString());
		sb.setLength(0);
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
		report.setHeroCasualties(deadHeroes);
	}

	private void resolveAttackPhase(Army army) {
		Integer totalDmg = army.getHeroes().stream().mapToInt(Hero::getDamage).sum();
		Integer critical = criticalHit(army.getHeroes().size());
		StringBuilder sb = new StringBuilder();
		Hero hardestBlowHero = army.getHeroes().get(critical);
		hardestBlowHero.setDamage(hardestBlowHero.getDamage()*20);
		report.setHardestBlowHeroNo(String.valueOf(hardestBlowHero.getDamage()));
		sb.append(hardestBlowHero.getName()).append(" the ").append(hardestBlowHero.getRace()).append(" ").append(hardestBlowHero.getClazz());
		report.setHardestBlowHero(sb.toString());
		sb.setLength(0);
		List<Monster> deadMonsters = new ArrayList<>();
		for (Monster monster : army.getMonsters()) {
			if (totalDmg > monster.getComputedHP()) {
				totalDmg -= monster.getComputedHP();
				deadMonsters.add(monster);
			}else {
				break;
			}
		}
		hardestBlowHero.setDamage(hardestBlowHero.getDamage()/20);
		army.getMonsters().removeAll(deadMonsters);
		report.setMonsterCasualties(String.valueOf(deadMonsters.size()));
	}

	private Integer criticalHit(Integer attackerNo) {
		return UtilHelper.getRandomNumberInRange(0, attackerNo-1);
	}
	
	public Integer resolveBlow(Integer damageDelta, Integer armorDelta) {
		Integer result = 0;
		
		return result;
	}
}
