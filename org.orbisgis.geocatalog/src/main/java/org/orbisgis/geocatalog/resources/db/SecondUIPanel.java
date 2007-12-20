package org.orbisgis.geocatalog.resources.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.data.db.DBSource;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;
import org.gdms.driver.h2.H2spatialDriver;
import org.gdms.driver.postgresql.PostgreSQLDriver;
import org.sif.multiInputPanel.ListChoice;
import org.sif.multiInputPanel.MultiInputPanel;

public class SecondUIPanel extends MultiInputPanel {
	private final static String spatial_ref_sys = "spatial_ref_sys";
	private final static String geometry_columns = "geometry_columns";

	private FirstUIPanel firstPanel;

	private String host;
	private int port;
	private String dbName;
	private String user;
	private String password;
	private String dbType;

	public SecondUIPanel(final FirstUIPanel firstPanel) {
		super("Select table(s) name(s)...");
		addInput("tablesNames", null, null, new ListChoice(new String[0]));
		// addValidationExpression("strlength(tablesNames) IS NOT NULL",
//		addValidationExpression("strlength(tablesNames) > 0",
//				"Select at least one table !");
		this.firstPanel = firstPanel;
	}

	public String initialize() {
		dbType = firstPanel.getInput("dbType");
		host = firstPanel.getInput("host");
		dbName = firstPanel.getInput("dbName");
		user = firstPanel.getInput("user");
		password = firstPanel.getInput("password");

		try {
			DBDriver dBDriver;
			if (dbType.equals("H2 (spatial)")) {
				dBDriver = new H2spatialDriver();
				dbType = "jdbc:h2";
				port = (0 == firstPanel.getInput("port").length()) ? 9092
						: new Integer(firstPanel.getInput("port"));
			} else if (dbType.equals("PostgreSQL / PostGIS")) {
				dBDriver = new PostgreSQLDriver();
				dbType = "jdbc:postgresql";
				port = (0 == firstPanel.getInput("port").length()) ? 5432
						: new Integer(firstPanel.getInput("port"));
			} else {
				throw new RuntimeException("Unsupported DBType !");
			}
			final Connection connection = dBDriver.getConnection(host, port,
					dbName, user, password);
			final TableDescription[] tableDescriptions = dBDriver
					.getTables(connection);

			final StringBuilder sbAllTablesNames = new StringBuilder();

			for (int i = 0; i < tableDescriptions.length; i++) {
				final String tblName = tableDescriptions[i].getName();
				if (!((spatial_ref_sys.equals(tblName) || geometry_columns
						.equals(tblName)))) {
					sbAllTablesNames.append(tblName);
					if (i + 1 != tableDescriptions.length) {
						sbAllTablesNames.append(ListChoice.SEPARATOR);
					}
				}
			}
			setValue("tablesNames", sbAllTablesNames.toString());
			connection.close();
			return null;
		} catch (SQLException e) {
			return e.getMessage();
		} catch (DriverException e) {
			return e.getMessage();
		}
	}

	public DBSource[] getSelectedDBSources() {
		final String[] tablesNames = getInput("tablesNames").split(
				ListChoice.SEPARATOR);
		final DBSource[] dBSources = new DBSource[tablesNames.length];
		for (int i = 0; i < tablesNames.length; i++) {
			dBSources[i] = new DBSource(host, port, dbName, user, password,
					tablesNames[i].toString(), dbType);
		}
		return dBSources;
	}
}