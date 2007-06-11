package org.urbsat.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gdms.data.DataSourceFactory;
import org.gdms.spatial.GeometryValue;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

public class DataSaved {
	private static Map<String, GeometryValue> Env = new HashMap<String, GeometryValue>();

	private static Map<String, List<List<Geometry>>> grid = new HashMap<String, List<List<Geometry>>>();

	private static Map<String, DataSourceFactory> data = new HashMap<String, DataSourceFactory>();

	private static Map<String, LineString> wind = new HashMap<String, LineString>();

	public static void setDataSource(final String name, DataSourceFactory dsf) {
		data.put(name, dsf);
	}

	public static DataSourceFactory getDatasource(String name) {
		return data.get(name);
	}

	public static void setEnveloppe(String dataName, GeometryValue geometryValue) {
		Env.put(dataName, geometryValue);
	}

	public static GeometryValue getEnveloppe(String dataname) {
		return Env.get(dataname);
	}

	public static List<List<Geometry>> getGrid(String dataname) {
		return grid.get(dataname);
	}

	public static void setGrid(String dataname, List<List<Geometry>> theGrid) {
		grid.put(dataname, theGrid);
	}

	public static Geometry getMaillon(String dataname, int x, int y) {
		return grid.get(dataname).get(y).get(x);
	}

	public static void setWind(String dataname, LineString vector) {
		wind.put(dataname, vector);
	}

	public static LineString getWind(String dataname) {
		return wind.get(dataname);
	}
}