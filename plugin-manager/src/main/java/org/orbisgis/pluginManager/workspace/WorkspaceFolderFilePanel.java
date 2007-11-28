package org.orbisgis.pluginManager.workspace;

import org.orbisgis.pluginManager.ui.FolderFilePanel;

public class WorkspaceFolderFilePanel extends FolderFilePanel {

	public WorkspaceFolderFilePanel(String title, String dir) {
		super(title, dir);
	}

	@Override
	public String getId() {
		return "org.orbisgis.pluginManager.WorkspaceFileChooser";
	}
}
