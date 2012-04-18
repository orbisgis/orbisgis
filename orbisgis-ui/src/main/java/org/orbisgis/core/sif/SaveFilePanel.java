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
package org.orbisgis.core.sif;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

import org.orbisgis.utils.I18N;

public class SaveFilePanel extends OpenFilePanel {

	private boolean fileMustNotExist;

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
		if ((ret != null) && !ret.isAbsolute()) {
			ret = new File(fc.getCurrentDirectory(), ret.getName());
		}
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
			return I18N.getString("orbisgis.core.file.aFileMustSelected");
		} else if (fileMustNotExist) {
			if (getSelectedFile().exists()) {
				return I18N.getString("orbisgis.core.file.fileAlreadyExists");
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public File[] getSelectedFiles() {
		return new File[] { getSelectedFile() };
	}

	public void setFileMustNotExist(boolean fileMustNotExist) {
		this.fileMustNotExist = fileMustNotExist;
	}

	@Override
	public String postProcess() {
		if (getSelectedFile().exists()) {
			int ret = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(), I18N
					.getString("orbisgis.core.file.fileAlreadyExists")
					+ " " + I18N.getString("orbisgis.core.file.overwrite"), I18N
					.getString("orbisgis.core.file.existing"),
					JOptionPane.YES_NO_OPTION);
			if (ret == JOptionPane.NO_OPTION || ret == JOptionPane.CLOSED_OPTION) {
				// just do nothing...
                                // still, this isn't clean code...
                                return SimplePanel.CANCELED_ACTION;
                        }
		}
		return null;
	}
}
