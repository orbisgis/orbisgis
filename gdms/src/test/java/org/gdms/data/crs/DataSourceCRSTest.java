package org.gdms.data.crs;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.BaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;

public class DataSourceCRSTest extends TestCase {

	private DataSourceFactory dsf;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
	}

	public void testSHPCRSWithPRJ() throws Exception {

		String crsName = "NTF_Lambert_II_étendu";
		File file = new File(BaseTest.internalData + "landcover2000.shp");
		DataSource ds = dsf.getDataSource(file);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		assertTrue(sds.getCRS().getName().equals(crsName));
		sds.close();

	}
	
	
	public void testASCIICRSWithPRJ() throws Exception {

		File file = new File(BaseTest.internalData + "sample.asc");
		DataSource ds = dsf.getDataSource(file);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		System.out.println(sds.getCRS().getName());
		sds.close();

	}
	
	public void testSHPCRSWithoutPRJ() throws Exception {

		File file = new File(BaseTest.internalData + "hedgerow.shp");
		DataSource ds = dsf.getDataSource(file);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		assertTrue(sds.getCRS().getName().equals("Unknow CRS"));
		sds.close();

	}
}
