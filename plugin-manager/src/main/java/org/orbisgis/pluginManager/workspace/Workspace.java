package org.orbisgis.pluginManager.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;

public class Workspace {

	private File workspaceFolder;

	/**
	 * @param prefix
	 */
	public File createNewFile(String prefix, String suffix) {
		File ret = null;
		do {
			ret = new File(getMetadataFolder(), prefix
					+ System.currentTimeMillis() + suffix);
		} while (ret.exists());

		return ret;
	}

	/**
	 * @param name
	 *            relative path inside the workspace base path
	 * @return
	 */
	public File getFile(String name) {
		return new File(getMetadataFolder(), name);
	}

	public void init() {
		while (!validWorkspace()) {
			File homeFolder = PluginManager.getHomeFolder();
			File currentWorkspace = new File(homeFolder, "currentWorkspace.txt");
			if (!currentWorkspace.exists()) {
				WorkspaceFolderFilePanel panel = new WorkspaceFolderFilePanel(
						"Select the workspace folder", PluginManager
								.getHomeFolder().getAbsolutePath());
				boolean accepted = UIFactory.showDialog(panel);
				if (accepted) {
					File folder = panel.getSelectedFile();
					try {
						PrintWriter pw = new PrintWriter(currentWorkspace);
						pw.println(folder.getAbsolutePath());
						pw.close();
					} catch (FileNotFoundException e) {
						throw new RuntimeException("Cannot initialize system",
								e);
					}
				}
			}

			try {
				BufferedReader fileReader = new BufferedReader(new FileReader(
						currentWorkspace));
				String currentDir = fileReader.readLine();
				workspaceFolder = new File(currentDir);
				fileReader.close();
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Cannot find the workspace location");
			} catch (IOException e) {
				throw new RuntimeException("Cannot read the workspace location");
			}
		}
	}

	private boolean validWorkspace() {
		if (workspaceFolder == null) {
			return true;
		} else if (!workspaceFolder.exists()) {
			return true;
		} else {
			return workspaceFolder.isDirectory();
		}
	}

	private File getMetadataFolder() {
		return new File(workspaceFolder, ".metadata");
	}

	public void setWorkspaceFolder(String folder) {
		workspaceFolder = new File(folder);
	}
}
