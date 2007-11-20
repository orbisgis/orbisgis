package org.orbisgis;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for orbisgis");
		// $JUnit-BEGIN$
		suite.addTestSuite(LayerAndResourceManagementTest.class);

		return suite;
	}
}
