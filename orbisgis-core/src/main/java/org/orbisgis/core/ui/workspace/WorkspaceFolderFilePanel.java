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
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.orbisgis.core.Services;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.workspace.DefaultWorkspace;
import org.orbisgis.core.workspace.Workspace;

public class WorkspaceFolderFilePanel extends SaveFilePanel {

	public static final String SIF_ID = "org.orbisgis.coreceFileChooser";

	public WorkspaceFolderFilePanel(String title, String dir) {
		super(SIF_ID, title);
		JFileChooser ret = super.getFileChooser();
		ret.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES  );
		ret.setMultiSelectionEnabled(false);
		ret.setSelectedFile(new File(dir));
	}

	public String validateInput() {
		File file = getSelectedFile();
		if (file == null) {
			return "A file must be selected";
		} else if (file.exists() && !file.isDirectory()) {
			return "The selection must be a directory";
		}

		return null;
	}

	@Override
	public String postProcess() {
		File file = getSelectedFile();
		if (!new File(file, ".metadata").exists()) {
			int ret = JOptionPane.showConfirmDialog(null, "Workspace '"
					+ file.getAbsolutePath() + "' will be created. Proceed?",
					"The workspace doesn't exist", JOptionPane.YES_NO_OPTION);
			if (ret == JOptionPane.NO_OPTION) {
				return "The workspace won't be created";
			} else {
				Workspace ws = Services.getService(Workspace.class);
				try {
					ws.setWorkspaceFolder(file.getAbsolutePath());
				} catch (IOException e) {
					return "Cannot create workspaces, Check permissions";
				}
			}
		} else {
			File versionFile = new File(file,
					".metadata/org.orbisgis.version.txt");
			int version;
			if (versionFile.exists()) {
				try {
					BufferedReader fr = new BufferedReader(new FileReader(
							versionFile));
					String strVersion = fr.readLine();
					fr.close();
					version = Integer.parseInt(strVersion.trim());
				} catch (IOException e1) {
					return "Cannot read workspace version";
				} catch (NumberFormatException e) {
					return "Cannot read workspace version";
				}
			} else {
				version = DefaultWorkspace.WORKSPACE_VERSION;
			}
			DefaultSwingWorkspace dw = (DefaultSwingWorkspace) Services
					.getService(Workspace.class);
			if (dw.getWsVersion() != version) {
				return "Workspace version mistmatch. Either"
						+ " clean or select another folder.";
			}
		}
		return null;
	}

}
