package org.urbsat.function;

import java.io.File;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.function.FunctionManager;
import org.gdms.utility.Utility;

public class SQLTests {

	
	
	private static DataSource ds1;
	private static DataSourceFactory dsf =  new DataSourceFactory();
	private static String ds1Name;

	public static void main(String[] args) throws Exception {
		
		Long beginTime = System.currentTimeMillis();
		FunctionManager.addFunction(new Enveloppe());
		FunctionManager.addFunction(new MakeGrid());
		FunctionManager.addFunction(new Density());
		
		File src1 = new File("../../datas2tests/shp/mediumshape2D/landcover2000.shp");
		
		dsf.registerDataSource("parcelle", new FileSourceDefinition(src1));
				
		
		
		
		String polygon = "POLYGON()";
			
		//DataSource result = dsf.executeSQL("select Enveloppe(the_geom) as the_geom from parcelle;");
		
		//dsf.registerDataSource("enveloppe",result);
		
		
		//result.open();
		
		//System.out.println(result.getFieldCount());
		
		DataSource grid = dsf.executeSQL("select MakeGrid(4,4) as the_geom from parcelle;");
		
		
		new Utility().show(new DataSource[]{new SpatialDataSourceDecorator(grid)});
		
		
	}
	
	
}
