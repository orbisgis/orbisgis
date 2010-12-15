/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer,
Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 * info@orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.geocatalog;

import java.io.File;
import javax.swing.filechooser.FileFilter;

import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.OrbisConfiguration;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.core.ui.wizards.OpenGdmsFolderPanel;
import org.orbisgis.utils.FileUtils;

public class NewGeocatalogFolderPlugIn extends AbstractPlugIn {

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		OpenGdmsFolderPanel folderPanel = new OpenGdmsFolderPanel(
			"Select the folder to add");
		if (UIFactory.showDialog(new UIPanel[]{folderPanel})) {

			File[] files = folderPanel.getSelectedFiles();
			for (File file : files) {
				processFolder(file, folderPanel.getSelectedFilter());
			}
		}
		return true;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getGeocatalog();
		context.getFeatureInstaller().addPopupMenuItem(
			frame,
			this,
			new String[]{Names.POPUP_GEOCATALOG_ADD,
				Names.POPUP_GEOCATALOG_FOLDER},
			Names.POPUP_GEOCATALOG_ADD, false,
			OrbisGISIcon.GEOCATALOG_FILE, wbContext);

	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * the method that actually process the content of a directory, or a file.
	 * If the file is acceptable by the FileFilter, it is processed
	 * @param file
	 */
	private void processFolder(File file, FileFilter filter) {
		if (file.isDirectory()) {
			for (File content : file.listFiles()) {
				processFolder(content, filter);
			}
		} else {
			if (filter.accept(file) && OrbisConfiguration.isFileEligible(file)) {
				DataManager dm = (DataManager) Services.getService(DataManager.class);
				SourceManager sourceManager = dm.getSourceManager();
				try {
					String name = sourceManager.getUniqueName(FileUtils.getFileNameWithoutExtensionU(file));
					sourceManager.register(name, file);
				} catch (SourceAlreadyExistsException e) {
					Services.getErrorManager().error(
						"The source is already registered: "
						+ e.getMessage());
				}
			}
		}
	}
}
