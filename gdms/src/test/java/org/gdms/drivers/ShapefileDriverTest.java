package org.gdms.drivers;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.Geometries;
import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.NullCRS;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ShapefileDriverTest extends TestCase {
	private DataSourceFactory dsf = new DataSourceFactory();

	public void testBigShape() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.registerDataSource("big", new FileSourceDefinition(new File(
				SourceTest.externalData + "shp/bigshape3D/point3D.shp")));
		DataSource ds = dsf.getDataSource("big");
		ds.open();
		ds.cancel();
	}

	public void testSaveSQL() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.registerDataSource("shape", new FileSourceDefinition(
				new File(SourceTest.externalData
						+ "shp/mediumshape2D/landcover2000.shp")));

		DataSource sql = dsf.executeSQL(
				"select Buffer(the_geom, 20) from shape",
				DataSourceFactory.DEFAULT);
		DataSourceDefinition target = new FileSourceDefinition(new File(
				SourceTest.backupDir, "outputtestSaveSQL.shp"));
		dsf.registerDataSource("buffer", target);
		dsf.saveContents("buffer", sql);

		DataSource ds = dsf.getDataSource("buffer");
		ds.open();
		sql.open();
		assertTrue(ds.getRowCount() == sql.getRowCount());
		sql.cancel();
		ds.cancel();
	}

	public void testSaveEmptyGeometries() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] {
				"the_geom", "id" }, new Type[] {
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.STRING) });
		dsf.registerDataSource("obj", new ObjectSourceDefinition(omd));
		DataSource ds = dsf.getDataSource("obj");
		GeometryFactory gf = new GeometryFactory();
		ds.open();
		ds.insertFilledRow(new Value[] {
				ValueFactory.createValue(gf
						.createGeometryCollection(new Geometry[0])),
				ValueFactory.createValue("0") });
		DataSourceDefinition target = new FileSourceDefinition(new File(
				SourceTest.backupDir, "outputtestSaveEmptyGeometries.shp"));
		dsf.registerDataSource("buffer", target);
		dsf.saveContents("buffer", ds);
		String contents = ds.getAsString();
		ds.cancel();

		DataSource otherDs = dsf.getDataSource("buffer");
		otherDs.open();
		assertTrue(1 == otherDs.getRowCount());
		assertTrue(otherDs.isNull(0, 0));
		assertTrue(otherDs.getAsString().equals(contents));
		otherDs.cancel();
	}

	public void testSaveHeterogeneousGeometries() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] { "id",
				"geom" }, new Type[] { TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.GEOMETRY) });
		dsf.registerDataSource("obj", new ObjectSourceDefinition(omd));
		DataSourceDefinition target = new FileSourceDefinition(new File(
				SourceTest.backupDir,
				"outputtestSaveHeterogeneousGeometries.shp"));
		DataSource ds = dsf.getDataSource("obj");
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("1"),
				ValueFactory.createValue(Geometries.getPolygon()), });
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("0"),
				ValueFactory.createValue(Geometries.getPoint()), });
		try {
			dsf.registerDataSource("buffer", target);
			dsf.saveContents("buffer", ds);
			assertTrue(false);
		} catch (DriverException e) {
		}
		ds.cancel();
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("0"),
				ValueFactory.createValue(Geometries.getPoint()), });
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("1"),
				ValueFactory.createValue(Geometries.getPolygon()), });
		try {
			dsf.saveContents("buffer", ds);
			assertTrue(false);
		} catch (DriverException e) {
		}
		ds.cancel();
	}

	public void testSaveWrongType() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] { "id",
				"geom" }, new Type[] { TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.GEOMETRY) });
		dsf.registerDataSource("obj", new ObjectSourceDefinition(omd));
		DataSourceDefinition target = new FileSourceDefinition(new File(
				SourceTest.backupDir, "outputtestSaveWrongType.shp"));
		DataSource ds = dsf.getDataSource("obj");
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("1"),
				ValueFactory.createValue(Geometries.getPolygon()), });
		try {
			dsf.registerDataSource("buffer", target);
			dsf.saveContents("buffer", ds);
			assertTrue(false);
		} catch (DriverException e) {
			e.printStackTrace();
		}
		ds.cancel();
	}

	// SEE THE GT BUG REPORT :
	// http://jira.codehaus.org/browse/GEOT-1268

	private boolean crsConformity(final String fileName,
			final CoordinateReferenceSystem refCrs) throws DriverLoadException,
			DataSourceCreationException, DriverException {
		DataSource ds = dsf.getDataSource(new File(fileName));
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		return CRS.equalsIgnoreMetadata(refCrs, sds.getCRS(null));
		// && sds.getCRS(null).toWKT().equals(refCrs.toWKT());
	}

	public void testPrj() throws NoSuchAuthorityCodeException,
			FactoryException, DriverLoadException, DataSourceCreationException,
			DriverException {
		final String withoutExistingPrj = SourceTest.externalData
				+ "shp/mediumshape2D/landcover2000.shp";
		assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84,
				NullCRS.singleton));
		assertTrue(CRS.equalsIgnoreMetadata(NullCRS.singleton,
				DefaultGeographicCRS.WGS84));
		assertTrue(crsConformity(withoutExistingPrj, DefaultGeographicCRS.WGS84));
		// assertTrue(crsConformity(withoutExistingPrj,
		// CRS.decode("EPSG:4326")));

		final String withExistingPrj = SourceTest.externalData
				+ "shp/smallshape2D/bv_sap.shp";
		// assertTrue(crsConformity(withExistingPrj, CRS.decode("EPSG:27572")));
		assertTrue(crsConformity(withExistingPrj, CRS.decode("EPSG:27582")));
	}

}