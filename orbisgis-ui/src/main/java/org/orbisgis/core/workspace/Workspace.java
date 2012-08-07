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
package org.orbisgis.core.workspace;

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
	void init(boolean clean);

	/**
	 * Changes the workspace folder. Creates it if necessary
	 * 
	 * @param folder
	 * @throws IOException
	 */
	void setWorkspaceFolder(String folder) throws IOException;

	
	/**
	 * Get the current workspace path
	 * @return
	 */
	String getWorkspaceFolder();
	/**
	 * This invocation forces the workspace to be saved.
	 * 
	 * @throws IOException
	 */
	void saveWorkspace() throws IOException;

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

	/**
	 * Returns the folder inside the workspace where temporal results can be
	 * stored. At the higher frequency this folder will be removed each time the
	 * application exits
	 * 
	 * @return
	 */
	File getTempFolder();

}