package org.urbsat.function;

import java.io.File;
import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.FunctionManager;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class TestAppli {

	static DataSourceFactory dsf = new DataSourceFactory();

	static DataSource ds1 = null;

	static GeometryValue Env;
	static String ds1Name;
	static ArrayList<ArrayList<Geometry>> grid;

	public static void main(String[] args) throws Exception {
		
		Long beginTime = System.currentTimeMillis();
		FunctionManager.addFunction(new Enveloppe());
		FunctionManager.addFunction(new MakeGrid());
		FunctionManager.addFunction(new Density());
		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp");
		
		ds1 = dsf.getDataSource(src1);
		ds1Name = ds1.getName();
		
		
		ds1.open();
		//testEnveloppe();
		
		testMakeGrid();
		testGetBuildingDensity();testGetBuildingDensity();testGetBuildingDensity();
		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);
	}
	private static void testEnveloppe() throws Exception {
		
		
		String sqlQuery = "select Enveloppe(the_geom) from " + ds1Name  + ";";
		DataSource result = dsf.executeSQL(sqlQuery);
		//ObjectMemoryDriver omdResult = new ObjectMemoryDriver(result);
		System.out.println(result.getAsString());
		Env = (GeometryValue) result.getFieldValue(0, 0);
		
		//dsf.registerDataSource("getEnveloppe", new ObjectSourceDefinition(omdResult));
		
		
		
	}
	private static void testMakeGrid() throws Exception {

		ds1.open();
		/*String test ="select * from getEnveloppe;";
		DataSource resultest = dsf.executeSQL(test);
		resultest.open();*/
		
	
		
		String sqlQuery = "select MakeGrid(2,2) from " + ds1Name  + ";";
		DataSource result = dsf.executeSQL(sqlQuery);
		System.out.println(result.getAsString());
		
		

	}
	
	private static void testGetBuildingDensity() throws SyntaxException, DriverLoadException, NoSuchTableException, ExecutionException, DriverException {
		ds1.open();
		String sqlQuery = "select Density(1,0,the_geom,type,'built up areas') from " + ds1Name  + ";";
		DataSource result = dsf.executeSQL(sqlQuery);
		result.open();
		System.out.println(result.getAsString());
	}
	
	public static GeometryValue getEnveloppe() throws Exception {
		if (Env==null) {
			testEnveloppe();
		}
		return Env;
	
	}
	public static void setEnveloppe(GeometryValue geo) throws Exception {
		Env=geo;
	
	}
	
	public static ArrayList<ArrayList<Geometry>> getGrid() throws Exception {
	 return grid;
	
	}
	public static void setGrid(ArrayList<ArrayList<Geometry>> theGrid) throws Exception {
		 grid=theGrid;
		
		}
	public static Geometry getMaillon(int x, int y) {
		return grid.get(y).get(x);
	}

}
