package org.orbisgis.core.ui.plugins.toc;

import java.io.File;

import javax.swing.JOptionPane;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.progress.IProgressMonitor;

public class ExportInFileOperation implements BackgroundJob {

	private File savedFile;
	private DataSourceFactory dsf;
	private String sourceName;

	public ExportInFileOperation(DataSourceFactory dsf, String sourceName,
			File savedFile) {
		this.sourceName = sourceName;
		this.savedFile = savedFile;
		this.dsf = dsf;
	}

	@Override
	public String getTaskName() {
		return "Exporting in a file";
	}

	@Override
	public void run(IProgressMonitor pm) {

		String fileName = savedFile.getName();
		int index = fileName.lastIndexOf('.');
		if (index != -1) {
			fileName = fileName.substring(0, index);
		}
		final FileSourceDefinition def = new FileSourceDefinition(savedFile);
		dsf.getSourceManager().register(fileName, def);
		try {
			dsf.saveContents(fileName, dsf.getDataSource(sourceName), pm);
			JOptionPane.showMessageDialog(null,
					"The file has been exported and added in the geocatalog.");
		} catch (SemanticException e) {
			Services.getErrorManager().error("Error in the SQL statement.", e);
		} catch (DriverException e) {
			Services.getErrorManager()
					.error("Cannot create the datasource.", e);
		} catch (DriverLoadException e) {
			Services.getErrorManager().error("Cannot read the datasource.", e);
		} catch (DataSourceCreationException e) {
			Services.getErrorManager().error("Cannot read the datasource.", e);
		}

	}

}
