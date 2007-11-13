//useless
package org.urbsat.function;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.function.FunctionManager;

public class ReadExecuteProportion {

	static DataSourceFactory dsf = new DataSourceFactory();

	static DataSource ds1 = null;

	static DataSource ds2 = null;

	static String ds1Name;

	static String ds2Name;

	public static void main(String[] args) throws Exception {

		Long beginTime = System.currentTimeMillis();
		
		FunctionManager.addFunction(new Proportion());
		

		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp");
		
		ds1 = dsf.getDataSource(src1);
		ds1Name = ds1.getName();
				
		testMyFunction();

		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);

	}

	private static void testMyFunction() throws Exception {

		//String sqlQuery = "select MyFunction(5) as titi, MyFunction(7) as toto, runoff_sum from " + ds1Name  + ";";
		String sqlQuery = "select Proportion(the_geom) from " + ds1Name  + ";";
		DataSource result = dsf.executeSQL(sqlQuery);
				
		displayValue(result);
		ds1.open();
		

	}

	
	public static void displayValue(DataSource result2)
	throws DriverException {
		
		result2.open();
		
		
		for (int i = 0; i < result2.getFieldNames().length; i++) {
				
				System.out.println(result2.getFieldValue(result2.getRowCount()-1, 0));
				
			
		}

		result2.cancel();
	}
	

	public static void displayGeometry(SpatialDataSourceDecorator spatialds2)
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