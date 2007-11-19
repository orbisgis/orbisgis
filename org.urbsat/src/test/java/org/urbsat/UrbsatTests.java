package org.urbsat;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.urbsat.utilities.ToMultiPointTest;

public class UrbsatTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.urbsat");
		// $JUnit-BEGIN$
		suite.addTestSuite(ToMultiPointTest.class);
		// $JUnit-END$
		return suite;
	}
}