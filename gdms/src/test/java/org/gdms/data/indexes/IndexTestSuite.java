package org.gdms.data.indexes;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.gdms.spatial.SpatialEditionTest;

public class IndexTestSuite extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.gdms.engine.test");
		suite.addTestSuite(IndexesTest.class);
		suite.addTestSuite(SpatialEditionTest.class);

		suite.addTestSuite(IndexesTest.class);

		suite.addTestSuite(BTreeTest.class);
		suite.addTestSuite(RTreeTest.class);
		return suite;
	}
}
