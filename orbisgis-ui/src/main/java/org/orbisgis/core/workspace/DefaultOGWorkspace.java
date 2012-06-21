/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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

import org.orbisgis.core.Services;

public class DefaultOGWorkspace implements IOGWorkspace {
	private static final String RESULTS_FOLDER_NAME = "results";

	public DefaultOGWorkspace() {

		if (getWorkspace() == null) {
			throw new RuntimeException(Workspace.class.getName()
					+ " service not found");
		}

		File resultsDir = getWorkspace().getFile(RESULTS_FOLDER_NAME);
		if (!resultsDir.exists()) {
			resultsDir.mkdirs();
		}
	}

	private Workspace getWorkspace() {
		return (Workspace) Services.getService(Workspace.class);
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

	public void init(boolean clean) {
		getWorkspace().init(clean);
	}

	public void setWorkspaceFolder(String folder) throws IOException {
		getWorkspace().setWorkspaceFolder(folder);
	}

	public File getResultsFolder() {
		return getWorkspace().getFile(RESULTS_FOLDER_NAME);
	}

	public File getTempFolder() {
		return getWorkspace().getTempFolder();
	}

	public void addWorkspaceListener(WorkspaceListener listener) {
		getWorkspace().addWorkspaceListener(listener);
	}

	public void removeWorkspaceListener(WorkspaceListener listener) {
		getWorkspace().removeWorkspaceListener(listener);
	}

	public void saveWorkspace() throws IOException {
		getWorkspace().saveWorkspace();
	}

	public File getNewResultFile(String prefix, String suffix) {
		File ret = null;
		do {
			ret = new File(getResultsFolder(), prefix
					+ System.currentTimeMillis() + suffix);
		} while (ret.exists());

		return ret;
	}

	public String getWorkspaceFolder() {
		return getWorkspace().getWorkspaceFolder();
	}

}
