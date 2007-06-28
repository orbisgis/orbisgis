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
				new DBSource(null, 0, DB_PATH, "sa", "", "POINT",
						"jdbc:h2:file")));
		
		

		DataSource d;

		d = dsf.getDataSource("point");

		d.open();

		System.out.println("Number of lines " + d.getRowCount());

		int fieldCount = d.getMetadata().getFieldCount();

		for (int k = 0; k < d.getRowCount(); k++) {
			for (int t = 0; t < fieldCount; t++) {

				System.out.println("Metadonnées "
						+ d.getMetadata().getFieldName(t)
						+ d.getMetadata().getFieldType(t).getTypeCode());

				System.out.println("Valeur " + d.getFieldValue(k, t));

			}

		}

		d.commit();

		System.out
				.println("Total time " + (System.currentTimeMillis() - start));

	}

	public static void createH2db() throws ClassNotFoundException, SQLException {

		Class.forName("org.h2.Driver");

		Connection c = DriverManager.getConnection("jdbc:h2:" + DB_PATH, "sa",
				"");

		Statement st = c.createStatement();

		st.execute("DROP TABLE point IF EXISTS");

		st
				.execute("CREATE TABLE point (id INTEGER, nom VARCHAR(10), nom2 VARCHAR(100), length DECIMAL(20, 2), area DOUBLE, start DATE, prenom VARCHAR(100),  PRIMARY KEY(id), the_geom BLOB)");

		for (int i = 0; i < 4; i++) {

			st
					.execute("INSERT INTO point VALUES("
							+ i
							+ ", 'BOCHER', 'bocher', "
							+ (215.45 + i)
							+ ", 222,'2007-06-15', 'ERWAN', GeomFromText('POINT(0 1)', '-1'))");

		}

		st.close();
		c.close();

	}

}
