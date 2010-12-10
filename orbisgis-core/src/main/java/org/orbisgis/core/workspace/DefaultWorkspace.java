/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.configuration.PeriodicSaveWorkspace;
import org.orbisgis.utils.FileUtils;

public class DefaultWorkspace implements Workspace {

	private static final String TEMP_FOLDER_NAME = "temp";

	private static final String VERSION_FILE_NAME = "org.orbisgis.version.txt";

	private static Logger logger = Logger.getLogger(DefaultWorkspace.class);

	protected File workspaceFolder;

	private ArrayList<WorkspaceListener> listeners = new ArrayList<WorkspaceListener>();

	public static int WORKSPACE_VERSION = 3;

	/* Workspace saving timer */
	private PeriodicSaveWorkspace timer;

	private boolean defaultWorkspace;

	public DefaultWorkspace() {

		timer = new PeriodicSaveWorkspace(this);
	}

	@Override
	public File getTempFolder() {
		return getFile(TEMP_FOLDER_NAME);
	}

	/**
	 * @see org.orbisgis.core.workspace.Workspace#getNewFile(java.lang.String,
	 *      java.lang.String)
	 */
	public File getNewFile(String prefix, String suffix) {
		File ret = null;
		do {
			ret = new File(getWorkspaceFolder(), prefix
					+ System.currentTimeMillis() + suffix);
		} while (ret.exists());

		return ret;
	}

	/**
	 * @see org.orbisgis.core.workspace.Workspace#getNewFile()
	 */
	public File getNewFile() {
		return getNewFile("orbisgis", "");
	}

	/**
	 * @see org.orbisgis.core.workspace.Workspace#getFile(java.lang.String)
	 */
	public File getFile(String name) {
		File ret = new File(getWorkspaceFolder(), name);
		if (!ret.getParentFile().exists()) {
			ret.getParentFile().mkdirs();
		}

		return ret;
	}

