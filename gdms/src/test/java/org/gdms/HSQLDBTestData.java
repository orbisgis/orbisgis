package org.gdms;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;

public class HSQLDBTestData extends TestData {

	public static final DataSourceDefinition gisappsDataSourceDefinition = new DBTableSourceDefinition(
			new DBSource(null, 0, SourceTest.backupDir + File.separator
					+ "hsqldbSample", null, null, "gisapps", "jdbc:hsqldb:file"));

	private DataSourceDefinition def;

	public HSQLDBTestData(String name, int rowCount, String noPKField,
			boolean hasRepeatedRows, DataSourceDefinition def) {
		super(name, true, HSQLDB, rowCount, true, noPKField, hasRepeatedRows);
		this.def = def;
	}

	@Override
	public String backup(DataSourceFactory dsf) throws Exception {
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();

			Connection c = DriverManager.getConnection("jdbc:hsqldb:file:"
					+ SourceTest.backupDir + File.separator + "hsqldbSample",
					null, "");

			Statement st = c.createStatement();

			st.execute("DROP TABLE \"gisapps\" IF EXISTS");
			st
					.execute("CREATE CACHED TABLE \"gisapps\" (\"id\" IDENTITY PRIMARY KEY, \"gis\" VARCHAR(10), \"points\" INTEGER, \"version\" VARCHAR)");

			st
					.execute("INSERT INTO \"gisapps\" VALUES(0, 'orbisgis', 10, null)");
			st.execute("INSERT INTO \"gisapps\" VALUES(1, 'gvsig', 9, 1.1)");
			st.execute("INSERT INTO \"gisapps\" VALUES(2, 'kosmo', 8, 1.1)");
			st
					.execute("INSERT INTO \"gisapps\" VALUES(3, 'openjump', 7, 'a lot')");
			st
					.execute("INSERT INTO \"gisapps\" VALUES(4, 'qgis', 6, 'I do not know')");
			st.execute("INSERT INTO \"gisapps\" VALUES(5, 'orbiscad', 5, 1.0)");

			st.close();
			c.close();
		} catch (ClassNotFoundException e) {
			throw new DataSourceCreationException(e);
		} catch (SQLException e) {
			throw new DataSourceCreationException(e);
		}

		String name = "hsqldb" + System.currentTimeMillis();
		dsf.registerDataSource(name, def);
		return name;
	}
}
