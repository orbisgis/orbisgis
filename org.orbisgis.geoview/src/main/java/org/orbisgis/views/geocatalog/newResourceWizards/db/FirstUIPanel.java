/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.views.geocatalog.newResourceWizards.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.driver.DBDriver;
import org.gdms.driver.h2.H2spatialDriver;
import org.gdms.driver.postgresql.PostgreSQLDriver;
import org.sif.multiInputPanel.ComboBoxChoice;
import org.sif.multiInputPanel.MultiInputPanel;
import org.sif.multiInputPanel.PasswordType;
import org.sif.multiInputPanel.StringType;

public class FirstUIPanel extends MultiInputPanel {
	private final static int LENGTH = 20;

	public FirstUIPanel() {
		super("org.orbisgis.geocatalog.resources.db.FirstUIPanel",
				"Connect to database");
		setInfoText("Introduce the connection parameters");
		addInput("dbType", "DataBase type", "PostgreSQL / PostGIS",
				new ComboBoxChoice("PostgreSQL / PostGIS", "H2 (spatial)"));
		addValidationExpression("strlength(dbType) is not null",
				"Please choose a DataBase type");
		addInput("host", "Host name", "127.0.0.1", new StringType(LENGTH));
		addValidationExpression("strlength(host) is not null",
				"Please choose a host");
		addInput("port", "Port number", "5432", new StringType(LENGTH));

		addValidationExpression("(int(port) >= 0) and (int(port) <= 32767)",
				"Port number is a number in the range [0,32767]");
		addInput("dbName", "DataBase name", "dbName", new StringType(LENGTH));
		addValidationExpression("strlength(dbName) is not null",
				"DataBase name is mandatory!");
		addInput("user", "User name", "postgres", new StringType(LENGTH));
		addInput("password", "Password", "", new PasswordType(LENGTH));
	}

	public String postProcess() {
		final String dbType = getInput("dbType");
		final String host = getInput("host");
		int port;
		final String dbName = getInput("dbName");
		final String user = getInput("user");
		final String password = getInput("password");

		try {
			DBDriver dBDriver;
			if (dbType.equals("H2 (spatial)")) {
				dBDriver = new H2spatialDriver();
				port = (0 == getInput("port").length()) ? 9092 : new Integer(
						getInput("port"));
			} else if (dbType.equals("PostgreSQL / PostGIS")) {
				dBDriver = new PostgreSQLDriver();
				port = (0 == getInput("port").length()) ? 5432 : new Integer(
						getInput("port"));
			} else {
				throw new RuntimeException("Unsupported DBType !");
			}
			final Connection connection = dBDriver.getConnection(host, port,
					dbName, user, password);
			connection.close();
			return null;
		} catch (SQLException e) {
			return "Can not connect: " + e.getMessage();
		}
	}

	public String validateInput() {
		return null;
	}
}