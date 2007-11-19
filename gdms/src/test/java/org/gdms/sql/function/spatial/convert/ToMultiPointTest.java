package org.gdms.sql.function.spatial.convert;

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

import com.vividsolutions.jts.geom.MultiPoint;

public class ToMultiPointTest extends SpatialConvertCommonTools {
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEvaluate1() throws SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, DriverException {
		final DataSource resultDs = dsf
				.executeSQL("select pk + GeometryN(ToMultiPoint(geom)),ToMultiPoint(geom) from ds1;");
		resultDs.open();

		final long rowCount = resultDs.getRowCount();
		final int fieldCount = resultDs.getFieldCount();

		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Value[] fields = resultDs.getRow(rowIndex);
			assertTrue(9 == ((IntValue) fields[0]).intValue());
			assertTrue(((GeometryValue) fields[1]).getGeom() instanceof MultiPoint);

			for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
				System.out.print(fields[fieldIndex].toString() + ", ");
			}
			System.out.println();
		}

		resultDs.cancel();
	}

	public void testEvaluate2() throws SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, DriverException {
		final DataSource resultDs = dsf
				.executeSQL("select AsWKT(geom),AsWKT(ToMultiPoint(geom)) from ds2;");
		resultDs.open();

		final long rowCount = resultDs.getRowCount();
		final int fieldCount = resultDs.getFieldCount();

		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Value[] fields = resultDs.getRow(rowIndex);
			assertTrue(fields[0].toString().equals(fields[1].toString()));

			for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
				System.out.print(fields[fieldIndex].toString() + ", ");
			}
			System.out.println();
		}

		resultDs.cancel();
	}
}