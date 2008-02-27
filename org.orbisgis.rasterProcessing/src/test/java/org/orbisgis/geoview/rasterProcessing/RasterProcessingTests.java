package org.orbisgis.geoview.rasterProcessing;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.orbisgis.geoview.rasterProcessing.sql.customQuery.RasterToPointsTest;

public class RasterProcessingTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.orbisgis.geoview.rasterProcessing");
		//$JUnit-BEGIN$
		suite.addTestSuite(RasterToPointsTest.class);
		//$JUnit-END$
		return suite;
	}

}
