package org.orbisgis.geocatalog.resources.db;

import org.sif.multiInputPanel.IntType;
import org.sif.multiInputPanel.MultiInputPanel;
import org.sif.multiInputPanel.PasswordType;
import org.sif.multiInputPanel.StringType;

public class FirstUIPanel extends MultiInputPanel {
	private final static int LENGTH = 20;

	public FirstUIPanel() {
		super("org.orbisgis.geocatalog.resources.db.FirstUIPanel",
				"Connect to database");
		setInfoText("Introduce the connection parameters");
		addInput("dbType", "DataBase type", "jdbc:postgresl", new StringType(
				LENGTH));
		addValidationExpression(
				"(dbType LIKE 'jdbc:%')",
				// "((dbType LIKE 'jdbc:h2') or (dbType LIKE
				// 'jdbc:postgresql'))",
				"DataBase type must be jdbc:h2 or jdbc:portgresql");
		addInput("host", "Host name", "192.168.10.53", new StringType(LENGTH));
		addInput("port", "Port number", "5432", new IntType(LENGTH));
		addValidationExpression("(port >= 0) and (port <= 32767)",
				"Port number is a number in the range [0,32767]");
		addInput("dbName", "DataBase name", "gdms", new StringType(LENGTH));
		addValidationExpression("strlen(dbName) > 0",
				"DataBase name is mandatory!");
		addInput("user", "User name", "postgres", new StringType(LENGTH));
		addInput("password", "Password", "", new PasswordType(LENGTH));
	}
}