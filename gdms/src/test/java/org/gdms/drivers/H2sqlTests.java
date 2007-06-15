package org.gdms.drivers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.driver.DriverException;

import com.hardcode.driverManager.DriverLoadException;

public class H2sqlTests {

	private static final String DB_PATH = "/tmp/h2/myH2db";

	/**
	 * @param args
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws NonEditableDataSourceException
	 * @throws FreeingResourcesException
	 * @throws DriverException
	 * @throws DataSourceCreationException
	 * @throws NoSuchTableException
	 * @throws DriverLoadException
	 */
	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, DriverException, FreeingResourcesException,
			NonEditableDataSourceException, DriverLoadException,
			NoSuchTableException, DataSourceCreationException {

		long start = System.currentTimeMillis();

		createH2db();

		DataSourceFactory dsf = new DataSourceFactory();

		dsf.registerDataSource("point", new DBTableSourceDefinition(
				new DBSource(null, 0, DB_PATH, "sa", "", "LINESTRING",
						"jdbc:h2:file")));

	
		DataSource d;

		d = dsf.getDataSource("point");

		d.open();

		System.out.println("Number of lines " + d.getRowCount());
		
		int fieldCount = d.getMetadata().getFieldCount();
		
		for (int k=0; k<d.getRowCount();k++){
			for (int t=0; t<fieldCount;t++){
			
			System.out.println("Metadonnées " + d.getMetadata().getFieldName(t) + d.getMetadata().getFieldType(t).getTypeCode());
			
			System.out.println("Valeur " + d.getFieldValue(k, t));

			}			
			
			
		}
			
			
		
		
		d.commit();

		System.out
				.println("Total time " + (System.currentTimeMillis() - start));

	}

	public static void createH2db() throws ClassNotFoundException, SQLException {

		Class.forName("org.h2.Driver");

		Connection c = DriverManager.getConnection("jdbc:h2:" +
				DB_PATH, "sa",
				"");
		

		Statement st = c.createStatement();

		st.execute("DROP TABLE point IF EXISTS");
		st.execute("DROP TABLE linestring IF EXISTS");
		st.execute("DROP TABLE polygon IF EXISTS");


		st.execute("CREATE TABLE point (id INTEGER, nom VARCHAR(10), prenom VARCHAR(10),  PRIMARY KEY(id), the_geom GEOMETRY)");
		st.execute("CREATE TABLE linestring (id INTEGER, nom VARCHAR(10), prenom VARCHAR(10),  PRIMARY KEY(id), the_geom GEOMETRY)");
		st.execute("CREATE TABLE polygon (id INTEGER, nom VARCHAR(10), prenom VARCHAR(10),  PRIMARY KEY(id), the_geom GEOMETRY)");
		
		
		int k = 0;
		for (int i = 0; i < 3; i++) {

			k = i++;

			st.execute("INSERT INTO point VALUES(" + k
					+ ", 'erwan', 'bocher', GeomFromText('POINT(0 1)', '-1'))");
			st.execute("INSERT INTO linestring VALUES(" + k
					+ ", 'erwan', 'bocher', GeomFromText('LINESTRING ( 65 145, 259 152, 310 247, 356 204 )', '-1'))");
			st.execute("INSERT INTO polygon VALUES(" + k
					+ ", 'erwan', 'bocher', GeomFromText('POLYGON (( 126 80, 126 248, 258 248, 258 80, 126 80 ))', '-1'))");

			
		}

		st.close();
		c.close();

	}

}
