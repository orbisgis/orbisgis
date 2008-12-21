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

import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.QueryManagerListener;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.FunctionManagerListener;
import org.orbisgis.action.ActionControlsRegistry;
import org.orbisgis.configuration.BasicConfiguration;
import org.orbisgis.configuration.EPConfigHelper;
import org.orbisgis.editor.IEditor;
import org.orbisgis.errorManager.ErrorListener;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.outputManager.OutputManager;
import org.orbisgis.pluginManager.PluginActivator;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.SystemListener;
import org.orbisgis.sql.customQuery.Geomark;
import org.orbisgis.updates.DefaultUpdateManager;
import org.orbisgis.updates.UpdateManager;
import org.orbisgis.view.ViewManager;
import org.orbisgis.views.editor.EditorManager;
import org.orbisgis.window.EPWindowHelper;
import org.orbisgis.windows.errors.ErrorMessage;
import org.orbisgis.windows.mainFrame.UIManager;
import org.orbisgis.workspace.OGWorkspace;
import org.orbisgis.workspace.Workspace;
import org.orbisgis.workspace.WorkspaceListener;
import org.sif.UIFactory;

public class Activator implements PluginActivator {

	private RefreshViewFunctionManagerListener refreshFMListener = new RefreshViewFunctionManagerListener();

	private ArrayList<ErrorMessage> initMessages = new ArrayList<ErrorMessage>();

	public void start() throws Exception {
		final ErrorManager errorService = Services
				.getService(ErrorManager.class);
		ErrorManager em = errorService;
		ErrorListener cacheListener = new ErrorListener() {

			public void warning(String userMsg, Throwable e) {
				initMessages.add(new ErrorMessage(userMsg, e, false));
			}

			public void error(String userMsg, Throwable e) {
				initMessages.add(new ErrorMessage(userMsg, e, true));
			}

		};
		em.addErrorListener(cacheListener);

		// Register UI queries
		QueryManager.registerQuery(Geomark.class);

		// Install OrbisGIS core services
		OrbisgisCoreServices.installServices();

		// Initialize workspace
		initializeWorkspace();

		// Install update manager
		UpdateManager um = new DefaultUpdateManager();
		Services.registerService(UpdateManager.class,
				"Service to install updates from a remote site", um);

		// Initialize configuration
		EPConfigHelper.loadAndApplyConfigurations();

		// Install the refresh listener
		PluginManager pm = Services.getService(PluginManager.class);
		pm.addSystemListener(new SystemListener() {
			public void statusChanged() {
				ActionControlsRegistry.refresh();
			}
		});

		// Listen workspace changes
		Workspace workspace = Services.getService(Workspace.class);
		workspace.addWorkspaceListener(new WorkspaceListener() {

			public void workspaceChanged(File oldWorkspace, File newWorkspace) {
				try {
					initializeWorkspace();
				} catch (DriverException e) {
					errorService.error("Cannot initialize workspace", e);
				}
			}

			public void saveWorkspace() {
				stop();
			}

		});

		// Listen FunctionManager and QueryManager changes to refresh
		// geocognition view
		FunctionManager.addFunctionManagerListener(refreshFMListener);
		QueryManager.addQueryManagerListener(refreshFMListener);

		// Install the error listener
		em.addErrorListener(new FilteringErrorListener());

		// Put in output the errors raised at start up
		errorService.removeErrorListener(cacheListener);
		for (ErrorMessage msg : initMessages) {
			if (msg.isError()) {
				errorService.error(msg.getUserMessage(), msg.getException());
			} else {
				errorService.warning(msg.getUserMessage(), msg.getException());
			}
		}

		// Search for updates
		// um = Services.getService(UpdateManager.class);
		// if (um.isSearchAtStartup()) {
		// um.startSearch();
		// }

	}

	private void initializeWorkspace() throws DriverException {
		// Configuration of workspace directories
		Workspace workspace = Services.getService(Workspace.class);

		// Change DataSourceFactory and SourceManager folders
		OrbisgisCoreServices.installWorkspaceServices();

		// Link with SIF factory
		File sifDir = workspace.getFile("sif");
		if (!sifDir.exists()) {
			sifDir.mkdirs();
		}

		// Load windows status
		EPWindowHelper.loadStatus();

		UIFactory.setPersistencyDirectory(sifDir);
		UIFactory.setTempDirectory(Services.getService(OGWorkspace.class)
				.getTempFolder());
		UIFactory.setDefaultIcon(Activator.class
				.getResource("/org/orbisgis/images/mini_orbisgis.png"));
		UIFactory.setMainFrame(Services.getService(UIManager.class)
				.getMainFrame());
	}

