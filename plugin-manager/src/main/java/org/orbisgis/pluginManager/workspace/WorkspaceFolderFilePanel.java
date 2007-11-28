package org.orbisgis.pluginManager.workspace;

import org.orbisgis.pluginManager.ui.FolderFilePanel;

public class WorkspaceFolderFilePanel extends FolderFilePanel {

	public static final String SIF_ID = "org.orbisgis.pluginManager.WorkspaceFileChooser";

	public WorkspaceFolderFilePanel(String title, String dir) {
		super(title, dir);
	}

	@Override
	public String getId() {
		return SIF_ID;
	}
}
