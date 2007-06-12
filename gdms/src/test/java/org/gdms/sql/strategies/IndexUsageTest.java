package org.gdms.sql.strategies;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.spatial.io.GeomFromText;

public class IndexUsageTest extends TestCase {

	private DataSourceFactory dsf = new DataSourceFactory();

	public void testConstant() throws Exception {
		FunctionManager.addFunction(new GeomFromText());

		dsf.registerDataSource("cantons", new FileSourceDefinition(new File(
				SourceTest.externalData + "/shp/bigshape2D/communes.shp")));

		DataSource ds = dsf.getDataSource("cantons");
		ds.open();
		System.out.println(ds.getRowCount());
		ds.cancel();

		String sql = "select * from cantons where " + "Contains("
				+ "GeomFromText('POLYGON((" + "280000 2160000, "
				+ "458000 2160000, " + "458000 2300000, " + "280000 2300000, "
				+ "280000 2160000))'), " + "the_geom);";
		System.out.println(sql);
		ds = dsf.executeSQL(sql);
		ds.open();
		System.out.println(ds.getRowCount());
		ds.cancel();
	}

	public void testOrderOfFromTables() throws Exception {

	}

}
