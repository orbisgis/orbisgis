package org.gdms.sql.spatialSQL;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;

import com.hardcode.driverManager.DriverLoadException;

public class SpatialTests {

	static DataSourceFactory dsf = new DataSourceFactory();

	static DataSource ds1 = null;

	static DataSource ds2 = null;

	static String ds1Name;

	static String ds2Name;

	public static void main(String[] args) throws Exception {

		Long beginTime = System.currentTimeMillis();

		File src2 = new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp");
		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/bzh5_communes.shp");

		ds1 = dsf.getDataSource(src1);
		ds2 = dsf.getDataSource(src2);

		ds1Name = ds1.getName();
		ds2Name = ds2.getName();

		//SpatialDataSourceDecorator sds1 = new SpatialDataSourceDecorator(ds1);

		//SpatialDataSourceDecorator sds2 = new SpatialDataSourceDecorator(ds2);

		// Tests

		// testIntersection(sds1, sds2);
		testContains();

		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);

	}

	private static void testIntersection(SpatialDataSourceDecorator sds,
			SpatialDataSourceDecorator sds2) throws Exception {

		String sqlQuery = "select Intersection(" + ds1Name + ".the_geom,"
				+ ds2Name + ".the_geom) from " + ds1Name + ", " + ds2Name + ";";

		SpatialDataSource spatialds = new SpatialDataSourceDecorator(dsf
				.executeSQL(sqlQuery));

		displayGeometry(spatialds);

	}

	private static void testContains() throws SyntaxException,
			DriverLoadException, NoSuchTableException, ExecutionException,
			DriverException {

		String sqlQuery = "select * from " + ds1Name + ", " + ds2Name
				+ " where Contains(" + ds1Name + ".the_geom," + ds2Name
				+ ".the_geom)" + ";";

		SpatialDataSource spatialds = new SpatialDataSourceDecorator(dsf
				.executeSQL(sqlQuery));

		// displayGeometry(spatialds);

	}

	public static void displayGeometry(SpatialDataSource spatialds2)
			throws DriverException {

		spatialds2.open();

		for (int i = 0; i < spatialds2.getRowCount(); i++) {

			if (spatialds2.getGeometry(i).isEmpty()) {

			} else {
				System.out.println(spatialds2.getGeometry(i).toString());
			}
		}

		spatialds2.cancel();

	}
}