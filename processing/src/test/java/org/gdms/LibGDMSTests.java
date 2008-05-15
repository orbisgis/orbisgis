package org.gdms;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.gdms.sql.customQuery.geometry.raster.convert.RasterToPointsTest;
import org.gdms.sql.customQuery.geometry.raster.convert.RasterToPolygonsTest;
import org.gdms.sql.function.spatial.geometry.ExtractTest;
import org.gdms.triangulation.sweepLine4CDT.SweepLineTest;

public class LibGDMSTests extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for lib-gdms");
		// $JUnit-BEGIN$
		
		suite.addTestSuite(RasterToPointsTest.class);
		suite.addTestSuite(RasterToPolygonsTest.class);
		
		suite.addTestSuite(ExtractTest.class);
		suite.addTestSuite(SweepLineTest.class);
		// $JUnit-END$
		return suite;
	}
}