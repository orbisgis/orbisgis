package org.urbsat;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.urbsat.utilities.CreateGridTest;

public class UrbsatTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for org.urbsat");
		// $JUnit-BEGIN$
		 suite.addTestSuite(CreateGridTest.class);
		// $JUnit-END$
		return suite;
	}
}