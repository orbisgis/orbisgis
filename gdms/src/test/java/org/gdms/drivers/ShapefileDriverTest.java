package org.gdms.drivers;

import java.io.File;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.hardcode.driverManager.DriverLoadException;

import junit.framework.TestCase;

public class ShapefileDriverTest extends TestCase {
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

	public void testPrj() {
		DataSourceFactory dsf = new DataSourceFactory();

		String withoutExistingPrj = SourceTest.externalData
				+ "shp/mediumshape2D/landcover2000.shp";
		try {
			DataSource ds = dsf.getDataSource(new File(withoutExistingPrj));
			SpatialDataSource sds = new SpatialDataSourceDecorator(ds);
			sds.beginTrans();
			assertTrue(sds.getCRS(null).toWKT().equals(
					DefaultGeographicCRS.WGS84.toWKT()));
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			e.printStackTrace();
		} catch (DriverException e) {
			e.printStackTrace();
		}

		String withExistingPrj = SourceTest.externalData
				+ "shp/mediumshape2D/bzh5_communes.shp";
		try {
			DataSource ds = dsf.getDataSource(new File(withExistingPrj));
			SpatialDataSource sds = new SpatialDataSourceDecorator(ds);
			sds.beginTrans();
			System.out.println(sds.getCRS(null).toWKT());
			System.out.println("____________________________");
			System.out.println(CRS.decode("EPSG:27582").toWKT());
			assertTrue(sds.getCRS(null).toWKT().equals(
					CRS.decode("EPSG:27582").toWKT()));
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			e.printStackTrace();
		} catch (DriverException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}
	}
}