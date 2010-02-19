package org.orbisgis.core.ui.plugins.toc;

import javax.swing.JOptionPane;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.db.DBSource;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.progress.IProgressMonitor;

public class ExportInDatabaseOperation implements BackgroundJob {

	private DataSourceFactory dsf;
	private DBSource dbSource;
	private DBDriver dbDriver;
	private String schemaName;
	private String inSourceName;
	private String outSourceName;

	public ExportInDatabaseOperation(DataSourceFactory dsf,
			String inSourceName, String outSourceName, String schemaName,
			DBSource dbSource, DBDriver dbDriver) {
		this.outSourceName = outSourceName;
		this.inSourceName = inSourceName;
		this.dbSource = dbSource;
		this.dbDriver = dbDriver;
		this.schemaName = schemaName;
		this.dsf = dsf;
	}

	@Override
	public String getTaskName() {
		return "Exporting in a database";
	}

	@Override
	public void run(IProgressMonitor pm) {

		try {

			boolean changeName = false;
			String layerName = outSourceName;
			if (dsf.getSourceManager().exists(outSourceName)) {
				layerName = dsf.getSourceManager().getUniqueName(outSourceName);
				changeName = true;
			}

			// register both sources
			String registerDB = "select register('" + dbDriver.getDriverId()
					+ "' ,'" + dbSource.getHost() + "'," + " '"
					+ dbSource.getPort() + "','" + dbSource.getDbName() + "','"
					+ dbSource.getUser() + "','" + dbSource.getPassword()
					+ "','" + schemaName + "','" + outSourceName + "','"
					+ layerName + "');";

			dsf.executeSQL(registerDB);
			// Do the migration
			String load = "create table " + layerName + " as select * "
					+ "from " + inSourceName + " ;";
			dsf.executeSQL(load, pm);

			if (changeName) {
				JOptionPane.showMessageDialog(null,
						"The table has been registered in the GeoCatalog with the name "
								+ layerName + " because of existing name");
			} else {
				JOptionPane.showMessageDialog(null,
						"The table has been registered in the GeoCatalog with the name "
								+ layerName);
			}

		} catch (ParseException e) {
			Services.getErrorManager().error("Error in the SQL statement.", e);
		} catch (SemanticException e) {
			Services.getErrorManager().error("Error in the SQL statement.", e);
		} catch (DriverException e) {
			Services.getErrorManager()
					.error("Cannot create the datasource.", e);
		} catch (ExecutionException e) {
			Services.getErrorManager()
					.error("Cannot create the datasource.", e);
		}

	}

}
