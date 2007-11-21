package org.urbsat.utilities;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SQLSourceDefinition;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.spatial.GeometryValue;
import org.urbsat.UrbsatTestsCommonTools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;

public class CreateGridTest extends UrbsatTestsCommonTools {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		if (dsf.getSourceManager().exists("ds1p")) {
			dsf.getSourceManager().remove("ds1p");
		}
		super.tearDown();
	}

	public final void testEvaluate() throws DriverLoadException,
			NoSuchTableException, DataSourceCreationException, DriverException {
		// dsf.getSourceManager()
		// .register(
		// "ds1p",
		// new SQLSourceDefinition(
		// "select CREATEGRID(0.5, 0.5) from ds1;"));
		dsf.getSourceManager().register("ds1p",
				new SQLSourceDefinition("select * from ds1;"));
		final DataSource dataSource = dsf.getDataSource("ds1p");

		dataSource.open();
		final long rowCount = dataSource.getRowCount();
		final int fieldCount = dataSource.getFieldCount();
		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Value[] fields = dataSource.getRow(rowIndex);
			for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
				System.out.print(fields[fieldIndex].toString() + ", ");
			}
			System.out.println();
		}
		dataSource.cancel();
	}
}