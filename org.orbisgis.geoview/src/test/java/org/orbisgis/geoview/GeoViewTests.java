package org.orbisgis.geoview;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.PropertyConfigurator;
import org.orbisgis.geoview.layerModel.LayerModelTest;
import org.orbisgis.geoview.tools.ExtentToolTest;
import org.orbisgis.geoview.tools.SelectionTest;

public class GeoViewTests extends TestCase {

	public static Test suite() {

		PropertyConfigurator.configure(GeoViewTests.class
				.getResource("/org/orbisgis/log4j.properties"));

		TestSuite suite = new TestSuite("Test for orbisgis");
		// $JUnit-BEGIN$
		suite.addTestSuite(ExtentToolTest.class);
		suite.addTestSuite(SelectionTest.class);
		suite.addTestSuite(LayerModelTest.class);
		// $JUnit-END$
		return suite;
	}

}
