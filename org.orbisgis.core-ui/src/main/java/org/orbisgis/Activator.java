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

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.WarningListener;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.QueryManager;
import org.orbisgis.action.ActionControlsRegistry;
import org.orbisgis.errorManager.ErrorListener;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.pluginManager.PluginActivator;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.SystemListener;
import org.orbisgis.pluginManager.workspace.Workspace;
import org.orbisgis.pluginManager.workspace.WorkspaceListener;
import org.orbisgis.sql.customQuery.Geomark;
import org.orbisgis.views.documentCatalog.DocumentCatalogManager;
import org.orbisgis.views.documentCatalog.documents.MapDocument;
import org.orbisgis.window.EPWindowHelper;
import org.orbisgis.window.IWindow;
import org.orbisgis.windows.errors.ErrorFrame;
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

			private ErrorFrame error(ErrorMessage errorMessage) {
				IWindow[] wnds = EPWindowHelper
						.getWindows("org.orbisgis.windows.ErrorWindow");
				IWindow wnd;
				if (wnds.length == 0) {
					wnd = EPWindowHelper
							.createWindow("org.orbisgis.windows.ErrorWindow");
				} else {
					wnd = wnds[0];
				}
				((ErrorFrame) wnd).addError(errorMessage);
				return ((ErrorFrame) wnd);
			}

			public void error(String userMsg, Throwable e) {
				ErrorFrame ef = error(new ErrorMessage(userMsg, e, true));
				ef.showWindow();
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

		DefaultExtendedWorkspace defaultExtendedWorkspace = new DefaultExtendedWorkspace();
		Services
				.registerService(
						"org.orbisgis.ExtendedWorkspace",
						ExtendedWorkspace.class,
						"Gives access to directories inside the workspace. You can use the temporal folder in the workspace through this service. It lets the access to the results folder",
						defaultExtendedWorkspace);

		ExtendedWorkspace ews = (ExtendedWorkspace) Services
				.getService("org.orbisgis.ExtendedWorkspace");

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

		// Load windows status
		EPWindowHelper.loadStatus();
	}

	public void stop() {
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

			return exit == JOptionPane.YES_OPTION;
		} else {
			return true;
		}
	}
}