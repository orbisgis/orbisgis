package org.orbisgis.views.geocognition.sync;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NodeTests extends TestCase {

	public static Test suite() {

		TestSuite suite = new TestSuite("Node tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(TreeCompareTest.class);
		suite.addTestSuite(TreeCommitTest.class);
		suite.addTestSuite(TreeUpdateTest.class);
		// $JUnit-END$
		return suite;
	}

}
