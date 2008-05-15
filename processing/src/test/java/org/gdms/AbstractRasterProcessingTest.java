package org.gdms;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.asc.AscDriver;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

abstract public class AbstractRasterProcessingTest extends TestCase {
	private static final double EPSILON = 1.0E-7;

	public static DataSourceFactory dsf = new DataSourceFactory();

	public static GeoRaster geoRaster;
	public static float[] pixels;
	public static float pixelSize_X;
	public static float pixelSize_Y;
	public static double xUlcorner;
	public static double yUlcorner;

	public final static String geoRasterPath = AbstractRasterProcessingTest.class
			.getResource("4x3.asc").getFile();

	static {
		dsf.getSourceManager().getDriverManager().registerDriver("asc driver",
				AscDriver.class);
		dsf.getSourceManager().register("inGr", new File(geoRasterPath));

		try {
			geoRaster = GeoRasterFactory.createGeoRaster(geoRasterPath);
			pixels = (float[]) geoRaster.getGrapImagePlus().getPixels();
			geoRaster.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pixelSize_X = geoRaster.getMetadata().getPixelSize_X();
		pixelSize_Y = geoRaster.getMetadata().getPixelSize_Y();
		xUlcorner = geoRaster.getMetadata().getXulcorner();
		yUlcorner = geoRaster.getMetadata().getYulcorner();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public boolean floatingPointNumbersEquality(final double a, final double b) {
		if (Double.isNaN(a)) {
			return Double.isNaN(b);
		} else {
			return Math.abs(a - b) < EPSILON;
		}
	}

	public void print(final DataSource ds) throws DriverException {
		ds.open();
		final long rowCount = ds.getRowCount();
		final int fieldCount = ds.getFieldCount();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			for (int fieldId = 0; fieldId < fieldCount; fieldId++) {
				final Value fieldValue = ds.getFieldValue(rowIndex, fieldId);
				System.out.printf("%s = %s ", fieldValue.getClass()
						.getSimpleName(), fieldValue.toString());
			}
			System.out.println();
		}
		ds.cancel();
	}
}