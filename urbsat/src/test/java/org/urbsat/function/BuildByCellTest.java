package org.urbsat.function;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.sql.function.FunctionException;

import junit.framework.TestCase;

public class BuildByCellTest extends TestCase {
	
	public void testGetName() {
		assertTrue(new BuildByCell().getName().equals("BuildByCell"));
	}

	public void testIsAggregate() {
		assertEquals(new BuildByCell().isAggregate(),true );
	}

	public void testGetType() { 
		assertEquals(new BuildByCell().getType(new int[0]),Type.DOUBLE);
	}
	
	public void testEvaluate() throws FunctionException {
		assertTrue(new BuildByCell().evaluate(new Value[1]).equals("d"));
	}
	
}
