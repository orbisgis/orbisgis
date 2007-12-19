package org.orbisgis.geocatalog.resources.db;

import org.sif.multiInputPanel.ComboBoxChoice;
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
		addInput("dbType", "DataBase type", null, new ComboBoxChoice(
				"PostgreSQL / PostGIS", "H2 (spatial)"));
		addValidationExpression("strlength(dbType) > 0",
				"Please choose a DataBase type !");
		addInput("host", "Host name", null, new StringType(LENGTH));
		addInput("port", "Port number", null, new IntType(LENGTH));
		addValidationExpression("(port >= 0) and (port <= 32767)",
				"Port number is a number in the range [0,32767]");
		addInput("dbName", "DataBase name", null, new StringType(LENGTH));
		addValidationExpression("strlength(dbName) > 0",
				"DataBase name is mandatory!");
		addInput("user", "User name", null, new StringType(LENGTH));
		addInput("password", "Password", null, new PasswordType(LENGTH));
	}
}