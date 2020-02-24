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
import org.chaos.ethereal.utils.UtilHelper;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class BattleHelper {

	BattleReport report;
	LambdaLogger logger;
	
	public BattleHelper(LambdaLogger logger) {
		this.logger = logger;
	}

	public BattleReport resolveBattle(Army army, List<String> phases) {
		report = new BattleReport();
		
		report.setBiggestHorde(getBiggestHorde(army));
		report.setStartingHeroes(army.getHeroes().size());
		report.setStartingMonsters(army.getMonsters().size());
		
		for (String phase : phases) {
			switch (phase.toUpperCase()) {
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
		determineWinner();
		return report;
	}
	
	private void determineWinner() {
		if ((report.getStartingMonsters() == report.getMonsterCasualties()) || 
				((report.getStartingMonsters()-report.getMonsterCasualties())/20<(report.getStartingHeroes()-report.getHeroCasualties().size()))) {
			report.setWinner(AppConstants.HEROES);
		} else {
			report.setWinner(AppConstants.MONSTERS);
		}
		
	}

	private String getBiggestHorde(Army army) {
		//These two streams collect the most frequent mosnter and its number. Should more than one exist, an arbitrary string and number are returned.
		//Disclaimer: use of Function.identity() collector was blatantly copied from StackOverflow since it was the best way to retrieve the key,value pair to use the mapping function
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
		//Stream to get all the properties for the dmg dice from monsters.
		List<String> diceDmg = army.getMonsters().stream().map(Monster::getMainAttack).collect(Collectors.toList());
		List<Integer> totalDmgList = new ArrayList<>();
		List<Hero> deadHeroes = new ArrayList<>();
		
		//Stream+Lambda expression to add all the dice rolls at once
		//I get them on a list to select a random critical hit before adding them up
		diceDmg.stream().forEach(m -> totalDmgList.add(UtilHelper.rollDie(m)));
		
		//Setting up the report for the final load phase
		Integer critical = criticalHit(totalDmgList.size());
		totalDmgList.set(critical, totalDmgList.get(critical)*20);
		report.setHardestBlowMonsterNo(totalDmgList.get(critical));
		StringBuilder sb = new StringBuilder();
		Monster hardestBlowMonster = army.getMonsters().get(critical);
		sb.append(hardestBlowMonster.getName());
		report.setHardestBlowMonster(sb.toString());
		sb.setLength(0);
		
		//Calculating all the damage
		Integer totalDmg = totalDmgList.stream().mapToInt(Integer::intValue).sum();
		
		//Using a traditional for loop to be able to break it as soon as no more damage to inflict is left
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
		//Arbitrarily selecting a hero to perform a critical hit
		Integer critical = criticalHit(army.getHeroes().size());
		StringBuilder sb = new StringBuilder();
		List<Monster> deadMonsters = new ArrayList<>();
		Hero hardestBlowHero = army.getHeroes().get(critical);
		hardestBlowHero.setDamage(hardestBlowHero.getDamage()*3);
		
		//Summing up all damage with a simple stream
		Integer totalDmg = army.getHeroes().stream().mapToInt(Hero::getDamage).sum();
		
		//Preparing report for load phase
		report.setHardestBlowHeroNo(hardestBlowHero.getDamage());
		sb.append(hardestBlowHero.getName()).append(" the ").append(hardestBlowHero.getRace()).append(" ").append(hardestBlowHero.getClazz());
		report.setHardestBlowHero(sb.toString());
		sb.setLength(0);
//		Integer deadMonsters = 0;
		//Using a traditional for loop to be able to break it as soon as no more damage to inflict is left
		for (Monster monster : army.getMonsters()) {
			if (totalDmg > monster.getComputedHP()) {
				totalDmg -= monster.getComputedHP();
				deadMonsters.add(monster);
			}else {
				break;
			}
		}
		hardestBlowHero.setDamage(hardestBlowHero.getDamage()/3);
		
		//We remove all the dead monsters from the main army list
		army.getMonsters().removeAll(deadMonsters);
//		army.setMonsters(army.getMonsters().subList(deadMonsters-1, army.getMonsters().size()-1));
		report.setMonsterCasualties(deadMonsters.size());
	}

	private Integer criticalHit(Integer attackerNo) {
		return UtilHelper.getRandomNumberInRange(0, attackerNo-1);
	}
	
}
