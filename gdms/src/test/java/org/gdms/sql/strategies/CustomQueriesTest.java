package org.gdms.sql.strategies;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import junit.framework.TestCase;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.instruction.SemanticException;
import org.gdms.sql.parser.ParseException;

import com.hardcode.driverManager.DriverLoadException;

public class CustomQueriesTest extends TestCase {

	private DataSourceFactory dsf;

	/**
	 * DOCUMENT ME!
	 *
	 * @throws DriverLoadException
	 *             DOCUMENT ME!
	 * @throws ParseException
	 *             DOCUMENT ME!
	 * @throws DriverException
	 *             DOCUMENT ME!
	 * @throws SemanticException
	 *             DOCUMENT ME!
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void testCustomQuery() throws Exception {
		QueryManager.registerQuery(new SumQuery());

		dsf
				.registerDataSource("ds", new FileSourceDefinition(new File(
						SourceTest.externalData
								+ "shp/smallshape2D/multipoint2d.shp")));
		DataSource d = dsf.executeSQL("call sumquery from ds values ('gid');");

		d.open();
		d.cancel();
	}

	public void testRegister() throws Exception {
		String path = SourceTest.externalData
				+ "shp/smallshape2D/multipoint2d.shp";
		DataSource ret = dsf.executeSQL("call register ('" + path
				+ "', 'myshape');");
		assertTrue(ret == null);
		ret = dsf.getDataSource("myshape");
		assertTrue(ret != null);

		Class.forName("org.h2.Driver");
		Connection c = DriverManager.getConnection("jdbc:h2:"
				+ SourceTest.backupDir + File.separator + "h2db", "sa", "");
		Statement st = c.createStatement();
		st.execute("DROP TABLE h2table IF EXISTS");
		st.execute("CREATE TABLE h2table "
				+ "(id IDENTITY PRIMARY KEY, nom VARCHAR(10))");
		st.close();
		c.close();

		ret = dsf.executeSQL("call register "
				+ "('h2', '127.0.0.1', '0', 'h2db', '', '', "
				+ "'h2table', 'myh2table');");
		assertTrue(ret == null);
		ret = dsf.getDataSource("myh2table");
		assertTrue(ret != null);

		dsf.executeSQL("call register ('memory')");
		dsf.executeSQL("create table memory as select * from myshape");
		DataSource ds1 = dsf.getDataSource("myshape");
		DataSource ds2 = dsf.getDataSource("memory");
		ds1.open();
		ds2.open();
		assertTrue(ds1.getAsString().equals(ds2.getAsString()));
		ds1.cancel();
		ds2.cancel();
	}

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory();
	}
}
