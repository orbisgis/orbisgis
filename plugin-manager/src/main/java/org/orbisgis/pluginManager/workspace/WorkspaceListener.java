package org.orbisgis.pluginManager.workspace;

import java.io.File;

public interface WorkspaceListener {

	/**
	 * The workspace have been asked to be saved
	 */
	void saveWorkspace();

	/**
	 * The workspace directory has changed
	 *
	 * @param oldWorkspace
	 * @param newWorkspace
	 */
	void workspaceChanged(File oldWorkspace, File newWorkspace);

}
