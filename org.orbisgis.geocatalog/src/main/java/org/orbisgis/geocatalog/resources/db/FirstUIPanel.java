package org.orbisgis.geocatalog.resources.db;

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
		// super("org.orbisgis.geocatalog.resources.db.FirstUIPanel",
		// "Connect to database");
		super("Connect to database");
		setInfoText("Introduce the connection parameters");
		addInput("dbType", "DataBase type", "PostgreSQL / PostGIS",
				new ComboBoxChoice("PostgreSQL / PostGIS", "H2 (spatial)"));
		// addValidationExpression("strlength(dbType) IS NOT NULL",
		// TODO: comment for the binary release
		// addValidationExpression("strlength(dbType) > 0",
		// "Please choose a DataBase type !");
		addInput("host", "Host name", "127.0.0.1", new StringType(LENGTH));
		addInput("port", "Port number", "5432", new StringType(LENGTH));
		// addValidationExpression("(int(port) >= 0) and (int(port) <= 32767)",
		// "Port number is a number in the range [0,32767]");
		addInput("dbName", "DataBase name", "dbName", new StringType(LENGTH));
		// addValidationExpression("strlength(dbName) IS NOT NULL",
		// TODO: comment for the binary release
		// addValidationExpression("strlength(dbName) > 0",
		// "DataBase name is mandatory!");
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
}