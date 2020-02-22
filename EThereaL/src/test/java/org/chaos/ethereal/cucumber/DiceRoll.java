package org.chaos.ethereal.cucumber;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DiceRoll {

	static class DiceRollTest {
		static Integer rollTheDice(String dice, Integer bonus) {
			return 6;
		}
	}

	String diceRoll;
	Integer bonus;
	Integer actualDiceRoll;
	Integer actualBonus;
	
	@Given("I roll three 4 sided dice")
	public void i_want_to_roll_three_sided_dice() {
		diceRoll = "3d4";
	}

	@Given("a bonus of 2")
	public void a_bonus_of() {
	   bonus = 2;
	}

	@When("the dice roll")
	public void the_dice_roll() {
	   actualDiceRoll = DiceRollTest.rollTheDice(diceRoll, bonus);
	}

	@Then("value must be between 5 and 14")
	public void value_must_be_between_and() {
	    List<Integer> resultList= Arrays.asList(new Integer[] {5,6,7,8,9,10,11,12,13,14});
		assertTrue(resultList.contains(actualDiceRoll));
	}
}
