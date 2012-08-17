/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.workspace.DefaultWorkspace;
import org.orbisgis.core.workspace.Workspace;

public class DefaultSwingWorkspace extends DefaultWorkspace implements
		Workspace {

	private static Logger logger = Logger
			.getLogger(DefaultSwingWorkspace.class);

	/**
	 * @see org.orbisgis.core.workspace.Workspace#init(boolean)
	 */
	public void init(boolean clean) {
		try {
			logger.debug("Initializing workspace");
			File defaultWorkspace = getDefaultWorkspaceFile();
			if (defaultWorkspace.exists()) {
				if (clean) {
					FileUtils.deleteDirectory(new File(getWorkspaceFolder()));
				} else {
					readCurrentworkspace(defaultWorkspace);
					setWorkspaceFolder(workspaceFolder.getAbsolutePath());
					setDefaultWorkspace(true);
					logger.debug("Using workspace "
							+ workspaceFolder.getAbsolutePath());
				}
			} else {
				createWorkspaceFolderPanelChooser();
			}

		} catch (IOException ioe) {
			Services.getErrorManager().error("Error while init workspace", ioe);
		}
	}

	/**
	 * Open a window to select a workspace
	 * @throws IOException
	 */
	public void createWorkspaceFolderPanelChooser() throws IOException {
		WorkspaceFolderPanel workspaceFolderPanel = new WorkspaceFolderPanel(
				loadWorkspaces());
		boolean ok = UIFactory.showDialog(workspaceFolderPanel);
		if (ok) {
			String currentWorkspace = workspaceFolderPanel.getWorkspacePath();
			workspaceFolder = new File(currentWorkspace);
			setWorkspaceFolder(workspaceFolder.getAbsolutePath());
			saveWorkspaces(workspaceFolderPanel.getWorkspacesList());
			// Write the a current workspace folder
			if (workspaceFolderPanel.isSelected()) {
				writeDefaultWorkspaceFile(currentWorkspace);
				setDefaultWorkspace(true);
			} else {
				freeDefaultWorkspace();
				setDefaultWorkspace(false);
			}
			logger
					.debug("Using workspace "
							+ workspaceFolder.getAbsolutePath());
		} else {
			Services.getErrorManager().error("No workspace was chosen. Exiting OrbisGIS.");
                        System.exit(0);
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

}
