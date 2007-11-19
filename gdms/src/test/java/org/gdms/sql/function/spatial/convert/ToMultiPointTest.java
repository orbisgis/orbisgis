package org.gdms.sql.function.spatial.convert;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.values.IntValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.SpatialConvertCommonTools;

public class ToMultiPointTest extends SpatialConvertCommonTools {
	// private final static DataSourceFactory dsf = new DataSourceFactory();
	private final static String dsName = "testName";
	private final static File file = new File(
			"../../datas2tests/shp/smallshape2D/points.shp");

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEvaluate1() throws SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, DriverException {
		final DataSource resultDs = dsf
				.executeSQL("select pk+GeometryN(ToMultiPoint(geom)),ToMultiPoint(geom) from ds1;");
		resultDs.open();

		final long rowCount = resultDs.getRowCount();
		final int fieldCount = resultDs.getFieldCount();

		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Value[] fields = resultDs.getRow(rowIndex);
			assertTrue(9 == ((IntValue) fields[0]).intValue());
//			assertTrue( ((GeometryValue) fields[1]).getGeom()
			//
			// assertTrue(rowIndex + 1 == ((IntValue) fields[0]).intValue());
			// assertTrue(fields[1].toString().equals(fields[2].toString()));

			for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
				System.out.print(fields[fieldIndex].toString() + ", ");
			}
			System.out.println();
		}

		resultDs.cancel();
	}

	public void testEvaluate() throws SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, DriverException {
		dsf.getSourceManager().register(dsName, file);

		final DataSource resultDs = dsf
				.executeSQL("select id,AsWKT(the_geom),AsWKT(ToMultiPoint(the_geom)) from "
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