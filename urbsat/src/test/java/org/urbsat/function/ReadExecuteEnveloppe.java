//useless
package org.urbsat.function;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.sql.function.FunctionManager;

import com.vividsolutions.jts.geom.Geometry;

public class ReadExecuteEnveloppe {

	static DataSourceFactory dsf = new DataSourceFactory();

	static DataSource ds1 = null;


	static String ds1Name;



	public static void main(String[] args) throws Exception {

		Long beginTime = System.currentTimeMillis();
		
		FunctionManager.addFunction(new Enveloppe());
		FunctionManager.addFunction(new WindDirection());
		

		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp");
		
		ds1 = dsf.getDataSource(src1);
		ds1Name = ds1.getName();
		
		
		testMyFunction();

		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);

	}

	private static void testMyFunction() throws Exception {
		ds1.open();
		//String sqlQuery = "select MyFunction(5) as titi, MyFunction(7) as toto, runoff_sum from " + ds1Name  + ";";
	
	
		String sqlQuery = "select * from " + ds1Name+ " where gid=1;";
		DataSaved.setDataSource(ds1Name, dsf);
		MakeQuery.execute(sqlQuery);
		


		
		
		

	}

	
	public static void displayValue(DataSource result2)
	throws DriverException {
		
		result2.open();
		
		
		//for (int i = 0; i < result2.getFieldNames().length; i++) {
			System.out.println(result2.getAsString());
				result2.cancel();
			
		//}


		

	}
	public static Geometry returnValue(DataSource result2)
	throws DriverException {
		
		result2.open();	
		Geometry geo =(Geometry) (result2.getFieldValue(result2.getRowCount()-1, 0));
		result2.cancel();
		return geo;
		
		
	}
	
	public Geometry getEnveloppe(DataSource result2) throws Exception {
		testMyFunction();
		Geometry geo = returnValue(result2);
		return geo;
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