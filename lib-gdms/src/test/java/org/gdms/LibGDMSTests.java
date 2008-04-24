package org.gdms;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.gdms.sql.function.spatial.ExtractTest;
import org.gdms.triangulation.sweepLine4CDT.SweepLineTest;

public class LibGDMSTests extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for lib-gdms");
		// $JUnit-BEGIN$
		suite.addTestSuite(ExtractTest.class);
		suite.addTestSuite(SweepLineTest.class);
		// $JUnit-END$
		return suite;
	}
}