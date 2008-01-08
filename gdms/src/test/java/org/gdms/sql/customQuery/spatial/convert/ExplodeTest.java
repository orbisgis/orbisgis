package org.gdms.sql.customQuery.spatial.convert;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
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

	private void evaluate(final DataSource dataSource) throws SyntaxException,
			DriverLoadException, NoSuchTableException, ExecutionException,
			DriverException {
		dataSource.open();
		final long rowCount = dataSource.getRowCount();
		long rowIndex = 0;
		while (rowIndex < rowCount) {
			final Geometry geometryCollection = dataSource
					.getFieldValue(rowIndex, 2).getAsGeometry();
			for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
				final Value[] fields = dataSource.getRow(rowIndex++);
				final Geometry geometry = fields[1].getAsGeometry();
				assertTrue(geometryCollection.getGeometryN(i).equals(geometry));
				assertFalse(geometry instanceof GeometryCollection);

				System.out.printf("%d, %s, %s\n", rowIndex,
						geometry.toString(), geometryCollection.toString());
			}
		}
		dataSource.cancel();
	}

	public void testEvaluate1() throws SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, DriverException {
		dsf.getSourceManager().register("ds1p",
				"select pk, geom, geom from ds1;");
		evaluate(dsf.executeSQL("select Explode() from ds1p;"));
	}

	public void testEvaluate2() throws SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, DriverException {
		dsf.getSourceManager().register("ds2p",
				"select pk, geom, geom from ds2;");
		evaluate(dsf.executeSQL("select Explode() from ds2p;"));
	}
}