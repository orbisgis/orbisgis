package org.urbsat.function;

import junit.framework.TestCase;

public class BuildByCellTest extends TestCase {

	public void testGetName() {
		assertTrue(new BuildByCell().getName().equals("BuildByCell"));
	}

	public void testIsAggregate() {
		assertEquals(new BuildByCell().isAggregate(),true );
	}
	
	public void testGetType() {
		//assertTrue (new BuildByCell().g)
	}

}
