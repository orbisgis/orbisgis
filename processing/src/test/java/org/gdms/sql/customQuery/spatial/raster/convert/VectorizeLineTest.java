package org.gdms.sql.customQuery.spatial.raster.convert;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.TableDefinition;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.io.WKTReader;

public class VectorizeLineTest extends TestCase {
	private static final DataSourceFactory dsf = new DataSourceFactory();
	private MultiLineString multiLineString;

	static {
		QueryManager.registerQuery(VectorizeLine.class);
	}

	protected void setUp() throws Exception {
		super.setUp();

		final RasterMetadata rasterMetadata = new RasterMetadata(0.5, 9.5, 1,
				-1, 10, 10);
		final Metadata metadata = new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "Raster" });

		byte n = Byte.MAX_VALUE;
		final GeoRaster gr1 = GeoRasterFactory.createGeoRaster(new byte[] { //
				n, n, n, n, n, n, n, n, n, n,//
						n, 1, n, n, n, n, 1, n, n, n,//
						n, n, 1, n, n, n, 1, n, n, n,//
						n, n, 1, n, n, n, 1, n, n, n,//
						n, n, 1, n, n, n, 1, n, n, n,//
						n, n, n, 1, n, 1, n, n, n, n,//
						n, n, n, n, 1, n, n, n, n, n,//
						n, n, n, n, n, n, n, n, n, n,//
						n, n, n, n, n, n, n, n, n, n,//
						n, n, n, n, n, n, n, n, n, n, //
				}, rasterMetadata);
		gr1.open();
		gr1.setNodataValue(n);
		ObjectMemoryDriver driver1 = new ObjectMemoryDriver(metadata);
		driver1.addValues(new Value[] { ValueFactory.createValue(gr1) });
		dsf.getSourceManager().register("insds1", driver1);

		short m = Short.MAX_VALUE;
		final GeoRaster gr2 = GeoRasterFactory.createGeoRaster(new short[] { //
				m, m, m, m, m, m, m, m, m, m,//
						m, 1, m, m, m, m, 1, m, m, m,//
						m, m, 1, m, m, m, 1, m, m, m,//
						m, m, 1, m, m, m, 1, m, m, m,//
						m, m, 1, m, m, m, 1, m, m, m,//
						m, m, m, 1, m, 1, m, m, m, m,//
						m, m, m, m, 1, m, m, m, m, m,//
						m, m, m, m, m, m, m, m, m, m,//
						m, m, m, m, m, m, m, m, m, m,//
						m, m, m, m, m, m, m, m, m, m, //
				}, rasterMetadata);
		gr2.open();
		gr2.setNodataValue(m);
		ObjectMemoryDriver driver2 = new ObjectMemoryDriver(metadata);
		driver2.addValues(new Value[] { ValueFactory.createValue(gr2) });
		dsf.getSourceManager().register("insds2", driver2);

		float p = Float.MAX_VALUE;
		final GeoRaster gr3 = GeoRasterFactory.createGeoRaster(new float[] { //
				p, p, p, p, p, p, p, p, p, p,//
						p, 1, p, p, p, p, 1, p, p, p,//
						p, p, 1, p, p, p, 1, p, p, p,//
						p, p, 1, p, p, p, 1, p, p, p,//
						p, p, 1, p, p, p, 1, p, p, p,//
						p, p, p, 1, p, 1, p, p, p, p,//
						p, p, p, p, 1, p, p, p, p, p,//
						p, p, p, p, p, p, p, p, p, p,//
						p, p, p, p, p, p, p, p, p, p,//
						p, p, p, p, p, p, p, p, p, p, //
				}, rasterMetadata);
		gr3.open();
		gr3.setNodataValue(p);
		ObjectMemoryDriver driver3 = new ObjectMemoryDriver(metadata);
		driver3.addValues(new Value[] { ValueFactory.createValue(gr3) });
		dsf.getSourceManager().register("insds3", driver3);

		multiLineString = (MultiLineString) new WKTReader()
				.read("MULTILINESTRING ((1.5 8.5, 2.5 7.5, 2.5 5.5, 4.5 3.5, 6.5 5.5, 6.5 8.5))");
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		dsf.remove("outsds");
		dsf.remove("insds1");
		dsf.remove("insds2");
		dsf.remove("insds3");
	}

	public void testEvaluate() throws Exception {
		testEval("insds1");
		testEval("insds2");
		testEval("insds3");
	}

	private void testEval(String insds) throws Exception {
		dsf.getSourceManager().register("outsds",
				"select VectorizeLine() from " + insds);
		final SpatialDataSourceDecorator output = new SpatialDataSourceDecorator(
				dsf.getDataSource("outsds"));
		output.open();
		assertTrue(multiLineString.equalsExact(output.getGeometry(0)));
		assertEquals(output.getFieldValue(0, 0).getAsDouble(), 1d);
		// TODO : try to uncomment following instruction !
		// assertEquals(output.getRowCount(), 1);
		output.close();
		dsf.getSourceManager().remove("outsds");
	}

	public void testGeTablesDefinitions() {
		assertEquals(new VectorizeLine().geTablesDefinitions().length, 1);
		assertEquals(new VectorizeLine().geTablesDefinitions()[0],
				TableDefinition.RASTER);
	}

	public void testGetFunctionArguments() {
		// TODO
	}

	public void testGetMetadata() throws DriverException {
		Metadata metadata = new VectorizeLine().getMetadata(null);

		assertEquals(metadata.getFieldCount(), 2);
		assertEquals(metadata.getFieldType(0).getTypeCode(), Type.DOUBLE);
		assertEquals(metadata.getFieldType(1).getTypeCode(), Type.GEOMETRY);
		assertEquals(metadata.getFieldType(1).getIntConstraint(
				Constraint.GEOMETRY_TYPE), GeometryConstraint.MULTI_LINESTRING);
		assertEquals(metadata.getFieldType(1).getIntConstraint(
				Constraint.GEOMETRY_DIMENSION), 2);
	}

	public void testGetName() {
		assertEquals(new VectorizeLine().getName(), "VectorizeLine");
	}
}