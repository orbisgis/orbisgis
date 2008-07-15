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
package org.orbisgis;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.WarningListener;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.QueryManager;
import org.orbisgis.action.ActionControlsRegistry;
import org.orbisgis.editor.IEditor;
import org.orbisgis.errorManager.ErrorListener;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.pluginManager.PluginActivator;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.SystemListener;
import org.orbisgis.pluginManager.workspace.Workspace;
import org.orbisgis.pluginManager.workspace.WorkspaceListener;
import org.orbisgis.renderer.symbol.collection.DefaultSymbolCollection;
import org.orbisgis.sql.customQuery.Geomark;
import org.orbisgis.views.documentCatalog.DocumentCatalogManager;
import org.orbisgis.views.documentCatalog.documents.MapDocument;
import org.orbisgis.views.editor.EditorManager;
import org.orbisgis.views.outputView.OutputManager;
import org.orbisgis.window.EPWindowHelper;
import org.orbisgis.windows.errors.ErrorMessage;
import org.sif.UIFactory;

public class Activator implements PluginActivator {

	private DataSourceFactory dsf;

	public void start() throws Exception {
		QueryManager.registerQuery(new Geomark());

		// Initialize workspace
		initializeWorkspace();

		// Install the refresh listener
		PluginManager pm = (PluginManager) Services
				.getService("org.orbisgis.PluginManager");
		pm.addSystemListener(new SystemListener() {
			public void statusChanged() {
				ActionControlsRegistry.refresh();
			}
		});

		// Install the error listener
		final ErrorManager em = (ErrorManager) Services
				.getService("org.orbisgis.ErrorManager");
		em.addErrorListener(new ErrorListener() {

			public void warning(String userMsg, Throwable e) {
				error(new ErrorMessage(userMsg, e, false));
			}

			private void error(ErrorMessage errorMessage) {
				OutputManager om = (OutputManager) Services
						.getService("org.orbisgis.OutputManager");
				Color color;
				if (errorMessage.isError()) {
					color = Color.red;
				} else {
					color = Color.orange;
				}
				om.append(errorMessage.getUserMessage(), color);
				om.append(errorMessage.getTrace(), color);
				om.makeVisible();

				if (errorMessage.isError()) {
					JOptionPane.showMessageDialog(null, errorMessage
							.getLongMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}

			public void error(String userMsg, Throwable e) {
				error(new ErrorMessage(userMsg, e, true));
			}

		});

		// Pipeline the warnings in gdms to the warning system in the
		// application
		dsf.setWarninglistener(new WarningListener() {

			public void throwWarning(String msg) {
				em.warning(msg, null);
			}

			public void throwWarning(String msg, Throwable t, Object source) {
				em.warning(msg, t);
			}

		});

		// Listen workspace changes
		Workspace workspace = (Workspace) Services
				.getService("org.orbisgis.Workspace");

		workspace.addWorkspaceListener(new WorkspaceListener() {

			public void workspaceChanged(File oldWorkspace, File newWorkspace) {
				initializeWorkspace();
			}

			public void saveWorkspace() {
				stop();
			}

		});

		DocumentCatalogManager dcm = (DocumentCatalogManager) Services
				.getService("org.orbisgis.DocumentCatalogManager");
		if (dcm.isEmpty()) {
			MapDocument mapDocument = new MapDocument();
			mapDocument.setName("Map1");
			dcm.addDocument(mapDocument);
			dcm.openDocument(mapDocument);
		}
	}

	private void initializeWorkspace() {

		// Configuration of workspace directories
		Workspace workspace = (Workspace) Services
				.getService("org.orbisgis.Workspace");

		File sourcesDir = workspace.getFile("sources");
		if (!sourcesDir.exists()) {
			sourcesDir.mkdirs();
		}

		DefaultOGWorkspace defaultOGWorkspace = new DefaultOGWorkspace();
		Services
				.registerService(
						"org.orbisgis.OGWorkspace",
						OGWorkspace.class,
						"Gives access to directories inside the workspace. You can use the temporal folder in the workspace through this service. It lets the access to the results folder",
						defaultOGWorkspace);

		OGWorkspace ews = (OGWorkspace) Services
				.getService("org.orbisgis.OGWorkspace");

		dsf = new DataSourceFactory(sourcesDir.getAbsolutePath(), ews
				.getTempFolder().getAbsolutePath());
		dsf.setResultDir(ews.getResultsFolder());

		// Installation of the service
		Services
				.registerService(
						"org.orbisgis.DataManager",
						DataManager.class,
						"Access to the sources, to its properties (indexes, etc.) and its contents, either raster or vectorial",
						new DefaultDataManager(dsf));

		// Link with SIF factory
		File sifDir = workspace.getFile("sif");
		if (!sifDir.exists()) {
			sifDir.mkdirs();
		}
		UIFactory.setPersistencyDirectory(sifDir);
		UIFactory.setTempDirectory(ews.getTempFolder());
		UIFactory.setDefaultIcon(Activator.class
				.getResource("/org/orbisgis/images/mini_orbisgis.png"));

		// Load symbol collection and install symbol service
		File symbolCollectionFile = ews
				.getFile("org.orbisgis.symbol-collection.xml");
		DefaultSymbolCollection col = new DefaultSymbolCollection(
				symbolCollectionFile);
		if (symbolCollectionFile.exists()) {
			try {
				col.loadCollection();
			} catch (JAXBException e) {
				Services.getErrorManager().error(
						"Cannot load symbol collection", e);
			} catch (IOException e) {
				Services.getErrorManager().error(
						"Cannot load symbol collection", e);
			} catch (IncompatibleVersionException e) {
				Services.getErrorManager().error(
						"Cannot load symbol collection", e);
			}
		}
		Services.registerService("org.orbisgis.SymbolManager",
				SymbolManager.class, "Provides methods to create symbols and "
						+ "access the symbol collection",
				new DefaultSymbolManager(symbolCollectionFile));

		// Load windows status
		EPWindowHelper.loadStatus();
	}

	public void stop() {
		SymbolManager sm = (SymbolManager) Services
				.getService("org.orbisgis.SymbolManager");
		try {
			sm.saveXML();
		} catch (JAXBException e1) {
			Services.getErrorManager().error("Cannot save symbol collection",
					e1);
		} catch (IOException e1) {
			Services.getErrorManager().error("Cannot save symbol collection",
					e1);
		}

		EPWindowHelper.saveStatus();
		try {
			dsf.getSourceManager().saveStatus();
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot save source information",
					e);
		}
	}

	public boolean allowStop() {
		DataManager dataManager = (DataManager) Services
				.getService("org.orbisgis.DataManager");
		SourceManager sourceManager = dataManager.getSourceManager();
		String[] sourceNames = sourceManager.getSourceNames();

		ArrayList<String> memoryResources = new ArrayList<String>();
		for (String sourceName : sourceNames) {
			try {
				int sourceType = sourceManager.getSourceType(sourceName);
				if ((sourceType & SourceManager.MEMORY) == SourceManager.MEMORY) {
					memoryResources.add(sourceName);
				}
			} catch (NoSuchTableException e) {
			}
		}

		boolean ret;
		if (memoryResources.size() > 0) {
			String resourceList = CollectionUtils
					.getCommaSeparated(memoryResources.toArray(new String[0]));

			int exit = JOptionPane
					.showConfirmDialog(
							null,
							"The following resources are stored "
									+ "in memory and its content may be lost: \n"
									+ resourceList
									+ ".\nDo you want to exit"
									+ " and probably lose the content of those sources?",
							"Loose object resources?",
							JOptionPane.YES_NO_OPTION);

			ret = (exit == JOptionPane.YES_OPTION);
		} else {
			ret = true;
		}

		EditorManager em = (EditorManager) Services
				.getService("org.orbisgis.EditorManager");
		IEditor[] editors = em.getEditors();
		for (IEditor editor : editors) {
			if (!em.closeEditor(editor)) {
				ret = false;
				break;
			}
		}

		return ret;
	}
}