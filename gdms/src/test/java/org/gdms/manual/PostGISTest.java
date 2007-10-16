package org.gdms.manual;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;

public class PostGISTest extends TestCase {

	public synchronized void testConnection() throws Exception {
		DBSource dbSource = new DBSource("127.0.0.1", 5432, "gdms", "postgres",
				"postgres", "communes", "jdbc:postgresql");
		DataSourceFactory dsf = new DataSourceFactory();
		DataSource ds = dsf.getDataSource(dbSource);
//		DataSource ds = dsf.getDataSource(new File(SourceTest.externalData
//				+ "shp/bigshape2D/communes.shp"), DataSourceFactory.NORMAL);
		ds.open();
		int geomFieldId = ds.getFieldIndexByName("the_geom");
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < ds.getRowCount(); i++) {
			ds.getFieldValue(i, geomFieldId);
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Tiempo: " + ((t2 - t1) / 1000.0));
		ds.cancel();
	}
}
