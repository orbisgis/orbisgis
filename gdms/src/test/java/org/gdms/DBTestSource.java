/**
 *
 */
package org.gdms;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;

public class DBTestSource extends TestSource {

	private String sqlScriptFile;
	private String jdbcDriver;
	private DBSource dbSource;

	public DBTestSource(String name, String jdbcDriver,
			String sqlScriptFile, DBSource dbSource) {
		super(name);
		this.jdbcDriver = jdbcDriver;
		this.sqlScriptFile = sqlScriptFile;
		this.dbSource = dbSource;
	}

	@Override
	public void backup() throws Exception {
		FileInputStream fis = new FileInputStream(sqlScriptFile);
		DataInputStream dis = new DataInputStream(fis);
		byte[] buffer = new byte[(int) fis.getChannel().size()];
		dis.readFully(buffer);
		String script = new String(buffer);

		Class.forName(jdbcDriver);
		String connectionString = dbSource.getPrefix() + ":";
		if (dbSource.getHost() != null) {
			connectionString += "//" + dbSource.getHost();

			if (dbSource.getPort() != -1) {
				connectionString += (":" + dbSource.getPort());
			}
			connectionString += "/";
		}

		connectionString += (dbSource.getDbName());

		Connection c = DriverManager.getConnection(connectionString,
				dbSource.getUser(), dbSource.getPassword());

		Statement st = c.createStatement();
		String[] statements = script.split("\\Q;\\E");
		for (String statement : statements) {
			try {
				st.execute(statement);
			} catch (SQLException e) {
				System.err.println(statement);
				e.printStackTrace();
			}
		}
		st.close();
		c.close();

		DBTableSourceDefinition def = new DBTableSourceDefinition(dbSource);
		SourceTest.dsf.registerDataSource(name, def);
	}

}