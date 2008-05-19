package org.gdms.drivers;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;

public class GDMSDriverTest extends TestCase {

	private DataSourceFactory dsf;

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory("src/test/resources/backup/sources",
				"src/test/resources/backup");
	}

	public void testSaveASGDMS() throws Exception {
		File gdmsFile = new File("src/test/resources/backup/saveAsGDMS.gdms");
		dsf.getSourceManager().register("gdms",
				gdmsFile);
		DataSource ds = dsf.getDataSource(new File(SourceTest.externalData
				+ "shp/mediumshape2D/landcover2000.shp"));
		ds.open();
		dsf.saveContents("gdms", ds);
		ds.cancel();

		ds = dsf.getDataSource(gdmsFile);
		ds.open();
		ds.cancel();
	}
}
