package org.chaos.ethereal.cucumber;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.chaos.ethereal.TestContext;
import org.chaos.ethereal.helper.ArmyHelper;
import org.chaos.ethereal.helper.SetOfValuesHelper;
import org.chaos.ethereal.helper.SpecsHelper;
import org.chaos.ethereal.persistence.Army;
import org.chaos.ethereal.persistence.Hero;
import org.chaos.ethereal.persistence.Monster;
import org.chaos.ethereal.persistence.SetOfValues;
import org.chaos.ethereal.persistence.Specs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ArmyCreator {

	static class ArmyCreatorTest {
		static ArmyHelper helper = new ArmyHelper(new TestContext().getLogger());
		
		static Army generateAnArmy(Integer monstersSize, Integer heroesSize) throws Exception {
			return helper.createArmy(monstersSize, heroesSize);
		}
		
		static Boolean validateArmy(Object hm){
			SpecsHelper specsHelper = new SpecsHelper();
			SetOfValuesHelper setOfValuesHelper = new SetOfValuesHelper();
			List<Specs> specs = specsHelper.retrieveAllFileSpecs(new TestContext().getLogger());
			List<SetOfValues> setOfValues = setOfValuesHelper.retrieveAllPossibleValues(new TestContext().getLogger());
			try {
				return helper.validateHeroMonster(hm, specs, setOfValues);
			} catch (Exception e) {
				return false;
			}
		}
	}

	Integer heroArmySize;
	Integer monsterArmySize;
	Army army;
	String actualErrorMessage;
	Integer goodDexValue;
	Integer badDexValue;
	Boolean validationResult;
	Hero validatedHero;
	Monster validatedMonster;
	Object validatedHM;
	
	@Given("I have a valid Hero")
	public void i_have_a_valid_Hero() {
		validatedHero = new Hero();
		validatedHero.setClazz("Fighter");
		validatedHero.setDamage(12);
		validatedHero.setDexterity(12);
		validatedHero.setGender("Female");
		validatedHero.setHitpoints(30);
		validatedHero.setId(1);
		validatedHero.setMagic(12);
		validatedHero.setMana(30);
		validatedHero.setName("Lydia");
		validatedHero.setRace("Elf");
		validatedHero.setStrength(12);
	}
	
	@Given("a valid Monster")
	public void a_valid_Monster() {
		validatedMonster = new Monster();
		validatedMonster.setArmor(15);
		validatedMonster.setArmyId(1);
		validatedMonster.setComputedHP(200);
		validatedMonster.setHitpoints("4d4");
		validatedMonster.setId(1);
		validatedMonster.setLevel("2");
		validatedMonster.setMainAttack("1d10");
		validatedMonster.setName("Grog");
		validatedMonster.setSpecialAttack("2d4");
	}
	
	@Given("I generate an army from an API call")
	public void i_generate_an_army_from_an_API_call() {
	    monsterArmySize = 1000;
	    heroArmySize = 50;
	}

	@When("the army creator is called")
	public void the_army_creator_is_called() {
	    try {
	    	army = ArmyCreatorTest.generateAnArmy(monsterArmySize, heroArmySize);
		} catch (Exception e) {
			actualErrorMessage = e.getMessage();
		}
	}

	@Then("the army size has to be fifty Heroes")
	public void the_army_size_has_to_be_fifty_Heroes() {
	    assertTrue(army.getHeroes().size()==50);
	}

	@Then("and a thousand Monsters")
	public void a_thousand_Monsters() {
	    assertTrue(army.getMonsters().size()==1000);
	}
	
	@Then("no errors happened")
	public void no_errors_happened() {
		assertTrue(actualErrorMessage == null);
	}
	
	@Given("I generate an army from an API call with more heroes than available")
	public void i_generate_an_army_from_an_API_call_with_more_heroes_than_available() {
		monsterArmySize = 1000;
	    heroArmySize = 500;
	}
	
	@Then("I should get an Exception with the message \"Not enough heroes to create the army!!\"")
	public void i_should_get_an_Exception_with_the_message() {
	    assertTrue(actualErrorMessage.equals("Not enough heroes to create the army!!"));
	}
	
	@Given("I generate an army from an API call with invalid parameters")
	public void i_generate_an_army_from_an_API_call_with_invalid_parameters() {
		monsterArmySize = null;
	    heroArmySize = -7;
	}
	
	@Then("I should get an Exception with the error message")
	public void i_should_get_an_Exception_with_the_error_message() {
		System.out.println(actualErrorMessage);
		assertNotNull(actualErrorMessage);
		
	}
	
	@Given("a hero has a {int} as dex")
	public void a_hero_has_a_as_dex(Integer int1) {
	    validatedHero.setDexterity(int1);
	    validatedHM = validatedHero;
	}
	
	@When("the army validator is invoked")
	public void the_army_validator_is_invoked() {
		validationResult = ArmyCreatorTest.validateArmy(validatedHM);
	}
	
	@Then("I should get a false value")
	public void i_should_get_a_false_value() {
	    assertFalse(validationResult);
	}
	
	@Given("a hero has a {string} as race")
	public void a_hero_has_a_as_race(String string) {
		validatedHero.setRace(string);
		validatedHM = validatedHero;
	}
	
	@Given("a hero has a {string} as class")
	public void a_hero_has_a_as_class(String string) {
		validatedHero.setClazz(string);
		validatedHM = validatedHero;
	}
	
	@Given("a monster has a {string} as hitpoints")
	public void a_monster_has_a_as_hitpoints(String string) {
		validatedMonster.setHitpoints(string);
		validatedHM = validatedMonster;
	}
	
	@Given("a monster has a {string} as level")
	public void a_monster_has_a_as_level(String string) {
	   validatedMonster.setLevel(string);
	   validatedHM = validatedMonster;
	}
	
	@Given("a monster has a {string} as name")
	public void a_monster_has_a_as_name(String string) {
	   validatedMonster.setName(string);
	   validatedHM = validatedMonster;
	}
	
	@Then("I should get a true value")
	public void i_should_get_a_true_value() {
		assertTrue(validationResult);
	}
}
