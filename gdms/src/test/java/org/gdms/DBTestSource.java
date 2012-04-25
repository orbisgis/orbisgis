/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
/**
 *
 */
package org.gdms;

import java.io.DataInputStream;
import java.io.File;
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

	public DBTestSource(String name, String jdbcDriver, String sqlScriptFile,
			DBSource dbSource) {
		super(name);
		this.jdbcDriver = jdbcDriver;
		this.sqlScriptFile = sqlScriptFile;
		this.dbSource = dbSource;
	}

        public boolean isConnected() throws Exception {
                File f = new File(sqlScriptFile);
                if (!f.exists()) {
                        return false;
                }

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
                try {
                        Connection c = DriverManager.getConnection(connectionString, dbSource.getUser(), dbSource.getPassword());
                } catch (SQLException ex) {
                        return false;
                }
                return true;
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

                DriverManager.setLoginTimeout(2);
		Connection c = DriverManager.getConnection(connectionString, dbSource
				.getUser(), dbSource.getPassword());

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
                TestBase.dsf.getSourceManager().remove(name);
		TestBase.dsf.getSourceManager().register(name, def);
	}

}