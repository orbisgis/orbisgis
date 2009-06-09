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
package org.orbisgis.core.ui.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.Services;
import org.orbisgis.core.workspace.DefaultWorkspace;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.utils.FileUtils;
import org.orbisgis.core.workspace.Workspace;

public class DefaultSwingWorkspace extends DefaultWorkspace implements
		Workspace {

	private static Logger logger = Logger
			.getLogger(DefaultSwingWorkspace.class);

	/**
	 * @see org.orbisgis.workspace.Workspace#init(boolean)
	 */
	public void init(boolean clean) throws IOException {
		logger.debug("Initializing workspace");
		File currentWorkspace = getCurrentWorkspaceFile();
		if (currentWorkspace.exists()) {
			readCurrentworkspace(currentWorkspace);
		}
		while (validateWorkspace() != null) {
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
			FileUtils.deleteDir(getMetadataFolder());
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

	private class ShowDialog implements Runnable {

		private File ret;

		@Override
		public void run() {
			WorkspaceFolderFilePanel panel = new WorkspaceFolderFilePanel(
					"Select the workspace folder", Services.getService(
							ApplicationInfo.class).getHomeFolder()
							.getAbsolutePath());
			if (UIFactory.showDialog(panel)) {
				ret = panel.getSelectedFile();
			} else {
				ret = null;
			}
		}

	}

}
