package org.urbsat.function;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.sql.function.FunctionManager;

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
		FunctionManager.addFunction(new BuildByCell());
		FunctionManager.addFunction(new BuildLenght());
		FunctionManager.addFunction(new AverageBuildSpace());
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
		
		
		DataSaved.setDataSource(ds1Name, dsf);
		DataSaved.setDataSource(ds2Name, dsf2);
		MakeQuery.execute("select Enveloppe(the_geom) from " + ds1Name  + ";");
		MakeQuery.execute("select Enveloppe(the_geom) from " + ds2Name  + ";");
		MakeQuery.execute("select Enveloppe(the_geom) from " + ds1Name  + ";");
		MakeQuery.execute("select MakeGrid(3,3) from " + ds1Name  + ";");
		MakeQuery.execute("select Density(0,0,the_geom,type,'built up areas') from " + ds1Name  + ";");
		MakeQuery.execute("select BuildByCell(0,0,the_geom,type,'built up areas') from " + ds1Name  + ";");
		MakeQuery.execute("select BuildLenght(0,0,the_geom,type,'built up areas') from " + ds1Name  + ";");
		//MakeQuery.execute("select AverageBuildSpace(0,0,the_geom,type,'built up areas') from " + ds1Name  + ";");
		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);	
		
	}
}