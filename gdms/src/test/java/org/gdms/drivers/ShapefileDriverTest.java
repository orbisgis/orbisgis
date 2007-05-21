package org.gdms.drivers;

import java.io.File;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;

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
			DataSource ds1 = dsf.getDataSource(new File(withoutExistingPrj));
			SpatialDataSource sds1 = new SpatialDataSourceDecorator(ds1);
			System.out.println(sds1.getCRS(null).toWKT());
//			assertTrue(sds1.getCRS(null).toWKT().equals(?????));
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			e.printStackTrace();
		} catch (DriverException e) {
			e.printStackTrace();
		}

		String withExistingPrj = SourceTest.externalData
				+ "shp/bigshape2D/bzh5_communes.shp";

		System.out.println(withoutExistingPrj);
	}
}