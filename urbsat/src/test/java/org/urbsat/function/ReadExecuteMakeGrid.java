package org.urbsat.function;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.function.FunctionManager;

import com.hardcode.driverManager.DriverLoadException;

public class ReadExecuteMakeGrid {

	static DataSourceFactory dsf = new DataSourceFactory();

	static DataSource ds1 = null;


	static String ds1Name;



	public static void main(String[] args) throws Exception {

		Long beginTime = System.currentTimeMillis();
		
		FunctionManager.addFunction(new MakeGrid());
		FunctionManager.addFunction(new Enveloppe());

		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp");
		
		ds1 = dsf.getDataSource(src1);
		ds1Name = ds1.getName();
				
		testMyFunction();

		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);

	}

	private static void testMyFunction() throws Exception {

		//String sqlQuery = "select MyFunction(5) as titi, MyFunction(7) as toto, runoff_sum from " + ds1Name  + ";";
		
		String sqlQuery = "select MakeGrid(4,8) from " + ds1Name  + ";";
		DataSource result = dsf.executeSQL(sqlQuery);
				
		displayValue(result);
		ds1.open();
		

	}

	
	public static void displayValue(DataSource result2)
	throws DriverException {
		
		result2.open();
		
		System.out.println(result2.getAsString());
		System.out.println("test");

		result2.cancel();
	}
	



	
}