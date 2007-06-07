package org.urbsat.function;

import java.io.File;
import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.FunctionManager;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Geometry;

public class Appli {

	static DataSourceFactory dsf = new DataSourceFactory();
	static DataSource ds1 = null;
	static String ds1Name;
	static DataSourceFactory dsf2 = new DataSourceFactory();
	static DataSource ds2 = null;
	static String ds2Name;
	

	public static void main(String[] args) throws Exception {
		
		Long beginTime = System.currentTimeMillis();
		FunctionManager.addFunction(new Enveloppe());
		FunctionManager.addFunction(new MakeGrid());
		FunctionManager.addFunction(new Density());
		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp");
		File src2 = new File(
		"../../datas2tests/shp/mediumshape2D/hedgerow.shp");
		ds2 = dsf2.getDataSource(src2);
		ds2Name = ds2.getName();
		ds1 = dsf.getDataSource(src1);
		ds1Name = ds1.getName();
		
		ds1.open();
		//testEnveloppe();
		
		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);
		DataSaved.setDatasource(ds1Name, dsf);
		DataSaved.setDatasource(ds2Name, dsf2);
		MakeQuery.execute("select Enveloppe(the_geom) from " + ds1Name  + ";");
		MakeQuery.execute("select Enveloppe(the_geom) from " + ds2Name  + ";");
		MakeQuery.execute("select Enveloppe(the_geom) from " + ds1Name  + ";");
		MakeQuery.execute("select MakeGrid(2,2) from " + ds1Name  + ";");
		MakeQuery.execute("select Density(1,0,the_geom,type,'built up areas') from " + ds1Name  + ";");
		MakeQuery.execute("select MakeGrid(4,4) from " + ds1Name  + ";");
		MakeQuery.execute("select Density(1,2,the_geom,type,'built up areas') from " + ds1Name  + ";");
	}
}