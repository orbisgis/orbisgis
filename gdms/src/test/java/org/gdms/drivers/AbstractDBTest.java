package org.gdms.drivers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.source.SourceManager;

public abstract class AbstractDBTest extends TestCase {

	protected DataSourceFactory dsf;
	protected SourceManager sm;

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		sm = dsf.getSourceManager();
		sm.removeAll();
	}

	protected void executeScript(DBSource dbSource, String statement)
			throws Exception {
		Class.forName("org.postgresql.Driver").newInstance();
		Class.forName("org.h2.Driver").newInstance();
		Class.forName("org.hsqldb.jdbcDriver").newInstance();
		String connectionString = dbSource.getPrefix() + ":";
		if (dbSource.getHost() != null) {
			connectionString += "//" + dbSource.getHost();

			if (dbSource.getPort() != -1) {
				connectionString += (":" + dbSource.getPort());
			}
			connectionString += "/";
		}

		connectionString += (dbSource.getDbName());

		Connection c = DriverManager.getConnection(connectionString, dbSource
				.getUser(), dbSource.getPassword());

		Statement st = c.createStatement();
		st.execute(statement);
		st.close();
		c.close();
	}

	protected DBSource getPostgreSQLSource(String tableName) {
		return new DBSource("127.0.0.1", 5432, "gdms", "postgres", "postgres",
				tableName, "jdbc:postgresql");
	}

	protected DBSource getH2Source(String tableName) {
		return new DBSource(null, -1, "src/test/resources/backup/" + tableName,
				"sa", "", tableName, "jdbc:h2");
	}

	protected DBSource getHSQLDBSource(String tableName) {
		return new DBSource(null, -1, "src/test/resources/backup/" + tableName,
				"sa", "", tableName, "jdbc:hsqldb:file");
	}

	protected void deleteTable(DBSource source) {
		String script = "DROP TABLE \"" + source.getTableName() + "\";";
		try {
			executeScript(source, script);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
