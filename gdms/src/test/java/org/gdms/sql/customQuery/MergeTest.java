package org.gdms.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.SpatialConvertCommonTools;

public class MergeTest extends SpatialConvertCommonTools {
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		if (dsf.getSourceManager().exists("ds1p")) {
			dsf.getSourceManager().remove("ds1p");
		}
		super.tearDown();
	}

	public void testEvaluate1() throws SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, DriverException,
			DataSourceCreationException {
		dsf.getSourceManager().register("ds1p",
				"select Merge() from ds1,ds2,ds3;");
		final DataSource dataSource = dsf.getDataSource("ds1p");
		dataSource.open();
		final long rowCount = dataSource.getRowCount();
		final int fieldCount = dataSource.getFieldCount();
		assertTrue(9 == rowCount);
		assertTrue(2 == fieldCount);
		dataSource.cancel();
	}
}