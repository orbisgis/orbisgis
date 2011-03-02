/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher. * 
 * 
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO,Adelin PIAU
 * 
 * Copyright (C) 2011 Erwan BOCHER, Alexis GUEGANNO, Antoine GOURLAY
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
 * info_at_orbisgis.org
 */

package org.orbisgis.core.ui.plugins.views.geocatalog;

import java.io.File;

import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.OrbisConfiguration;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.core.ui.wizards.OpenGdmsFilePanel;
import org.orbisgis.utils.FileUtils;
import org.orbisgis.utils.I18N;

/**
 * This plugin is used when the user wants to open a file using the geocatalog.
 * It will open a panel dedicated to the selection of the wanted files. This
 * panel will then return the selected files to this PlugIn
 */

public class NewGeocatalogFilePlugIn extends AbstractPlugIn {

	/**
	 * During execution, the plugin wil create a OpenGdmsFilePanel to let the
	 * user select the files he wants. The only files imported in the geocatalog
	 * are those for which we have a driver register in the DataManager.
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean execute(PlugInContext context) throws Exception {
		OpenGdmsFilePanel filePanel = new OpenGdmsFilePanel(I18N
				.getString("orbisgis.org.core.selectFileAdd"));
		if (UIFactory.showDialog(new UIPanel[] { filePanel })) {
			// We can retrieve the files that have been selected by the user
			File[] files = filePanel.getSelectedFiles();
			for (File file : files) {
				// For each file, we ensure that we have a driver
				// that can be used to read it. If we don't, we don't
				// open the file.
				if (OrbisConfiguration.isFileEligible(file)) {
					DataManager dm = (DataManager) Services
							.getService(DataManager.class);
					SourceManager sourceManager = dm.getSourceManager();
					try {
						String name = sourceManager.getUniqueName(FileUtils
								.getFileNameWithoutExtensionU(file));
						sourceManager.register(name, file);
					} catch (SourceAlreadyExistsException e) {
						ErrorMessages
								.error(ErrorMessages.SourceAlreadyRegistered
										+ ": ", e);
					}
				}
			}
		}
		return true;
	}

	/**
	 * The method used for plugin initialization.
	 * 
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getGeocatalog();
		context.getFeatureInstaller().addPopupMenuItem(
				frame,
				this,
				new String[] { Names.POPUP_GEOCATALOG_ADD,
						Names.POPUP_GEOCATALOG_FILE },
				Names.POPUP_GEOCATALOG_ADD, false,
				OrbisGISIcon.GEOCATALOG_FILE, wbContext);

	}

	/**
	 * As this plugin is used directly by the geocatalog, it is naturally
	 * enabled.
	 * 
	 * @return
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
}
