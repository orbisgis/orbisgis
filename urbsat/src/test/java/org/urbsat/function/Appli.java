	package org.urbsat.function;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
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
		FunctionManager.addFunction(new ReadWKT());
		
		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp");
		
		
		File src2 = new File(
		"../../datas2tests/shp/mediumshape2D/hedgerow.shp");
		ds2 = dsf2.getDataSource(src2);
		ds2Name = ds2.getName();
		ds1 = dsf.getDataSource(src1);
		ds1Name = ds1.getName();
		
		ds1.open();
		DataSaved.setFileName("landocover2000.shp", ds1Name);
		//testEnveloppe();		
		
		String DB_PATH = "C:\\Documents and Settings\\thebaud\\Bureau\\STH_docs\\landocover2000.shpG4_4";
		dsf.registerDataSource("point", new DBTableSourceDefinition(
				new DBSource(null, 0, DB_PATH, "sa", "", "POINT",
						"jdbc:h2")));
	
		DataSaved.setDataSource(ds1Name, dsf);
		DataSaved.setDataSource(ds2Name, dsf2);
		MakeQuery.execute("select Enveloppe(the_geom) from " + ds1Name  + ";");
		//MakeQuery.execute("select Enveloppe(the_geom) from " + ds2Name  + ";");
	//	MakeQuery.execute("select Enveloppe(the_geom) from " + ds1Name  + ";");
	//	MakeQuery.execute("select MakeGrid(4,4) from " + ds1Name  + ";");
	//	MakeQuery.execute("select Density(the_geom,GEOM) from " + ds1Name +" , "+"point"+ " where type='built up areas' and INDEX_X=2 and INDEX_Y=2"+";");
//		MakeQuery.execute("select Density(the_geom,geom) from " + ds1Name +" , "+ds1Name+"G4_4"+ " where type='built up areas' and index_X=2 and index_Y=2"+";");
//		MakeQuery.execute("select MakeGrid(2,2) from " + ds1Name  + ";");
//		MakeQuery.execute("select MakeGrid(1,1) from " + ds1Name  + ";");
//		MakeQuery.execute("select Density(the_geom,geom) from " + ds1Name +" , "+ds1Name+"G2_2"+ " where type='built up areas' and index_X=0 and index_Y=1"+";");
//		MakeQuery.execute("select Density(the_geom,geom) from " + ds1Name +" , "+ds1Name+"G4_4"+ " where type='built up areas' and index_X=2 and index_Y=2"+";");
//		MakeQuery.execute("select BuildByCell(the_geom,geom) from " + ds1Name +" , "+ds1Name+"G4_4"+ " where type='built up areas' and index_X=2 and index_Y=2"+";");
//		MakeQuery.execute("select BuildByCell(the_geom,geom) from " + ds1Name +" , "+ds1Name+"G1_1"+ " where type='built up areas' and index_X=0 and index_Y=0"+";");
//		MakeQuery.execute("select BuildLenght(the_geom,geom) from " + ds1Name +" , "+ds1Name+"G1_1"+ " where type='built up areas' and index_X=0 and index_Y=0"+";");
		

		
		//MakeQuery.execute("select BuildByCell(0,0,the_geom,type,'built up areas') from " + ds1Name  + ";");
		//MakeQuery.execute("select BuildLenght(2,0,the_geom,type,'built up areas') from " + ds1Name  + ";");
		//MakeQuery.execute("select AverageBuildSpace(0,0,the_geom,type,'built up areas') from " + ds1Name  + ";");
		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);	
		
	}
}