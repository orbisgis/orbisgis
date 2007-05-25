package org.urbsat.function;

import java.io.File;

import org.gdms.data.InternalDataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.function.FunctionManager;

import com.hardcode.driverManager.DriverLoadException;

public class ReadExecuteFunction {

	static DataSourceFactory dsf = new DataSourceFactory();

	static InternalDataSource ds1 = null;

	static InternalDataSource ds2 = null;

	static String ds1Name;

	static String ds2Name;

	public static void main(String[] args) throws Exception {

		Long beginTime = System.currentTimeMillis();
		
		FunctionManager.addFunction(new MyFunction());
		

		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp");
		
		ds1 = dsf.getDataSource(src1);
		ds1Name = ds1.getName();
				
		testMyFunction(ds1);

		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);

	}

	private static void testMyFunction(InternalDataSource ds1) throws Exception {

		String sqlQuery = "select MyFunction() as toto from " + ds1Name  + ";";

		InternalDataSource result = dsf.executeSQL(sqlQuery);
				
		displayValue(result);

	}

	
	public static void displayValue(InternalDataSource result2)
	throws DriverException {
		
		result2.open();
		
		
		for (int i = 0; i < result2.getFieldNames().length; i++) {
			
				System.out.println(result2.getFieldNames()[i]);
			
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