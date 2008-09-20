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
package org.orbisgis.views.geocatalog.actions.create;

import java.io.File;
import java.util.ArrayList;

import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.ReadWriteDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.resource.Folder;
import org.orbisgis.resource.IResource;
import org.orbisgis.ui.sif.ChoosePanel;
import org.orbisgis.views.geocatalog.Catalog;
import org.orbisgis.views.geocatalog.action.IResourceAction;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class CreateFileResource implements IResourceAction {

	public boolean accepts(IResource resource) {
		return resource.getResourceType() instanceof Folder;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount <= 1;
	}

	public void execute(Catalog catalog, IResource selectedNode) {
		// Get the non raster writable drivers
		DataManager dm = (DataManager) Services
				.getService(DataManager.class);
		DriverManager driverManager = dm.getSourceManager().getDriverManager();
		String[] driverNames = driverManager.getDriverNames();
		ArrayList<String> filtered = new ArrayList<String>();
		for (String driverName : driverNames) {
			ReadOnlyDriver rod = (ReadOnlyDriver) driverManager
					.getDriver(driverName);
			if ((rod.getType() & SourceManager.RASTER) == 0) {
				if ((rod.getType() & SourceManager.FILE) == SourceManager.FILE) {
					if ((rod.getType() & SourceManager.VECTORIAL) == SourceManager.VECTORIAL) {
						if (rod instanceof ReadWriteDriver) {
							filtered.add(driverName);
						}
					}
				}
			}
		}
		createSource(dm, driverManager, filtered);
	}

	static void createSource(DataManager dm, DriverManager driverManager,
			ArrayList<String> filtered) {
		String[] driverNames;
		driverNames = filtered.toArray(new String[0]);
		String[] typeNames = new String[driverNames.length];
		for (int i = 0; i < driverNames.length; i++) {
			ReadOnlyDriver rod = (ReadOnlyDriver) driverManager
					.getDriver(driverNames[i]);
			typeNames[i] = dm.getSourceManager().getSourceTypeName(
					rod.getType());
		}

		ChoosePanel cp = new ChoosePanel("Select the type of source",
				typeNames, driverNames);
		if (UIFactory.showDialog(cp)) {
			// Create wizard
			UIPanel[] wizardPanels = new UIPanel[2];
			ReadWriteDriver driver = (ReadWriteDriver) driverManager
					.getDriver((String) cp.getSelected());
			boolean file;
			if ((driver.getType() & SourceManager.FILE) == SourceManager.FILE) {
				file = true;
			} else if ((driver.getType() & SourceManager.DB) == SourceManager.DB) {
				file = false;
			} else {
				Services.getErrorManager().error(
						"Unsupported source type: " + cp.getSelected());
				return;
			}
			SaveFilePanel saveFilePanel = new SaveFilePanel(null,
					"Select the file to create");
			if (file) {
				saveFilePanel.setFileMustNotExist(true);
				saveFilePanel.addAllFilter(driver.getName());
				wizardPanels[0] = saveFilePanel;
			} else {
				throw new UnsupportedOperationException("Not implemented yet");
			}
			MetadataCreation mc = new MetadataCreation(driver);
			wizardPanels[1] = mc;
			if (UIFactory.showDialog(wizardPanels)) {
				DataSourceCreation dsc = null;
				DataSourceDefinition dsd = null;
				String name = null;
				if (file) {
					File selectedFile = saveFilePanel.getSelectedFile();
					selectedFile = new File(((FileDriver) driver)
							.completeFileName(selectedFile.getAbsolutePath()));
					dsc = new FileSourceCreation(selectedFile, mc.getMetadata());
					dsd = new FileSourceDefinition(selectedFile);
					name = selectedFile.getName();
				} else {
					throw new UnsupportedOperationException(
							"Not implemented yet");
				}

				try {
					dm.getDSF().createDataSource(dsc);
				} catch (DriverException e) {
					Services.getErrorManager().error(
							"Cannot create source: " + dsc, e);
					return;
				}
				name = dm.registerWithUniqueName(name, dsd);
				// catalog.addResources(new IResource[] { ResourceFactory
				// .createResource(name, new GdmsSource()) });
			}
		}
	}
}
