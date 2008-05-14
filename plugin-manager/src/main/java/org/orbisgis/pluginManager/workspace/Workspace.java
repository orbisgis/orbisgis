package org.orbisgis.pluginManager.workspace;

import java.io.File;
import java.io.IOException;

public interface Workspace {

	/**
	 * Gets a file that doesn't exist inside the workspace folder.
	 *
	 * @param prefix
	 *            Prefix of the new file
	 * @param suffix
	 *            Suffix of the new file
	 * @return A file that doesn't exist yet located in the workspace/.metadata
	 *         directory
	 */
	File getNewFile(String prefix, String suffix);

	/**
	 * Gets a file that doesn't exist inside the workspace folder.
	 *
	 * @return A file that doesn't exist yet located in the workspace/.metadata
	 *         directory
	 */
	File getNewFile();

	/**
	 * Returns a file in the workspace/.metadata directory
	 *
	 * @param name
	 *            relative path inside the workspace/.metadata directory
	 * @return
	 */
	File getFile(String name);

	/**
	 * Initializes the workspace. This method can be called before a workspace
	 * folder is specified, so it will manage to find a good workspace folder
	 * depending of the application: using a workspace by default, storing the
	 * latest used workspace and asking the user if there were no previous
	 * execution, etc.
	 *
	 * @param clean
	 *            If the workspace have to start a new workspace from scratch
	 * @throws IOException
	 */
	void init(boolean clean) throws IOException;

	/**
	 * Changes the workspace folder
	 *
	 * @param folder
	 * @throws IOException
	 */
	void setWorkspaceFolder(String folder) throws IOException;

	/**
	 * This invocation forces the workspace to be saved.
	 */
	void saveWorkspace();

	/**
	 * Adds a workspace listener
	 *
	 * @param listener
	 */
	void addWorkspaceListener(WorkspaceListener listener);

	/**
	 * Removes a workspace listener
	 *
	 * @param listener
	 */
	void removeWorkspaceListener(WorkspaceListener listener);
}