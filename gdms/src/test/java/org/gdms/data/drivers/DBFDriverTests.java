package org.gdms.data.drivers;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverUtilities;

public class DBFDriverTests extends TestCase {
	public void testOpen() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		DriverUtilities.copy(new File("src/test/resources/puntos.backup.dbf"), new File("src/test/resources/puntos.dbf"));
		DataSource ads = dsf.getDataSource(new File("src/test/resources/puntos.dbf"));
		ads.open();
		ads.setDouble(0, 0, 3);
		ads.commit();
		ads.open();
		assertTrue(ads.getDouble(0, 0) == 3);
		ads.cancel();
	}
}
