package org.urbsat.utilities;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.values.IntValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.urbsat.Register;

public class ConvertIntoMultiPoint2DTest extends TestCase {
	private final static DataSourceFactory dsf = new DataSourceFactory();
	private final static String dsName = "testName";
	private final static File file = new File(
			"../../datas2tests/shp/smallshape2D/points.shp");
	static {
		try {
			new Register().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEvaluate() throws SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, DriverException {
		dsf.getSourceManager().register(dsName, file);

		final DataSource resultDs = dsf
				.executeSQL("select id,AsWKT(the_geom),AsWKT(ConvertIntoMultiPoint2D(the_geom)) from "
						+ dsName + ";");
		resultDs.open();
		final long rowCount = resultDs.getRowCount();
		final int fieldCount = resultDs.getFieldCount();

		assertTrue(4 == rowCount);
		assertTrue(3 == fieldCount);

		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Value[] fields = resultDs.getRow(rowIndex);

			assertTrue(rowIndex + 1 == ((IntValue) fields[0]).intValue());
			assertTrue(fields[1].toString().equals(fields[2].toString()));
			
			for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
				System.out.print(fields[fieldIndex].toString() + ", ");
			}
			System.out.println();
		}
	}
}