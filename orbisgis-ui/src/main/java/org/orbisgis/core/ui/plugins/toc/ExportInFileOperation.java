/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.toc;

import java.io.File;

import javax.swing.JOptionPane;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.geocatalog.Catalog;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

public class ExportInFileOperation implements BackgroundJob {

	private File savedFile;
	private DataSourceFactory dsf;
	private String sourceName;
	private WorkbenchFrame frame;

	public ExportInFileOperation(DataSourceFactory dsf, String sourceName,
			File savedFile, WorkbenchFrame frame) {
		this.sourceName = sourceName;
		this.savedFile = savedFile;
		this.dsf = dsf;
		this.frame = frame;
	}

	@Override
	public String getTaskName() {
		return I18N.getString("orbisgis.org.orbisgis.exportInFile");
	}

	@Override
	public void run(ProgressMonitor pm) {

		String fileName = savedFile.getName();
		int index = fileName.lastIndexOf('.');
		if (index != -1) {
			fileName = fileName.substring(0, index);
		}
		final FileSourceDefinition def = new FileSourceDefinition(savedFile, DriverManager.DEFAULT_SINGLE_TABLE_NAME);
		final SourceManager sourceManager = dsf.getSourceManager();
		if (sourceManager.exists(fileName)) {
			fileName = sourceManager.getUniqueName(fileName);
		}
		sourceManager.register(fileName, def);
		try {
			dsf.saveContents(fileName, dsf.getDataSource(sourceName), pm);
			JOptionPane.showMessageDialog(UIFactory.getMainFrame(),
					I18N.getString("orbisgis.org.orbisgis.exportInFile.geocatalog"));
		} catch (DriverException e) {
			ErrorMessages.error(ErrorMessages.CannotCreateDataSource, e);
		} catch (DriverLoadException e) {
			ErrorMessages.error(ErrorMessages.CannotReadDataSource, e);
		} catch (DataSourceCreationException e) {
			ErrorMessages.error(ErrorMessages.CannotReadDataSource, e);
		} catch (NoSuchTableException e) {
			ErrorMessages.error(ErrorMessages.CannotReadDataSource, e);
		}

		if (frame != null && frame instanceof Catalog) {
			((Catalog) frame).repaint();
		}

	}

}
