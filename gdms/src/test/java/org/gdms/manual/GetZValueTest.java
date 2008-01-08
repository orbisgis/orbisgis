package org.gdms.manual;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.FirstStrategy;

public class GetZValueTest {

	static DataSourceFactory dsf = new DataSourceFactory();

	static DataSource ds1 = null;

	static DataSource ds2 = null;

	static String ds1Name;

	static String ds2Name;

	private static long beginTime;

	public static void main(String[] args) throws Exception {

		beginTime = System.currentTimeMillis();

		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/pointsz.shp");


		ds1 = dsf.getDataSource(src1);
		
		ds1Name = ds1.getName();
		

		GetZ();

		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);

	}

	

	private static void GetZ() throws Exception {
		String sqlQuery = "select GetZ(the_geom) as toto, the_geom  from " + ds1Name +" ;";

		
		System.out.println("exec " +sqlQuery );
		
		DataSource result = dsf.executeSQL(sqlQuery);
		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);
		result.open();
		System.out.println(result.getFieldValue(2, 0));
		System.out.println("fin exec");

		
		
		 
	}

}