package org.orbisgis;

import java.io.File;
import java.io.IOException;

import org.orbisgis.pluginManager.workspace.Workspace;
import org.orbisgis.pluginManager.workspace.WorkspaceListener;

public class DefaultExtendedWorkspace implements ExtendedWorkspace {

	private static final String TEMP_FOLDER_NAME = "temp";
	private static final String RESULTS_FOLDER_NAME = "results";

	public DefaultExtendedWorkspace() {

		File tempDir = getWorkspace().getFile(TEMP_FOLDER_NAME);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}

		File resultsDir = getWorkspace().getFile(RESULTS_FOLDER_NAME);
		if (!resultsDir.exists()) {
			resultsDir.mkdirs();
		}
	}

	private Workspace getWorkspace() {
		return (Workspace) Services.getService("org.orbisgis.Workspace");
	}

	public File getFile(String name) {
		return getWorkspace().getFile(name);
	}

	public File getNewFile(String prefix, String suffix) {
		return getWorkspace().getNewFile(prefix, suffix);
	}

	public File getNewFile() {
		return getWorkspace().getNewFile();
	}

	public void init(boolean clean) throws IOException {
		getWorkspace().init(clean);
	}

	public void setWorkspaceFolder(String folder) throws IOException {
		getWorkspace().setWorkspaceFolder(folder);
	}

	public File getResultsFolder() {
		return getWorkspace().getFile(RESULTS_FOLDER_NAME);
	}

	public File getTempFolder() {
		return getWorkspace().getFile(TEMP_FOLDER_NAME);
	}

	public void addWorkspaceListener(WorkspaceListener listener) {
		getWorkspace().addWorkspaceListener(listener);
	}

	public void removeWorkspaceListener(WorkspaceListener listener) {
		getWorkspace().removeWorkspaceListener(listener);
	}

	public void saveWorkspace() {
		getWorkspace().saveWorkspace();
	}

}
