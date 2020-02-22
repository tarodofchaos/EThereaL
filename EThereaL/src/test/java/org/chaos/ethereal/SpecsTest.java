package org.chaos.ethereal;

import java.util.List;

import org.chaos.ethereal.helper.SpecsHelper;
import org.chaos.ethereal.persistence.Specs;
import org.junit.Before;
import org.junit.Test;

public class SpecsTest {
	
	@Before
	public void setUp() {
		
	}

	@Test
	public void getSequence() {
		SpecsHelper helper = new SpecsHelper();
		TestContext ctx = new TestContext();
		List<Specs> specs = helper.retrieveAllFileSpecs(ctx.getLogger());
		System.out.println("");
	}
	
}
