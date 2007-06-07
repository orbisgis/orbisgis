package org.urbsat.function;

import java.util.ArrayList;
import java.util.HashMap;

import org.gdms.data.DataSourceFactory;
import org.gdms.spatial.GeometryValue;

import com.vividsolutions.jts.geom.Geometry;

public class DataSaved {
	private static HashMap<String,GeometryValue> Env;
	private static HashMap<String,ArrayList<ArrayList<Geometry>>> grid;
	private static HashMap<String,DataSourceFactory>  data;
	public static void setDatasource(String name,DataSourceFactory db) {
		if (data==null) {
			data = new HashMap<String,DataSourceFactory>();
		}
		data.put(name, db);
	}
	
	public static DataSourceFactory getDatasource(String name) {
		return data.get(name);
	}
	public static void setEnveloppe(String dataname, GeometryValue geo) throws Exception {
		if (Env==null) {
			Env = new HashMap<String,GeometryValue>();
		}
		Env.put(dataname, geo);
	
	}
	public static GeometryValue getEnveloppe(String dataname) throws Exception {
		return Env.get(dataname);
	
	}
	public static ArrayList<ArrayList<Geometry>> getGrid(String dataname) throws Exception {
	 return grid.get(dataname);
	
	}
	public  static void setGrid(String dataname, ArrayList<ArrayList<Geometry>> theGrid) throws Exception {
		 if (grid==null) {
			 grid = new HashMap<String,ArrayList<ArrayList<Geometry>>>();
		 }
		grid.put(dataname, theGrid);
		
		}
	public static Geometry getMaillon(String dataname,int x, int y) {
		return grid.get(dataname).get(y).get(x);
	}

}
