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
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.driverManager.DriverLoadException;

public class ShapefileDriverTest extends TestCase {
	private DataSourceFactory dsf = new DataSourceFactory();

	public void testSaveSQL() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.registerDataSource("shape", new FileSourceDefinition(
				new File(SourceTest.externalData
						+ "shp/mediumshape2D/landcover2000.shp")));

		DataSource sql = dsf
				.executeSQL("select Buffer(the_geom, 20) from shape", DataSourceFactory.DEFAULT);
		DataSourceDefinition target = new FileSourceDefinition(new File(
				SourceTest.backupDir, "outputtestSaveSQL.shp"));
		dsf.registerContents("buffer", target, sql);

		DataSource ds = dsf.getDataSource("buffer");
		ds.open();
		sql.open();
		assertTrue(ds.getRowCount() == sql.getRowCount());
		sql.cancel();
		ds.cancel();
	}

	public void testSaveHeterogeneousGeometries() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] { "id",
				"geom" }, new Type[] { TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.GEOMETRY) });
		dsf.registerDataSource("obj", new ObjectSourceDefinition(omd));
		DataSource ds = dsf.getDataSource("obj");
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("0"),
				ValueFactory.createValue(Geometries.getPoint()), });
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("1"),
				ValueFactory.createValue(Geometries.getPolygon()), });
		DataSourceDefinition target = new FileSourceDefinition(new File(
				SourceTest.backupDir, "outputtestSaveHeterogeneousGeometries.shp"));
		try {
			dsf.registerContents("buffer", target, ds);
			assertTrue(false);
		} catch (DriverException e) {
		}
		ds.cancel();
	}

	// SEE THE GT BUG REPORT :
	// http://jira.codehaus.org/browse/GEOT-1268

	private boolean crsConformity(final String fileName,
			final CoordinateReferenceSystem refCrs) throws DriverLoadException,
			DataSourceCreationException, DriverException {
		DataSource ds = dsf.getDataSource(new File(fileName));
		SpatialDataSource sds = new SpatialDataSourceDecorator(ds);
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