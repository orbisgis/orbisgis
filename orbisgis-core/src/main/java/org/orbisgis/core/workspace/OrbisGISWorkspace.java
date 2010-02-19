package org.orbisgis.core.workspace;

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
import org.orbisgis.core.DataManager;
import org.orbisgis.core.OrbisgisCoreServices;
import org.orbisgis.core.Services;
import org.orbisgis.core.configuration.BasicConfiguration;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.images.IconLoader;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.configuration.EPConfigHelper;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.window.EPWindowHelper;
import org.orbisgis.utils.CollectionUtils;

public class OrbisGISWorkspace implements WorkspaceListener {

	private RefreshViewFunctionManagerListener refreshFMListener = new RefreshViewFunctionManagerListener();
	Workspace workspace;
	private final ErrorManager errorService;

	public OrbisGISWorkspace() {
		errorService = Services.getService(ErrorManager.class);
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
		UIFactory.setDefaultIcon(IconLoader.getIconUrl(IconNames.LOGO_MINI));
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
					.getCommaSeparated(memoryResources.toArray(new String[0]));

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