	public void stop() {
		FunctionManager.removeFunctionManagerListener(refreshFMListener);
		QueryManager.removeQueryManagerListener(refreshFMListener);

		EPWindowHelper.saveStatus();
		try {
			Services.getService(DataManager.class).getSourceManager()
					.saveStatus();
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot save source information",
					e);
		}

		EPConfigHelper.saveAppliedConfigurations();
		BasicConfiguration bc = Services.getService(BasicConfiguration.class);
		bc.save();
	}

	public boolean allowStop() {
		DataManager dataManager = Services.getService(DataManager.class);
		SourceManager sourceManager = dataManager.getSourceManager();
		String[] sourceNames = sourceManager.getSourceNames();

		ArrayList<String> memoryResources = new ArrayList<String>();
		for (String sourceName : sourceNames) {
			int sourceType = sourceManager.getSource(sourceName).getType();
			if ((sourceType & SourceManager.MEMORY) == SourceManager.MEMORY) {
				memoryResources.add(sourceName);
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
							"Lose object resources?",
							JOptionPane.YES_NO_OPTION);

			ret = (exit == JOptionPane.YES_OPTION);
		} else {
			ret = true;
		}

		EditorManager em = Services.getService(EditorManager.class);
		IEditor[] editors = em.getEditors();
		for (IEditor editor : editors) {
			try {
				if (!em.closeEditor(editor)) {
					ret = false;
					break;
				}
			} catch (IllegalArgumentException e) {
				// Ignore. Some editor closings lead to the closing of others
			}
		}

		return ret;
	}

	private final class FilteringErrorListener implements ErrorListener {
		private ErrorPanel ep = new ErrorPanel();

		private String lastMessage = null;
		private long lastTimeStamp = 0;
		private boolean ignoredMsgShown = false;

		public void warning(String userMsg, Throwable e) {
			error(userMsg, e, false);
		}

		private boolean looksLikePrevious(String currentMsg) {
			if (lastMessage == null || (currentMsg == null)) {
				lastMessage = currentMsg;
				return false;
			} else {
				String currentMsgStart = currentMsg.substring(0, currentMsg
						.length() / 4);
				String currentMsgEnd = currentMsg.substring((3 * currentMsg
						.length()) / 4);
				return (lastMessage.startsWith(currentMsgStart) || lastMessage
						.endsWith(currentMsgEnd));
			}
		}

		private boolean shouldRepport(String msg) {
			if (looksLikePrevious(msg)
					&& (System.currentTimeMillis() - lastTimeStamp) < 5000) {
				if (!ignoredMsgShown) {
					ignoredMsgShown = true;
					reportOutputManager(new ErrorMessage("Similar error "
							+ "messages not shown", null, false));
				}
				return false;
			} else {
				lastMessage = msg;
				ignoredMsgShown = false;
				return true;
			}
		}

		private void error(String userMsg, Throwable e, boolean error) {
			ErrorMessage errorMessage = new ErrorMessage(userMsg, e, error);

			// Show the message to the user
			if (shouldRepport(userMsg)) {
				// Pipe the message to the output manager
				reportOutputManager(errorMessage);

				if (errorMessage.isError()) {
					ep.show("Error", errorMessage.getLongMessage());
					// Send log
					boolean isBug = (e instanceof RuntimeException)
							|| (e instanceof Error);
					if (isBug && ep.sendLog()) {
						Thread t = new Thread(new SendLog());
						t.setPriority(Thread.MIN_PRIORITY);
						t.start();

					}
				}
			}
			lastTimeStamp = System.currentTimeMillis();
		}

		private void reportOutputManager(ErrorMessage errorMessage) {
			OutputManager om = Services.getService(OutputManager.class);
			Color color;
			if (errorMessage.isError()) {
				color = Color.red;
			} else {
				color = new Color(128, 128, 0);
			}
			om.print(errorMessage.getLongMessage() + "\n", color);
		}

		public void error(String userMsg, Throwable e) {
			error(userMsg, e, true);
		}
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