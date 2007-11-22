package org.urbsat.utilities;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SQLSourceDefinition;
import org.gdms.data.values.IntValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.spatial.GeometryValue;
import org.urbsat.UrbsatTestsCommonTools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class CreateGridTest extends UrbsatTestsCommonTools {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		if (dsf.getSourceManager().exists("ds1ppp")) {
			dsf.getSourceManager().remove("ds1ppp");
		}
		if (dsf.getSourceManager().exists("ds1pp")) {
			dsf.getSourceManager().remove("ds1pp");
		}
		if (dsf.getSourceManager().exists("ds1p")) {
			dsf.getSourceManager().remove("ds1p");
		}
		super.tearDown();
	}

	private void check(final DataSource dataSource, final boolean checkCentroid)
			throws AlreadyClosedException, DriverException {
		dataSource.open();
		final long rowCount = dataSource.getRowCount();
		final int fieldCount = dataSource.getFieldCount();
		assertTrue(4 == rowCount);
		assertTrue(2 == fieldCount);
		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Value[] fields = dataSource.getRow(rowIndex);
			final Geometry geom = ((GeometryValue) fields[0]).getGeom();
			final int id = ((IntValue) fields[1]).getValue();
			assertTrue(geom instanceof Polygon);
			assertTrue(Math.abs(1 - geom.getArea()) < 0.000001);
			assertTrue(4 == geom.getLength());
			assertTrue(5 == geom.getNumPoints());
			if (checkCentroid) {
				assertTrue(0.5 + (id - 1) / 2 == geom.getCentroid()
						.getCoordinate().x);
				assertTrue(0.5 + (id - 1) % 2 == geom.getCentroid()
						.getCoordinate().y);
			}

			for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
				System.out.print(fields[fieldIndex].toString() + ", ");
			}
			System.out.println();
		}
		dataSource.cancel();
	}

	public final void testEvaluate() throws DriverLoadException,
			NoSuchTableException, DataSourceCreationException, DriverException {
		dsf.getSourceManager().register("ds1p",
				new SQLSourceDefinition("select creategrid(1.0, 1) from ds1;"));
		check(dsf.getDataSource("ds1p"), true);

		dsf.getSourceManager().register("ds1pp",
				new SQLSourceDefinition("select creategrid(1,1,0) from ds1;"));
		check(dsf.getDataSource("ds1pp"), true);

		dsf.getSourceManager().register("ds1ppp",
				new SQLSourceDefinition("select creategrid(1,1,90) from ds1;"));
		check(dsf.getDataSource("ds1ppp"), false);
	}
}