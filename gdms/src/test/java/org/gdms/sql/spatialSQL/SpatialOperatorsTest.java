package org.gdms.sql.spatialSQL;

import org.gdms.SourceTest;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;

public class SpatialOperatorsTest extends SourceTest {
	private void testBuffer(String dsName) throws Exception {
		String sqlQuery = "select Buffer(" + dsName + ".the_geom,20) from "
				+ dsName + ";";

		SpatialDataSource spatialds = new SpatialDataSourceDecorator(dsf
				.executeSQL(sqlQuery));

		spatialds.beginTrans();

		System.out.println(spatialds.getGeometry(1).toString());

		spatialds.rollBackTrans();

	}

	public void testBuffer() throws Exception {
		String[] resources = super.getSpatialResources();
		for (String ds : resources) {
			testBuffer(ds);
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setWritingTests(false);
		super.setUp();
	}
}