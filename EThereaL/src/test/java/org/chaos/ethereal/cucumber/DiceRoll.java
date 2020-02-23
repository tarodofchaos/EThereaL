package org.chaos.ethereal.cucumber;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DiceRoll {

	static class DiceRollTest {
		static Integer rollTheDice(String dice) {
			Integer result = 0;
			
			Random r = new Random();
			try {
				diceNumber = Integer.valueOf(dice.substring(0,dice.indexOf("d")));
				if (dice.contains("+")) {
					dieSize = Integer.valueOf(dice.substring(dice.indexOf("d")+1, dice.indexOf("+")));
					modifier = Integer.valueOf(dice.substring(dice.indexOf("+")+1));
				} else {
					dieSize = Integer.valueOf(dice.substring(dice.indexOf("d")+1));
					modifier = 0;
				}
				
				for (int i = 0; i < diceNumber; i++) {
					result += (r.nextInt(dieSize  + 1));
				}
				result += modifier;
			} catch (NumberFormatException e) {
				return -1;
			}
			return result;
		}
	}

	String diceRoll;
	String bonus;
	Integer actualDiceRoll;
	static Integer diceNumber;
	static Integer dieSize;
	static Integer modifier;
	
	@Given("I roll three 4 sided dice")
	public void i_want_to_roll_three_sided_dice() {
		diceRoll = "3d4";
	}

	@Given("a bonus of 2")
	public void a_bonus_of() {
	   bonus = "+2";
	}

	@When("the dice roll")
	public void the_dice_roll() {
	   actualDiceRoll = DiceRollTest.rollTheDice(diceRoll+bonus);
	}

	@Then("value must be between 5 and 14")
	public void value_must_be_between_and() {
		Assert.assertTrue(actualDiceRoll >= diceNumber+modifier && actualDiceRoll <= (diceNumber*dieSize)+modifier);
	}
	
	@Given("I roll an invalid die")
	public void i_roll_an_invalid_die() {
		diceRoll = "ad4";
	}
	
	@Then("value must be -1")
	public void value_must_be() {
		assertTrue(actualDiceRoll == -1);
	}
}
