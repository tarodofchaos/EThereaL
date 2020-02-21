package org.chaos.ethereal.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
		
		report.setBiggestHorde(getBiggestHorde(army));
		
		for (String phase : phases) {
			switch (phase) {
			//Attack
			case "A":
				resolveAttackPhase(army);
				break;
			//Defend
			case "D":
				resolveDefendPhase(army);
				break;
			//Magic
			case "M":
				resolveMagicPhase(army);
				break;
			//Hold
			case "H":
				resolveHoldPhase(army);
				break;

			default:
				break;
			}
		}
		return report;
	}
	
	private String getBiggestHorde(Army army) {
		String biggestHorde = army.getMonsters().stream()
		        .map(Monster::getName).filter(Objects::nonNull)
		        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
		        .entrySet().stream().max(Map.Entry.comparingByValue())
		        .map(Map.Entry::getKey).orElse("Many hordes");
		Long biggestHordeSize =  army.getMonsters().stream()
		        .map(Monster::getName).filter(Objects::nonNull)
		        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
		        .entrySet().stream().max(Map.Entry.comparingByValue())
		        .map(Map.Entry::getValue).orElse(-1L);
		       
		StringBuilder sb = new StringBuilder();
		sb.append(biggestHordeSize.toString()).append(" ").append(biggestHorde);
		return sb.toString();
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
		Integer critical = criticalHit(army.getHeroes().size());
		StringBuilder sb = new StringBuilder();
		Hero hardestBlowHero = army.getHeroes().get(critical);
		hardestBlowHero.setDamage(hardestBlowHero.getDamage()*3);
		Integer totalDmg = army.getHeroes().stream().mapToInt(Hero::getDamage).sum();
		report.setHardestBlowHeroNo(String.valueOf(hardestBlowHero.getDamage()));
		sb.append(hardestBlowHero.getName()).append(" the ").append(hardestBlowHero.getRace()).append(" ").append(hardestBlowHero.getClazz());
		report.setHardestBlowHero(sb.toString());
		sb.setLength(0);
		Integer deadMonsters = 0;
		for (Monster monster : army.getMonsters()) {
			if (totalDmg > monster.getComputedHP()) {
				totalDmg -= monster.getComputedHP();
				deadMonsters++;
			}else {
				break;
			}
		}
		hardestBlowHero.setDamage(hardestBlowHero.getDamage()/3);
		army.setMonsters(army.getMonsters().subList(deadMonsters-1, army.getMonsters().size()-1));
		report.setMonsterCasualties(String.valueOf(deadMonsters));
	}

	private Integer criticalHit(Integer attackerNo) {
		return UtilHelper.getRandomNumberInRange(0, attackerNo-1);
	}
	
}
