package org.orbisgis.geoview.rasterProcessing.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.sql.customQuery.QueryManager;
import org.orbisgis.geoview.rasterProcessing.AbstractRasterProcessingTest;

import com.vividsolutions.jts.geom.Point;

public class RasterToPointsTest extends AbstractRasterProcessingTest {
	static {
		QueryManager.registerQuery(new RasterToPoints());
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		dsf.remove("outDs");
	}

	public void testEvaluate() throws Exception {
		dsf.getSourceManager().register("outDs",
				"select RasterToPoints('inGr');");
		final DataSource ds = dsf.getDataSource("outDs");
		// ClassCastException : why ?
		// SpatialDataSourceDecorator sds = (SpatialDataSourceDecorator) ds;

		ds.open();
		final long rowCount = ds.getRowCount();
		final int fieldCount = ds.getFieldCount();
		assertTrue(3 == fieldCount);
		assertTrue(geoRaster.getWidth() * geoRaster.getHeight() == rowCount);

		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final int id = ds.getFieldValue(rowIndex, 0).getAsInt();
			final Point point = (Point) ds.getFieldValue(rowIndex, 1)
					.getAsGeometry();
			final double height = ds.getFieldValue(rowIndex, 2).getAsDouble();

			assertTrue(rowIndex == id);
			assertTrue(floatingPointNumbersEquality(pixels[rowIndex], height));
			assertTrue(floatingPointNumbersEquality(point.getX(), xUlcorner
					+ pixelSize_X * (rowIndex % geoRaster.getWidth())));
			assertTrue(floatingPointNumbersEquality(point.getY(), yUlcorner
					+ pixelSize_Y * (rowIndex / geoRaster.getHeight())));
		}
		ds.cancel();

		print(ds);
	}
}