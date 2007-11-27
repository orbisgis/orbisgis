package org.orbisgis;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for orbisgis");
		// $JUnit-BEGIN$
		suite.addTestSuite(LayerAndResourceManagementTest.class);
		suite.addTestSuite(LayerAndResourceManagementTest2.class);
		suite.addTestSuite(TocTest.class);
		suite.addTestSuite(InfoToolTest.class);
		suite.addTestSuite(SQLConsoleTest.class);

		return suite;
	}
}
