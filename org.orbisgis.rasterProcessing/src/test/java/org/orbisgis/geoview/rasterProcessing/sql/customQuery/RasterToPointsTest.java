package org.orbisgis.geoview.rasterProcessing.sql.customQuery;

import org.gdms.data.SpatialDataSourceDecorator;
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
		final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
				dsf.getDataSource("outDs"));

		sds.open();
		final long rowCount = sds.getRowCount();
		final int fieldCount = sds.getFieldCount();
		assertTrue(3 == fieldCount);
		assertTrue(geoRaster.getWidth() * geoRaster.getHeight() >= rowCount);
		assertTrue(9 == rowCount);

		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final int id = sds.getFieldValue(rowIndex, 0).getAsInt();
			final Point point = (Point) sds.getGeometry(rowIndex);
			final float height = sds.getFieldValue(rowIndex, 2).getAsFloat();

			final int c = (int) Math.round((point.getX() - xUlcorner)
					/ pixelSize_X);
			final int r = (int) Math.round((point.getY() - yUlcorner)
					/ pixelSize_Y);
			final int i = r * geoRaster.getWidth() + c;

			assertTrue(rowIndex <= id);
			assertTrue(id == i);
			assertTrue(floatingPointNumbersEquality(pixels[i], height));
		}
		sds.cancel();

		print(sds);
	}
}