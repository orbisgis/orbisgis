/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */

package org.orbisgis.core.ui.plugins.views.geocatalog;

import java.io.File;

import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.InitializationException;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.AndDriverFilter;
import org.gdms.source.FileDriverFilter;
import org.gdms.source.NotDriverFilter;
import org.gdms.source.RasterDriverFilter;
import org.gdms.source.SourceManager;
import org.gdms.source.WritableDriverFilter;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.components.sif.ChoosePanel;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.geocatalog.actions.create.MetadataCreation;
import org.orbisgis.utils.FileUtils;
import org.orbisgis.utils.I18N;

public class GeocatalogCreateFileSourcePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		DataManager dm = Services.getService(DataManager.class);
		String[] res = getPlugInContext().getSelectedSources();
		if (res.length == 0) {
			execute(dm.getSourceManager(), null);
		} else {
			for (String resource : res) {
				execute(dm.getSourceManager(), resource);
			}
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getGeocatalog();
		context.getFeatureInstaller()
				.addPopupMenuItem(
						frame,
						this,
						new String[] { Names.POPUP_GEOCATALOG_CREATE_SRC_PATH1,
								Names.POPUP_GEOCATALOG_CREATE_SRC_PATH2 },
						Names.POPUP_GEOCATALOG_CREATE_SRC_GROUP, false, null,
						wbContext);
	}

	public void execute(SourceManager sourceManager, String sourceName) {
		// Get the non raster writable drivers
		DataManager dm = (DataManager) Services.getService(DataManager.class);
		DriverManager driverManager = sourceManager.getDriverManager();

		Driver[] filtered = driverManager.getDrivers(new AndDriverFilter(
				new FileDriverFilter(), new NotDriverFilter(
						new RasterDriverFilter()), new WritableDriverFilter()));

		createSource(dm, driverManager, filtered);
	}

	static void createSource(DataManager dm, DriverManager driverManager,
			Driver[] filtered) {
		String[] typeNames = new String[filtered.length];
		String[] driverNames = new String[filtered.length];
		SourceManager sourceManager = dm.getSourceManager();
		for (int i = 0; i < filtered.length; i++) {
			driverNames[i] = filtered[i].getDriverId();
			Driver rod = filtered[i];
			typeNames[i] = rod.getTypeDescription();
		}

		ChoosePanel cp = new ChoosePanel(
				I18N
						.getString("orbisgis.org.orbisgis.core.geocatalog.selectTypeSource"),
				typeNames, driverNames);	
		if (UIFactory.showDialog(cp)) {
			// Create wizard
			UIPanel[] wizardPanels = new UIPanel[2];
			Driver driver = driverManager.getDriver((String) cp.getSelected());
			boolean file;
			if ((driver.getType() & SourceManager.FILE) == SourceManager.FILE) {
				file = true;
			} else if ((driver.getType() & SourceManager.DB) == SourceManager.DB) {
				file = false;
			} else {
				ErrorMessages.error(ErrorMessages.UnsupportedSourceType + " "
						+ cp.getSelected());
				return;
			}
			SaveFilePanel saveFilePanel = new SaveFilePanel(null, I18N
					.getString("orbisgis.org.core.selectFileCreate"));
			if (file) {
				saveFilePanel.setFileMustNotExist(true);
				saveFilePanel.addFilter(((FileDriver) driver)
						.getFileExtensions(), driver.getTypeDescription());
				wizardPanels[0] = saveFilePanel;
			} else {
				throw new UnsupportedOperationException(
						ErrorMessages.NotImplementedYet);
			}
			MetadataCreation mc = new MetadataCreation(driver);
			wizardPanels[1] = mc;
			if (UIFactory.showDialog(wizardPanels)) {
				DataSourceCreation dsc = null;
				DataSourceDefinition dsd = null;
				String name = null;
				if (file) {
					File selectedFile = saveFilePanel.getSelectedFile();
					String selectedPath = selectedFile.getAbsolutePath();
					boolean hasExtension = false;
					String[] extensions = ((FileDriver) driver)
							.getFileExtensions();
					for (String extension : extensions) {
						if (selectedPath.toLowerCase().endsWith(
								extension.toLowerCase())) {
							hasExtension = true;
							break;
						}
					}
					if (!hasExtension) {
						selectedFile = new File(selectedPath + "."
								+ extensions[0]);
					}
					dsc = new FileSourceCreation(selectedFile, mc.getMetadata());
					dsd = new FileSourceDefinition(selectedFile, DriverManager.DEFAULT_SINGLE_TABLE_NAME);
					name = FileUtils.getFileNameWithoutExtensionU(selectedFile);
				} else {
					throw new UnsupportedOperationException(
							ErrorMessages.NotImplementedYet);
				}

				try {
					dm.getDataSourceFactory().createDataSource(dsc);
				} catch (DriverException e) {
					ErrorMessages.error(ErrorMessages.CannotCreateSource + " "
							+ dsc, e);
					return;
				}

				name = sourceManager.getUniqueName(name);
				try {
					sourceManager.register(name, dsd);
				} catch (SourceAlreadyExistsException e) {
					ErrorMessages.error(ErrorMessages.SourceAlreadyExists, e);
				} catch (InitializationException e) {
					ErrorMessages.error(ErrorMessages.CannotInitializeSource, e);
				}
			}
		}
	}

	public boolean isEnabled() {
		return true;
	}
}
