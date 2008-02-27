package org.orbisgis.geoview.rasterProcessing;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.orbisgis.geoview.rasterProcessing.sql.customQuery.RasterToPointsTest;
import org.orbisgis.geoview.rasterProcessing.sql.customQuery.RasterToPolygonsTest;

public class RasterProcessingTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.orbisgis.geoview.rasterProcessing");
		// $JUnit-BEGIN$
		suite.addTestSuite(RasterToPointsTest.class);
		suite.addTestSuite(RasterToPolygonsTest.class);
		// $JUnit-END$
		return suite;
	}
}