	/**
	 * @see org.orbisgis.core.workspace.Workspace#init(boolean)
	 */
	public void init(boolean clean) {
		try {
			logger.debug("Initializing workspace");
			File currentWorkspaceFile = getDefaultWorkspaceFile();
			readCurrentworkspace(currentWorkspaceFile);
			String error = validateWorkspace();
			if (error != null) {
				throw new IllegalStateException("Invalid workspace: " + error);
			}

			setWorkspaceFolder(workspaceFolder.getAbsolutePath());
			logger
					.debug("Using workspace "
							+ workspaceFolder.getAbsolutePath());
			if (clean) {
				FileUtils.deleteDir(new File(getWorkspaceFolder()));
			}
		} catch (IOException ioe) {
			Services.getErrorManager().error("Error while init workspace", ioe);
		}
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

	/**
	 * 
	 * @return the default current OrbisGIS workspace. If there is no default
	 *         workspace return null.
	 * @throws FileNotFoundException
	 */
	protected File getDefaultWorkspaceFile() throws FileNotFoundException {
		ApplicationInfo ogInfo = Services.getService(ApplicationInfo.class);
		ogInfo.getHomeFolder().mkdirs();
		File file = new File(ogInfo.getHomeFolder(), "currentWorkspace.txt");
		return file;
	}

	/**
	 * 
	 * @return all OrbisGIS workspaces
	 * @throws FileNotFoundException
	 */
	protected File getDefaultWorkspacesFile() throws FileNotFoundException {
		ApplicationInfo ogInfo = Services.getService(ApplicationInfo.class);
		File file = new File(ogInfo.getHomeFolder(), "workspaces.txt");
		return file;
	}

	/**
	 * 
	 * @return a list of all OrbisGIS workspaces stored in the workspaces.txt
	 *         file in the user home folder.
	 * @throws FileNotFoundException
	 */
	public ArrayList<String> loadWorkspaces() throws FileNotFoundException {
		ApplicationInfo ogInfo = Services.getService(ApplicationInfo.class);
		ArrayList<String> workspacesList = new ArrayList<String>();

		if (getDefaultWorkspacesFile().exists()) {
			try {
				BufferedReader fileReader = new BufferedReader(new FileReader(
						getDefaultWorkspacesFile()));

				String str;
				while ((str = fileReader.readLine()) != null) {
					workspacesList.add(str);
				}
				fileReader.close();

			} catch (IOException e) {
				Services.getErrorManager().error("bug!", e);
			}
		} else {
			workspacesList.add(ogInfo.getUserHomeFolder().getAbsolutePath()
					+ File.separator + "OrbisGIS");
		}

		return workspacesList;

	}

	public void saveWorkspaces(ArrayList<String> workspacesList) {
		try {
			PrintWriter pw = new PrintWriter(getDefaultWorkspacesFile());

			for (String workspace : workspacesList) {
				pw.println(workspace);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot initialize system", e);
		}
	}

	protected String validateWorkspace() {
		if (workspaceFolder == null) {
			return "Workspace is null";
		} else if (!workspaceFolder.exists()) {
			return "Workspace folder does not exist: " + workspaceFolder;
		} else if (!workspaceFolder.isDirectory()) {
			return "Workspace must be a folder: " + workspaceFolder;
		} else {
			File versionFile = getVersionFile();
			if (versionFile.exists()) {
				try {
					BufferedReader fr = new BufferedReader(new FileReader(
							versionFile));
					String strVersion = fr.readLine();
					fr.close();
					int version = Integer.parseInt(strVersion.trim());
					if (getWsVersion() != version) {
						return "Workspace version mistmatch. Expected "
								+ getWsVersion() + " and found " + version;
					}
				} catch (IOException e1) {
					return e1.getMessage();
				} catch (NumberFormatException e) {
					return e.getMessage();
				}
			} else {
				if (getWsVersion() != 1) {
					return "Workspace version mistmatch. Expected "
							+ getWsVersion()
							+ " and unversioned workspace found, default used: 1";
				}
			}
		}

		return null;
	}

	public int getWsVersion() {
		return WORKSPACE_VERSION;
	}

	private File getVersionFile() {
		return new File(getWorkspaceFolder(), VERSION_FILE_NAME);
	}

	/**
	 * @see org.orbisgis.core.workspace.Workspace#setWorkspaceFolder(java.lang.String)
	 */
	public void setWorkspaceFolder(String folder) throws IOException {
		saveWorkspace();

		File oldWorkspace = workspaceFolder;
		workspaceFolder = new File(folder);
		createWorkspaceFolder();

		for (WorkspaceListener listener : listeners) {
			try {
				listener.workspaceChanged(oldWorkspace, workspaceFolder);
			} catch (Exception e) {
				Services.getErrorManager().error(
						"Error while changing workspace", e);
			}
		}
	}

	protected void createWorkspaceFolder() throws IOException {
		File workspaceFolderFile = new File(getWorkspaceFolder());
		if (!workspaceFolderFile.exists()) {
			if (!workspaceFolderFile.mkdirs()) {
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

	public void writeDefaultWorkspaceFile(String workspaceFile)
			throws IOException {

		if (!getDefaultWorkspaceFile().exists()) {
			try {
				PrintWriter pw = new PrintWriter(getDefaultWorkspaceFile());
				pw.println(workspaceFile);
				pw.close();
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Cannot initialize system", e);
			}
		}
	}

	public void freeDefaultWorkspace() throws IOException {
		if (getDefaultWorkspaceFile().exists()) {
			getDefaultWorkspaceFile().delete();
		}
	}

	public PeriodicSaveWorkspace getTimer() {
		return timer;
	}

	public void setTimer(PeriodicSaveWorkspace timer) {
		this.timer = timer;
	}

	public String getWorkspaceFolder() {
		if (workspaceFolder != null) {
			return workspaceFolder.getAbsolutePath();
		}
		return null;
	}

	public void setDefaultWorkspace(boolean defaultWorkspace) {
		this.defaultWorkspace = defaultWorkspace;
	}

	public boolean isDefaultWorkspace() {
		return defaultWorkspace;
	}
}
