package org.chaos.ethereal.cucumber;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.chaos.ethereal.TestContext;
import org.chaos.ethereal.helper.ArmyHelper;
import org.chaos.ethereal.persistence.Army;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ArmyCreator {

	static class ArmyCreatorTest {
		static ArmyHelper helper = new ArmyHelper(new TestContext().getLogger());
		
		static Army generateAnArmy(Integer monstersSize, Integer heroesSize) throws Exception {
			return helper.createArmy(monstersSize, heroesSize);
		}
	}

	Integer heroArmySize;
	Integer monsterArmySize;
	Army army;
	String actualErrorMessage;
	
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
	
}
