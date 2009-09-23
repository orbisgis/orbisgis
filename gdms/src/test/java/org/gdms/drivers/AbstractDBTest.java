/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
		String script = "DROP TABLE " + source.getTableName() + ";";
		try {
			executeScript(source, script);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
