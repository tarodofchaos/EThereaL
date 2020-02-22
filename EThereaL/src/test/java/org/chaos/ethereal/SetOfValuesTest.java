package org.chaos.ethereal;

import java.util.List;

import org.chaos.ethereal.helper.SetOfValuesHelper;
import org.chaos.ethereal.persistence.SetOfValues;
import org.junit.Before;
import org.junit.Test;

public class SetOfValuesTest {
	
	@Before
	public void setUp() {
		
	}

	@Test
	public void getSequence() {
		SetOfValuesHelper helper = new SetOfValuesHelper();
		TestContext ctx = new TestContext();
		List<SetOfValues> result = helper.retrieveAllPossibleValues(ctx.getLogger());
		System.out.println("");
	}
	
}
