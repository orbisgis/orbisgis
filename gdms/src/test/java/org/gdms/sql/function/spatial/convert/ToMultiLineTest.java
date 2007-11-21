package org.gdms.sql.function.spatial.convert;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SQLSourceDefinition;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.SpatialConvertCommonTools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;

public class ToMultiLineTest extends SpatialConvertCommonTools {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		if (dsf.getSourceManager().exists("ds3p")) {
			dsf.getSourceManager().remove("ds3p");
		}
		super.tearDown();
	}

	public final void testEvaluate() throws DriverLoadException,
			NoSuchTableException, DataSourceCreationException, DriverException {
		dsf.getSourceManager().register(
				"ds3p",
				new SQLSourceDefinition(
						"select pk, geom, ToMultiLine(geom) from ds3;"));
		final DataSource dataSource = dsf.getDataSource("ds3p");

		dataSource.open();
		final long rowCount = dataSource.getRowCount();
		final int fieldCount = dataSource.getFieldCount();
		assertTrue(3 == fieldCount);
		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Value[] fields = dataSource.getRow(rowIndex);
			final Geometry inGeometry = ((GeometryValue) fields[1]).getGeom();

			if (inGeometry instanceof MultiPoint) {
				assertTrue(fields[2] instanceof NullValue);
			} else {
				final Geometry outGeometry = ((GeometryValue) fields[2]).getGeom();
				assertTrue(outGeometry instanceof MultiLineString);
				assertTrue(outGeometry.toString().equals(
						"MULTILINESTRING ((0 0, 1 1, 0 1, 0 0))"));
			}

			for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
				System.out.print(fields[fieldIndex].toString() + ", ");
			}
			System.out.println();
		}
		dataSource.cancel();
	}
}