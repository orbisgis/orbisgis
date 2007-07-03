package org.urbsat.function;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.function.FunctionManager;
import org.urbsat.utilities.CreateRugoxel;

public class Appli2 {
	static DataSourceFactory dsf = new DataSourceFactory();
	
	
	public static void main(String[] args) throws Exception {
		FunctionManager.addFunction(new Enveloppe());
		FunctionManager.addFunction(new CreateGrid());
		FunctionManager.addFunction(new CreateRugoxel());
		File src = new File(
		"../../datas2tests/shp/mediumshape2D/landcover2000.shp");
		DataSource ds1 = dsf.getDataSource(src);
		ObjectMemoryDriver omd = new ObjectMemoryDriver(ds1);
		dsf.registerDataSource("parcelle", new ObjectSourceDefinition(omd));
	/*	dsf.registerDataSource("parcelle", new DBTableSourceDefinition(
				new DBSource(null, 0, "C:\\Documents and Settings\\thebaud\\Bureau\\STH_docs\\essai", "sa", "", ds1.getName(),
				"jdbc:h2")));*/
		
		DataSource ds2 = dsf.executeSQL("select Enveloppe(the_geom) from parcelle");
		ObjectMemoryDriver omd2 = new ObjectMemoryDriver(ds2);
		dsf.registerDataSource("enveloppe", new ObjectSourceDefinition(omd2));
		
	
		DataSource ds3 = dsf.executeSQL("select CreateGrid(4,4,expr0) from enveloppe");
		DataSource result =dsf.executeSQL("select * from grid");
		result.open();
		
		dsf.executeSQL("select CreateRugoxel (geom, GeomFromText('POLYGON (( 250000 2300000, "
				+ "300000 2300000, 300000 2330000,250000 2330000, "
				+ "250000 2300000))')) from grid;");
		//System.out.println(result.getAsString());
		
		
		
	}
}
