package org.orbisgis;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.orbisgis.layerModel.LayerModelTest;
import org.orbisgis.renderer.ClassificationTest;
import org.orbisgis.renderer.LiteShapeTest;

public class AllTests extends TestCase {

	public static Test suite() {

		TestSuite suite = new TestSuite("Test for orbisgis");
		// $JUnit-BEGIN$
		suite.addTestSuite(LayerModelTest.class);
		suite.addTestSuite(LiteShapeTest.class);
		suite.addTestSuite(FormatTest.class);
		suite.addTestSuite(ClassificationTest.class);
		suite.addTestSuite(MapContextTest.class);
		// $JUnit-END$
		return suite;
	}

}
