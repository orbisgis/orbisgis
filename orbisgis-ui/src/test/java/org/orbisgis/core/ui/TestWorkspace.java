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
package org.orbisgis.core.ui;

import java.io.File;
import java.io.IOException;
import org.junit.Ignore;

import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.core.workspace.WorkspaceListener;

@Ignore
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
