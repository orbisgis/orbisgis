/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.pluginManager.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

public class SaveFilePanel extends OpenFilePanel {

	public SaveFilePanel(String id, String title) {
		super(id, title);
		getFileChooser().setDialogType(JFileChooser.SAVE_DIALOG);
	}

	@Override
	public File getSelectedFile() {
		File ret;
		JFileChooser fc = getFileChooser();
		FileChooserUI ui = fc.getUI();
		if (ui instanceof BasicFileChooserUI) {
			BasicFileChooserUI basicUI = (BasicFileChooserUI) ui;
			String fileName = basicUI.getFileName();
			if ((fileName == null) || (fileName.length() == 0)) {
				ret = null;
			} else {
				ret = autoComplete(new File(fileName));
			}
		} else {
			ret = autoComplete(super.getSelectedFile());
		}
		System.out.println(ret);
		return ret;
	}

	private File autoComplete(File selectedFile) {
		FileFilter ff = getFileChooser().getFileFilter();
		if (ff instanceof FormatFilter) {
			FormatFilter filter = (FormatFilter) ff;
			return filter.autoComplete(selectedFile);
		} else {
			return selectedFile;
		}
	}

	public String validateInput() {
		File file = getSelectedFile();
		if (file == null) {
			return "A file must be specified";
		} else {
			return null;
		}
	}

	@Override
	public File[] getSelectedFiles() {
		return new File[] { getSelectedFile() };
	}
}
