package org.orbisgis.core.ui;

import java.io.File;
import java.io.IOException;

import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.core.workspace.WorkspaceListener;

public class TestWorkspace implements Workspace {

	private File workspaceFolder;

	@Override
	public void addWorkspaceListener(WorkspaceListener listener) {
	}

	@Override
	public File getFile(String name) {
		return new File(workspaceFolder, name);
	}

	@Override
	public File getNewFile(String prefix, String suffix) {
		try {
			return File.createTempFile(prefix, suffix, workspaceFolder);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public File getNewFile() {
		return getNewFile("javaexecutiontest", "txt");
	}

	@Override
	public void init(boolean clean) {
	}

	@Override
	public void removeWorkspaceListener(WorkspaceListener listener) {
	}

	@Override
	public void saveWorkspace() throws IOException {
	}

	@Override
	public void setWorkspaceFolder(String folder) throws IOException {
		workspaceFolder = new File(folder);
	}

	@Override
	public File getTempFolder() {
		return new File(workspaceFolder, "temp");
	}

	@Override
	public String getWorkspaceFolder() {
		return workspaceFolder.getAbsolutePath();
	}

}
