package org.gdms.drivers;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.BaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.source.SourceManager;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

import com.vividsolutions.jts.geom.Envelope;

public class RasterTest extends TestCase {

	private DataSourceFactory dsf;

	public void testProducedRasterEnvelope() throws Exception {
		DataSource ds = dsf.getDataSource("raster");
		ds.open();
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		Envelope env = sds.getFullExtent();
		assertTrue(env.getWidth() > 0);
		assertTrue(env.getHeight() > 0);
		ds.cancel();
	}

	public void testSQLResultSourceType() throws Exception {
		int type = dsf.getSourceManager().getSource("raster").getType();
		assertTrue((type & SourceManager.RASTER) > 0);
	}

	public void setUp() throws Exception {
		byte[] rasterData = new byte[4];
		RasterMetadata rasterMetadata = new RasterMetadata(0, 0, 1, 1, 2, 2);
		GeoRaster gr = GeoRasterFactory.createGeoRaster(rasterData,
				rasterMetadata);

		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		DefaultMetadata metadata = new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });
		ObjectMemoryDriver omd = new ObjectMemoryDriver(metadata);
		omd.addValues(new Value[] { ValueFactory.createValue(gr) });
		dsf.getSourceManager().register("raster", omd);
	}

	public void testOpenJPG() throws Exception {
		File file = new File("src/test/resources/sample.jpg");
		testOpen(file);
	}

	public void testOpenPNG() throws Exception {
		File file = new File("src/test/resources/sample.png");
		testOpen(file);
	}

	public void testOpenASC() throws Exception {
		File file = new File(BaseTest.externalData + "/grid/sample.asc");
		testOpen(file);
	}

	public void testOpenTIFF() throws Exception {
		File file = new File(BaseTest.externalData
				+ "/geotif/littlelehavre.tif");
		testOpen(file);
	}

	private void testOpen(File file) throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(file.getAbsolutePath());
		gr.open();
		int rasterType = gr.getType();
		DataSource ds = dsf.getDataSource(file);
		ds.open();
		Metadata metadata = ds.getMetadata();
		Type fieldType = metadata.getFieldType(0);
		assertTrue(fieldType.getIntConstraint(Constraint.RASTER_TYPE) == rasterType);
		ds.getFieldValue(0, 0);
		ds.cancel();
	}
}
