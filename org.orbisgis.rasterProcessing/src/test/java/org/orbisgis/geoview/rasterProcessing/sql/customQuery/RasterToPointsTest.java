package org.orbisgis.geoview.rasterProcessing.sql.customQuery;

import junit.framework.TestCase;

import org.gdms.sql.customQuery.QueryManager;

public class RasterToPointsTest extends TestCase {
	static {
		QueryManager.registerQuery(new RasterToPoints());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEvaluate() {
	}

	public void testValidateTables() {
	}

	public void testValidateTypes() {
	}
}