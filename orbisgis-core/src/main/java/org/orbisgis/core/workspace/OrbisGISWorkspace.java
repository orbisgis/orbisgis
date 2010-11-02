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

package org.orbisgis.core.workspace;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.gdms.data.DataSourceFinalizationException;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.QueryManagerListener;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.FunctionManagerListener;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.OrbisgisCoreServices;
import org.orbisgis.core.Services;
import org.orbisgis.core.configuration.BasicConfiguration;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.configuration.EPConfigHelper;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;
import org.orbisgis.core.ui.window.EPWindowHelper;
import org.orbisgis.utils.CollectionUtils;

public class OrbisGISWorkspace implements WorkspaceListener {

	private RefreshViewFunctionManagerListener refreshFMListener = new RefreshViewFunctionManagerListener();
	Workspace workspace;

	public OrbisGISWorkspace() {
		initializeWorkspace();
		workspace = Services.getService(Workspace.class);
		workspace.addWorkspaceListener(this);
		FunctionManager.addFunctionManagerListener(refreshFMListener);
		QueryManager.addQueryManagerListener(refreshFMListener);

		Services
				.registerService(
						OrbisGISWorkspace.class,
						"Gives access to the plug-in system manager. It can stop the application, access the log files, etc.",
						this);
	}

	public void workspaceChanged(File oldWorkspace, File newWorkspace) {
		initializeWorkspace();
	}

	public void saveWorkspace() {
		stop();
	}

	private void initializeWorkspace() {
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
		UIFactory.setTempDirectory(Services.getService(IOGWorkspace.class)
				.getTempFolder());
		UIFactory.setDefaultIcon(IconLoader.getIconUrl("mini_orbisgis.png"));
		WorkbenchContext wbContext = Services
				.getService(WorkbenchContext.class);
		UIFactory.setMainFrame(wbContext.getWorkbench().getFrame());
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
					.getCommaSeparated(memoryResources.toArray(new String[memoryResources.size()]));

			int exit = JOptionPane
					.showConfirmDialog(
							null,
							"The following resources are stored "
									+ "in memory and its content may be lost: \n"
									+ resourceList
									+ ".\nDo you want to exit"
									+ " and probably lose the content of those sources?",
							"Lose object resources?", JOptionPane.YES_NO_OPTION);

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

	public boolean stopPlugins() {
		if (!allowStop())
			return false;
		try {
			stop();
		} catch (Exception e) {
			Services.getErrorManager().error("Error stoping plug-in", e);
		}

		try {
			Services.getService(DataManager.class).getDataSourceFactory()
					.freeResources();
		} catch (DataSourceFinalizationException e) {
			Services.getErrorManager().error("Error cleaning temp folder", e);
		}
		System.exit(0);
		return true;
	}

	private final class RefreshViewFunctionManagerListener implements
			FunctionManagerListener, QueryManagerListener {
		public void functionRemoved(String functionName) {
			refreshGeocognition();
		}

		private void refreshGeocognition() {
			WorkbenchContext wbContext = Services
					.getService(WorkbenchContext.class);
			if (wbContext != null) {
				Component view = wbContext.getWorkbench().getFrame().getView(
						"Geocognition");
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
