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
import org.h2spatial.SQLCodegenerator;

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

		Connection c = DriverManager.getConnection(connectionString, dbSource
				.getUser(), dbSource.getPassword());

		Statement st = c.createStatement();
		if (jdbcDriver.equals("org.h2.Driver")) {
			SQLCodegenerator.addSpatialFunctions(st);
		}
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
		SourceTest.dsf.getSourceManager().register(name, def);
	}

}