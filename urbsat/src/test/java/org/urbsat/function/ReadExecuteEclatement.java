//useless
package org.urbsat.function;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.sql.function.FunctionManager;

public class ReadExecuteEclatement {

	static DataSourceFactory dsf = new DataSourceFactory();

	static DataSource ds1 = null;

	

	static String ds1Name;



	public static void main(String[] args) throws Exception {

		Long beginTime = System.currentTimeMillis();
		
		FunctionManager.addFunction(new Eclatement());
		

		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp");
		
		ds1 = dsf.getDataSource(src1);
		ds1Name = ds1.getName();
				
		testEclatement();

		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);

	}

	private static void testEclatement() throws Exception {

		ds1.open();
		String sqlQuery = "select Eclatement(the_geom) from " + ds1Name  + " where gid=1;";
		DataSource result = dsf.executeSQL(sqlQuery);
		System.out.println("test1");
		displayValue(result);
		
		
		
	
	}

	
	public static void displayValue(DataSource result2)
	throws DriverException {
		
		result2.open();
		
		
		for (int i = 0; i < result2.getFieldNames().length; i++) {
				
				System.out.println(result2.getAsString());
				System.out.println(result2.getFieldNames()[i]);
				
				
				System.out.println(i);
			
		}

		result2.cancel();
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