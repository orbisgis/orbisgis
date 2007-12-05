package org.orbisgis.geocatalog;

import java.io.IOException;

import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.workspace.Workspace;
import org.orbisgis.pluginManager.workspace.WorkspaceFolderFilePanel;
import org.sif.UIFactory;

public class ChangeWorkspaceAction implements IGeocatalogAction {

	public void actionPerformed(Catalog catalog) {
		Workspace workspace = PluginManager.getWorkspace();
		WorkspaceFolderFilePanel panel = new WorkspaceFolderFilePanel(
				"Select the workspace folder", PluginManager.getHomeFolder()
						.getAbsolutePath());
		boolean accepted = UIFactory.showDialog(panel);
		if (accepted) {
			OrbisgisCore.saveStatus();
			try {
				workspace.setWorkspaceFolder(panel.getSelectedFile()
						.getAbsolutePath());
				OrbisgisCore.initialize();
			} catch (IOException e) {
				PluginManager.error("Cannot change workspace", e);
			}
		}
	}

	public boolean isEnabled(GeoCatalog geoCatalog) {
		return true;
	}

	public boolean isVisible(GeoCatalog geoCatalog) {
		return true;
	}

}
