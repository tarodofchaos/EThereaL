package org.chaos.ethereal;

import org.chaos.ethereal.helper.SequenceHelper;
import org.junit.Before;
import org.junit.Test;

public class SequenceTest {
	
	@Before
	public void setUp() {
		
	}

	@Test
	public void getSequence() throws Exception {
		Integer i = SequenceHelper.getNewSeq("ethereal_battle_report");
		System.out.println(i);
	}
	
}
