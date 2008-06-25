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
package org.orbisgis.pluginManager.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.orbisgis.Services;
import org.orbisgis.pluginManager.ApplicationInfo;
import org.orbisgis.pluginManager.ui.OpenFilePanel;

public class WorkspaceFolderFilePanel extends OpenFilePanel {

	public static final String SIF_ID = "org.orbisgis.pluginManager.WorkspaceFileChooser";

	public WorkspaceFolderFilePanel(String title, String dir) {
		super(SIF_ID, title);
		JFileChooser ret = super.getFileChooser();
		ret.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		ret.setMultiSelectionEnabled(false);
		ret.setSelectedFile(new File(dir));
	}

	public String validateInput() {
		File file = getSelectedFile();
		if (file == null) {
			return "A file must be selected";
		} else if (!file.exists()) {
			return "The directory must exist";
		} else if (!file.isDirectory()) {
			return "The selection must be a directory";
		}

		return null;
	}

	@Override
	public String postProcess() {
		File versionFile = new File(getSelectedFile(),
				".metadata/org.orbisgis.version.txt");
		ApplicationInfo ai = (ApplicationInfo) Services
				.getService("org.orbisgis.ApplicationInfo");
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
			version = 1;
		}
		if (ai.getWsVersion() != version) {
			return "Workspace version mistmatch. Either"
					+ " clean or select another folder.";
		} else {
			return null;
		}
	}
}
