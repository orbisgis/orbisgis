//useless
package org.urbsat.function;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.function.FunctionManager;

public class ReadExecuteFunction {

	static DataSourceFactory dsf = new DataSourceFactory();

	static DataSource ds1 = null;

	static DataSource ds2 = null;

	static String ds1Name;

	static String ds2Name;

	public static void main(String[] args) throws Exception {

		Long beginTime = System.currentTimeMillis();
		
		FunctionManager.addFunction(new MyFunction());
		//FunctionManager.addFunction(new ToLine());

		File src1 = new File(
				"../../datas2tests/shp/bigshape3D/point3D.shp");
		
		
		dsf.registerDataSource("mydata", new FileSourceDefinition(src1));
		
		
		ds1 = dsf.getDataSource(src1);
		ds1Name = ds1.getName();
		
		displayGeometry(new SpatialDataSourceDecorator(ds1));
		

		//testMyFunction();

		//testMyFunction(ds1);
		
		//testToLine(ds1);
		


		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);

	}

	private static void testMyFunction() throws Exception {

		//String sqlQuery = "select MyFunction(5) as titi, MyFunction(7) as toto, runoff_sum from " + ds1Name  + ";";
		String sqlQuery = "select MyFunction(5) as titi, MyFunction(7) as toto, runoff_sum from " + ds1Name  + ";";
		DataSource result = dsf.executeSQL(sqlQuery);
				
		displayValue(result);
		ds1.open();
		System.out.println(ds1.getFieldNames()[3]);

	}

	
	private static void testToLine(DataSource ds1) throws Exception {

		String sqlQuery = "select ToLine(the_geom) from " + ds1Name  + ";";

		SpatialDataSourceDecorator spatialds = new SpatialDataSourceDecorator(dsf
				.executeSQL(sqlQuery));

				
		displayGeometry(spatialds);

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