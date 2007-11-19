package org.gdms.sql.customQuery.spatial.convert;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SQLSourceDefinition;
import org.gdms.data.SyntaxException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.SpatialConvertCommonTools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

public class ExplodeTest extends SpatialConvertCommonTools {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		if (dsf.getSourceManager().exists("ds1p")) {
			dsf.getSourceManager().remove("ds1p");
		}

		if (dsf.getSourceManager().exists("ds2p")) {
			dsf.getSourceManager().remove("ds2p");
		}

		super.tearDown();
	}

	public void testEvaluate1() throws SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, DriverException {
		final DataSource inputDs = dsf
				.executeSQL("select pk,geom,geom from ds1;");
		dsf.getSourceManager().register("ds1p",
				new SQLSourceDefinition("select pk,geom,geom from ds1;"));
		final DataSource resultDs = dsf
				.executeSQL("select Explode() from ds1p;");
		resultDs.open();
		inputDs.open();

		final long rowCount = resultDs.getRowCount();
		long rowIndex = 0;
		while (rowIndex < rowCount) {
			final Geometry geometryCollection = ((GeometryValue) resultDs
					.getFieldValue(rowIndex, 2)).getGeom();
			for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
				final Value[] fields = resultDs.getRow(rowIndex++);
				final Geometry geometry = ((GeometryValue) fields[1]).getGeom();
				assertTrue(geometryCollection.getGeometryN(i).equals(geometry));
				assertFalse(geometry instanceof GeometryCollection);

				System.out.printf("%d, %s, %s\n", rowIndex,
						geometry.toString(), geometryCollection.toString());
			}
		}
		resultDs.cancel();
		inputDs.cancel();
	}

	public void testEvaluate2() throws SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, DriverException {
		final DataSource inputDs = dsf
				.executeSQL("select pk,geom,geom from ds2;");
		dsf.getSourceManager().register("ds2p",
				new SQLSourceDefinition("select pk,geom,geom from ds2;"));
		final DataSource resultDs = dsf
				.executeSQL("select Explode() from ds2p;");
		resultDs.open();
		inputDs.open();

		final long rowCount = resultDs.getRowCount();
		long rowIndex = 0;
		while (rowIndex < rowCount) {
			final Geometry geometryCollection = ((GeometryValue) resultDs
					.getFieldValue(rowIndex, 2)).getGeom();
			for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
				final Value[] fields = resultDs.getRow(rowIndex++);
				final Geometry geometry = ((GeometryValue) fields[1]).getGeom();
				assertTrue(geometryCollection.getGeometryN(i).equals(geometry));
				assertFalse(geometry instanceof GeometryCollection);

				System.out.printf("%d, %s, %s\n", rowIndex,
						geometry.toString(), geometryCollection.toString());
			}
		}
		resultDs.cancel();
		inputDs.cancel();
	}
}