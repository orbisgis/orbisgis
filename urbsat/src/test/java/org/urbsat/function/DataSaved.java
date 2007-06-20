	package org.urbsat.function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SyntaxException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.types.DefaultType;
import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class DataSaved {
	private static Map<String, GeometryValue> Env = new HashMap<String, GeometryValue>();

	private static Map<String, List<List<Geometry>>> grid = new HashMap<String, List<List<Geometry>>>();

	private static Map<String, DataSourceFactory> data = new HashMap<String, DataSourceFactory>();

	private static Map<String, LineString> wind = new HashMap<String, LineString>();
	
	private static Map<String, String> files = new HashMap<String, String>();

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
	
	public static DataSource registerGrid(String dataName, List<List<Geometry>> theGrid) throws DriverLoadException, NoSuchTableException, DataSourceCreationException, DriverException, FreeingResourcesException, NonEditableDataSourceException, SyntaxException, ExecutionException {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] { "index",
		"index_X","index_Y","geom" }, new Type[] {
		new DefaultType(null, "INDEX", Type.INT),
		new DefaultType(null, "X", Type.INT),
		new DefaultType(null, "Y", Type.INT),
		new DefaultType(null, "GEOM", Type.INT)
		});
		DataSourceFactory dsf = DataSaved.getDatasource(dataName);
		String name = dataName+"G"+theGrid.get(0).size()+"_"+theGrid.size();
		dsf.registerDataSource(name, new ObjectSourceDefinition(omd));
		DataSource ds = dsf.getDataSource(name);
		ds.open();
		int i=0;
		int y=0;
		while (i<theGrid.size()) {
			
			while(y<theGrid.get(i).size()) {
			ds.insertEmptyRow();
			ds.setFieldValue((i*theGrid.get(i).size()+y), 0, ValueFactory.createValue((i*theGrid.get(i).size()+y)));
			ds.setFieldValue((i*theGrid.get(i).size()+y), 1, ValueFactory.createValue(y));
			ds.setFieldValue((i*theGrid.get(i).size()+y), 2, ValueFactory.createValue(i));
			ds.setFieldValue((i*theGrid.get(i).size()+y), 3, ValueFactory.createValue(DataSaved.getMaillon(dataName, y, i)));
			y++;
			}
			i++;
			y=0;
		}
		
	
		ds.commit();
		System.out.println(ds.getName());
		DataSaved.setDataSource(ds.getName(),dsf);
		MakeQuery.execute("select * from "+name);
		return ds;
	}
	
	public static void SaveGrid (String dataName, List<List<Geometry>> theGrid) throws ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver");
		int y_grid = theGrid.size();
		int x_grid = theGrid.get(0).size();
		String DB_PATH = "C:\\Documents and Settings\\thebaud\\Bureau\\STH_docs\\"+DataSaved.getFileName(dataName)+"G"+x_grid+"_"+y_grid;
		Connection c = DriverManager.getConnection("jdbc:h2:" + DB_PATH, "sa",
				"");

		Statement st = c.createStatement();

		st.execute("DROP TABLE point IF EXISTS");

		st
				.execute("CREATE TABLE point (index INTEGER, index_x INTEGER, index_y INTEGER, geom GEOMETRY,  PRIMARY KEY(index))");
		int i =0;
		int y =0;
		while (i<y_grid) {
			
			while(y<x_grid) {
				st.execute("INSERT INTO point VALUES("
						+(i*x_grid+y)
						+", "+y
						+", "+i
						+", GeomFromText('"+DataSaved.getMaillon(dataName, y, i).toString()+"','-1'))");
			y++;
			}
			i++;
			y=0;
		}
		DataSourceFactory dsf = DataSaved.getDatasource(dataName);
	
		st.close();
		c.close();
		dsf.registerDataSource("point", new DBTableSourceDefinition(
				new DBSource(null, 0, DB_PATH, "sa", "", "POINT",
				"jdbc:h2")));

	}
	
	public static String getFileName(String dataName) {
		return files.get(dataName);
	}
	
	public static void setFileName (String url, String dataName) {
		files.put(dataName, url);
	}
}