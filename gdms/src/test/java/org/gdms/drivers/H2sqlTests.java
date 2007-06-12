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

		dsf.registerDataSource("matable", new DBTableSourceDefinition(
				new DBSource(null, 0, "./h2/myH2db", "sa", "", "matable",
						"jdbc:h2:file")));

		DataSource d;

		d = dsf.getDataSource("matable");

		d.open();

		System.out.println("Number of lines " + d.getRowCount());

		d.commit();

		System.out
				.println("Total time " + (System.currentTimeMillis() - start));

	}

	public static void createH2db() throws ClassNotFoundException, SQLException {

		Class.forName("org.h2.Driver");

		Connection c = DriverManager.getConnection("jdbc:h2:./h2/myH2db", "sa",
				"");
		;

		Statement st = c.createStatement();

		st.execute("DROP TABLE matable IF EXISTS");

		st
				.execute("CREATE TABLE matable (id INTEGER, nom VARCHAR(10), prenom VARCHAR(10),  PRIMARY KEY(id))");
		int k = 0;
		for (int i = 0; i < 10000; i++) {

			k = i++;

			st.execute("INSERT INTO matable VALUES(" + k
					+ ", 'erwan', 'bocher')");

			// st.execute("SHUTDOWN");
		}

		st.close();
		c.close();

	}

}
