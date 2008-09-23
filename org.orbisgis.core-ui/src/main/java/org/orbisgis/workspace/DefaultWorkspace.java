/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.orbisgis.Services;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;

public class DefaultWorkspace implements Workspace {

	private static final String VERSION_FILE_NAME = "org.orbisgis.version.txt";

	private static Logger logger = Logger.getLogger(DefaultWorkspace.class);

	private File workspaceFolder;

	private ArrayList<WorkspaceListener> listeners = new ArrayList<WorkspaceListener>();

	/**
	 * @see org.orbisgis.workspace.Workspace#getNewFile(java.lang.String,
	 *      java.lang.String)
	 */
	public File getNewFile(String prefix, String suffix) {
		File ret = null;
		do {
			ret = new File(getMetadataFolder(), prefix
					+ System.currentTimeMillis() + suffix);
		} while (ret.exists());

		return ret;
	}

	/**
	 * @see org.orbisgis.workspace.Workspace#getNewFile()
	 */
	public File getNewFile() {
		return getNewFile("orbisgis", "");
	}

	/**
	 * @see org.orbisgis.workspace.Workspace#getFile(java.lang.String)
	 */
	public File getFile(String name) {
		File ret = new File(getMetadataFolder(), name);
		if (!ret.getParentFile().exists()) {
			ret.getParentFile().mkdirs();
		}

		return ret;
	}

	/**
	 * @see org.orbisgis.workspace.Workspace#init(boolean)
	 */
	public void init(boolean clean) throws IOException {
		logger.debug("Initializing workspace");
		File currentWorkspace = getCurrentWorkspaceFile();
		if (currentWorkspace.exists()) {
			readCurrentworkspace(currentWorkspace);
		}
		while (!validWorkspace()) {
			File folder = askWorkspace();
			if (folder != null) {
				try {
					PrintWriter pw = new PrintWriter(currentWorkspace);
					pw.println(folder.getAbsolutePath());
					pw.close();
				} catch (FileNotFoundException e) {
					throw new RuntimeException("Cannot initialize system", e);
				}
			} else {
				throw new RuntimeException("Cannot initialize system");
			}

			if (currentWorkspace.exists()) {
				readCurrentworkspace(currentWorkspace);
			}
		}
		setWorkspaceFolder(workspaceFolder.getAbsolutePath());

		logger.debug("Using workspace " + workspaceFolder.getAbsolutePath());
		if (clean) {
			deleteFile(getMetadataFolder());
		}
		createMetadataDir();
	}

	private void readCurrentworkspace(File currentWorkspace) {
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(
					currentWorkspace));
			String currentDir = fileReader.readLine();
			workspaceFolder = new File(currentDir);
			fileReader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot find the workspace location", e);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read the workspace location", e);
		}
	}

	protected File askWorkspace() {
		ShowDialog dialog = new ShowDialog();
		try {
			SwingUtilities.invokeAndWait(dialog);
			return dialog.ret;
		} catch (InterruptedException e) {
			throw new RuntimeException("Cannot obtain the workspace folder", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Cannot obtain the workspace folder", e);
		}
	}

	private File getCurrentWorkspaceFile() throws FileNotFoundException {
		PluginManager psm = (PluginManager) Services
				.getService(PluginManager.class);
		psm.getHomeFolder().mkdirs();
		File file = new File(psm.getHomeFolder(), "currentWorkspace.txt");
		return file;
	}

	/**
	 * This function will recursively delete directories and files.
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
		} else if (!workspaceFolder.isDirectory()) {
			return false;
		} else {
			File versionFile = getVersionFile();
			if (versionFile.exists()) {
				try {
					BufferedReader fr = new BufferedReader(new FileReader(
							versionFile));
					String strVersion = fr.readLine();
					fr.close();
					int version = Integer.parseInt(strVersion.trim());
					return getWsVersion() == version;
				} catch (IOException e1) {
					return false;
				} catch (NumberFormatException e) {
					return false;
				}
			} else {
				return getWsVersion() == 1;
			}
		}
	}

	public int getWsVersion() {
		return 2;
	}

	private File getVersionFile() {
		return new File(getMetadataFolder(), VERSION_FILE_NAME);
	}

	private File getMetadataFolder() {
		return new File(workspaceFolder, ".metadata");
	}

	/**
	 * @see org.orbisgis.workspace.Workspace#setWorkspaceFolder(java.lang.String)
	 */
	public void setWorkspaceFolder(String folder) throws IOException {
		saveWorkspace();

		File oldWorkspace = workspaceFolder;
		workspaceFolder = new File(folder);
		createMetadataDir();

		File file = getCurrentWorkspaceFile();
		PrintWriter pw = new PrintWriter(file);
		pw.println(folder);
		pw.close();

		for (WorkspaceListener listener : listeners) {
			try {
				listener.workspaceChanged(oldWorkspace, workspaceFolder);
			} catch (Exception e) {
				Services.getErrorManager().error(
						"Error while changing workspace", e);
			}
		}
	}

	private void createMetadataDir() throws IOException {
		if (!getMetadataFolder().exists()) {
			if (!getMetadataFolder().mkdirs()) {
				throw new IOException("Cannot create metadata directory");
			} else {
				writeVersionFile();
			}
		}
	}

	public void addWorkspaceListener(WorkspaceListener listener) {
		listeners.add(listener);
	}

	public void removeWorkspaceListener(WorkspaceListener listener) {
		listeners.remove(listener);
	}

	public void saveWorkspace() throws IOException {
		if (workspaceFolder == null) {
			return;
		} else {
			writeVersionFile();
			for (WorkspaceListener listener : listeners) {
				try {
					listener.saveWorkspace();
				} catch (Exception e) {
					Services.getErrorManager().error(
							"Error while saving workspace", e);
				}
			}
		}
	}

	private void writeVersionFile() throws FileNotFoundException {
		File versionFile = getVersionFile();
		versionFile.getParentFile().mkdirs();
		PrintWriter pw = new PrintWriter(versionFile);
		pw.println(getWsVersion());
		pw.flush();
		pw.close();
	}

	private class ShowDialog implements Runnable {

		private File ret;

		@Override
		public void run() {
			PluginManager psm = (PluginManager) Services
					.getService(PluginManager.class);
			WorkspaceFolderFilePanel panel = new WorkspaceFolderFilePanel(
					"Select the workspace folder", psm.getHomeFolder()
							.getAbsolutePath());
			if (UIFactory.showDialog(panel)) {
				ret = panel.getSelectedFile();
			} else {
				ret = null;
			}
		}

	}

}
