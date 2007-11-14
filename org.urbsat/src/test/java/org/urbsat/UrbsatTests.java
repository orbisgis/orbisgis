package org.urbsat;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.urbsat.utilities.ConvertIntoMultiPoint2DTest;

public class UrbsatTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.urbsat");
		// $JUnit-BEGIN$
		suite.addTestSuite(ConvertIntoMultiPoint2DTest.class);
		// $JUnit-END$
		return suite;
	}
}