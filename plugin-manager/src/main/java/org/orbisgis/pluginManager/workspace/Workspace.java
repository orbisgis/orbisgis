package org.orbisgis.pluginManager.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;

public class Workspace {

	private static Logger logger = Logger.getLogger(Workspace.class);

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

	public File createNewFile() {
		return createNewFile("orbisgis", "");
	}

	/**
	 * @param name
	 *            relative path inside the workspace base path
	 * @return
	 */
	public File getFile(String name) {
		return new File(getMetadataFolder(), name);
	}

	public void init(boolean clean) throws IOException {
		logger.debug("Initializing workspace");
		while (!validWorkspace()) {
			File currentWorkspace = getCurrentWorkspaceFile();
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
		logger.debug("Using workspace " + workspaceFolder.getAbsolutePath());
		if (clean) {
			deleteFile(getMetadataFolder());
		}
		if (!getMetadataFolder().exists()) {
			if (!getMetadataFolder().mkdirs()) {
				throw new IOException("Cannot create metadata directory");
			}
		}
	}

	private File getCurrentWorkspaceFile() {
		return new File(PluginManager.getHomeFolder(), "currentWorkspace.txt");
	}

	/**
	 * This function will recursivly delete directories and files.
	 *
	 * @param path
	 *            File or Directory to be deleted
	 * @return true indicates success.
	 */
	private static boolean deleteFile(File path) {
		if (path.exists()) {
			if (path.isDirectory()) {
				File[] files = path.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteFile(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
		}
		return (path.delete());
	}

	private boolean validWorkspace() {
		if (workspaceFolder == null) {
			return false;
		} else if (!workspaceFolder.exists()) {
			return false;
		} else {
			return workspaceFolder.isDirectory();
		}
	}

	private File getMetadataFolder() {
		return new File(workspaceFolder, ".metadata");
	}

	public void setWorkspaceFolder(String folder) throws IOException {
		workspaceFolder = new File(folder);
		if (!getMetadataFolder().exists()) {
			if (!getMetadataFolder().mkdirs()) {
				throw new IOException("Cannot create metadata directory");
			}
		}

		File file = getCurrentWorkspaceFile();
		PrintWriter pw = new PrintWriter(file);
		pw.println(folder);
		pw.close();

	}

}
