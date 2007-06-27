package org.gdms.sql.strategies;

import java.io.File;
import java.io.IOException;

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
		DataSource ret = dsf.executeSQL("call register ('" + path + "', 'myshape');");
		assertTrue(ret == null);
		ret = dsf.getDataSource("myshape");
		assertTrue(ret != null);
	}

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory();
	}
}
