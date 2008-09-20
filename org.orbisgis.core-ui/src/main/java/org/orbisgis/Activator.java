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
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.WarningListener;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.QueryManagerListener;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.FunctionManagerListener;
import org.orbisgis.action.ActionControlsRegistry;
import org.orbisgis.editor.IEditor;
import org.orbisgis.errorManager.ErrorListener;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.outputManager.OutputManager;
import org.orbisgis.pluginManager.PluginActivator;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.SystemListener;
import org.orbisgis.sql.customQuery.Geomark;
import org.orbisgis.view.ViewManager;
import org.orbisgis.views.editor.EditorManager;
import org.orbisgis.window.EPWindowHelper;
import org.orbisgis.windows.errors.ErrorMessage;
import org.orbisgis.workspace.OGWorkspace;
import org.orbisgis.workspace.Workspace;
import org.orbisgis.workspace.WorkspaceListener;
import org.sif.UIFactory;

public class Activator implements PluginActivator {

	private RefreshViewFunctionManagerListener refreshFMListener = new RefreshViewFunctionManagerListener();
	private DataSourceFactory dsf;

	public void start() throws Exception {
		QueryManager.registerQuery(Geomark.class);

		OrbisgisCoreServices.installServices();

		// Initialize workspace
		initializeWorkspace();

		// Install the refresh listener
		PluginManager pm = (PluginManager) Services
				.getService(PluginManager.class);
		pm.addSystemListener(new SystemListener() {
			public void statusChanged() {
				ActionControlsRegistry.refresh();
			}
		});

		// Install the error listener
		final ErrorManager em = (ErrorManager) Services
				.getService(ErrorManager.class);
		em.addErrorListener(new ErrorListener() {
			
			private ErrorPanel ep = new ErrorPanel();
			
			public void warning(String userMsg, Throwable e) {
				error(userMsg, e, false);
			}

			private void error(String userMsg, Throwable e, boolean error) {
				ErrorMessage errorMessage = new ErrorMessage(userMsg, e, error);
				// Pipe the message to the output manager
				OutputManager om = (OutputManager) Services
						.getService(OutputManager.class);
				Color color;
				if (errorMessage.isError()) {
					color = Color.red;
				} else {
					color = Color.orange;
				}
				om.append(errorMessage.getUserMessage(), color);
				om.append(errorMessage.getTrace(), color);
				om.makeVisible();

				// Show message to the user and send log
				if (errorMessage.isError()) {
					ep.show("Error", errorMessage.getLongMessage());
					if (ep.sendLog()) {
						Thread t = new Thread(new SendLog());
						t.setPriority(Thread.MIN_PRIORITY);
						t.start();

					}
				}
			}

			public void error(String userMsg, Throwable e) {
				error(userMsg, e, true);
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
				.getService(Workspace.class);

		workspace.addWorkspaceListener(new WorkspaceListener() {

			public void workspaceChanged(File oldWorkspace, File newWorkspace) {
				initializeWorkspace();
			}

			public void saveWorkspace() {
				stop();
			}

		});

		// Listen FunctionManager and QueryManager changes to refresh
		// geocognition view
		FunctionManager.addFunctionManagerListener(refreshFMListener);
		QueryManager.addQueryManagerListener(refreshFMListener);
	}

	private void initializeWorkspace() {

		// Configuration of workspace directories
		Workspace workspace = (Workspace) Services
				.getService(Workspace.class);

		File sourcesDir = workspace.getFile("sources");
		if (!sourcesDir.exists()) {
			sourcesDir.mkdirs();
		}

		OGWorkspace ews = (OGWorkspace) Services
				.getService(OGWorkspace.class);

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
		FunctionManager.removeFunctionManagerListener(refreshFMListener);
		QueryManager.removeQueryManagerListener(refreshFMListener);

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
				.getService(DataManager.class);
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
				.getService(EditorManager.class);
		IEditor[] editors = em.getEditors();
		for (IEditor editor : editors) {
			if (!em.closeEditor(editor)) {
				ret = false;
				break;
			}
		}

		return ret;
	}

	private final class RefreshViewFunctionManagerListener implements
			FunctionManagerListener, QueryManagerListener {
		public void functionRemoved(String functionName) {
			refreshGeocognition();
		}

		private void refreshGeocognition() {
			ViewManager vm = Services.getService(ViewManager.class);
			if (vm != null) {
				Component view = vm.getView("org.orbisgis.views.Geocognition");
				if (view != null) {
					view.repaint();
				}
			}
		}

		public void functionAdded(String functionName) {
			refreshGeocognition();
		}

		public void queryAdded(String functionName) {
			refreshGeocognition();
		}

		public void queryRemoved(String functionName) {
			refreshGeocognition();
		}
	}

}