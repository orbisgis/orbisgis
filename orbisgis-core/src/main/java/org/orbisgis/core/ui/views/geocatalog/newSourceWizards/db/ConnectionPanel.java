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
package org.orbisgis.core.ui.views.geocatalog.newSourceWizards.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.data.db.DBSource;
import org.gdms.driver.DBDriver;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.DBDriverFilter;
import org.gdms.source.SourceManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.InputType;
import org.orbisgis.sif.multiInputPanel.IntType;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.PasswordType;
import org.orbisgis.sif.multiInputPanel.StringType;

public class ConnectionPanel extends MultiInputPanel {
	private final static int LENGTH = 20;
	public static final String DBTYPE = "dbtype";
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String DBNAME = "dbname";
	public static final String USER = "user";
	public static final String PASSWORD = "pass";

	public ConnectionPanel() {
		super("org.orbisgis.core.ui.geocatalog.resources.db.FirstUIPanel",
				"Connect to database");
		setInfoText("Introduce the connection parameters");
		addInput(DBTYPE, "DataBase type", getDriverInput());
		addValidationExpression(DBTYPE + " is not null",
				"Please choose a DataBase type");
		addInput(HOST, "Host name", "127.0.0.1", new StringType(LENGTH));
		addValidationExpression(HOST + " is not null", "Please choose a host");
		addInput(PORT, "Port number (0=default)", "0", new IntType(LENGTH));

		addValidationExpression("(" + PORT + " >= 0) and (" + PORT
				+ " <= 32767)",
				"Port number is a number in the range [0,32767]");
		addInput(DBNAME, "Database name", "database_name", new StringType(
				LENGTH));
		addValidationExpression(DBNAME + " is not null",
				"Database name is mandatory");
		addInput(USER, "User name", "postgres", new StringType(LENGTH));
		addInput(PASSWORD, "Password", "", new PasswordType(LENGTH));
	}

	private InputType getDriverInput() {
		DataManager dm = Services.getService(DataManager.class);
		SourceManager sourceManager = dm.getSourceManager();
		DriverManager driverManager = sourceManager.getDriverManager();

		Driver[] filtered = driverManager.getDrivers(new DBDriverFilter());

		String[] ids = new String[filtered.length];
		String[] texts = new String[filtered.length];
		for (int i = 0; i < texts.length; i++) {
			ReadOnlyDriver rod = (ReadOnlyDriver) filtered[i];
			ids[i] = rod.getDriverId();
			texts[i] = rod.getTypeDescription();
		}
		ComboBoxChoice combo = new ComboBoxChoice(ids, texts);
		return combo;
	}

	public String postProcess() {
		try {
			Connection connection = getConnection();
			connection.close();
			return null;
		} catch (SQLException e) {
			return "Can not connect: " + e.getMessage();
		}
	}

	public Connection getConnection() throws SQLException {
		DBSource dbSource = getDBSource(null);
		Connection connection = getDBDriver().getConnection(dbSource.getHost(),
				dbSource.getPort(), dbSource.getDbName(), dbSource.getUser(),
				dbSource.getPassword());
		return connection;
	}

	public DBDriver getDBDriver() {
		DataManager dm = Services.getService(DataManager.class);
		DriverManager driverManager = dm.getSourceManager().getDriverManager();
		String dbType = getInput(DBTYPE);
		DBDriver dbDriver = (DBDriver) driverManager.getDriver(dbType);
		return dbDriver;
	}

	public String validateInput() {
		return null;
	}

	public DBSource getDBSource(String tableName) {
		String host = getInput(HOST);
		int port = Integer.parseInt(getInput(PORT));
		String dbName = getInput(DBNAME);
		String user = getInput(USER);
		String password = getInput(PASSWORD);
		DBDriver dbDriver = getDBDriver();
		if ((port == 0) || (getInput(PORT).trim().length() == 0)) {
			port = dbDriver.getDefaultPort();
		}

		return new DBSource(host, port, dbName, user, password, tableName,
				getDBDriver().getPrefixes()[0]);
	}
}