/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT
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
 * info _at_ orbisgis.org
 */

package org.orbisgis.core.ui.plugins.views.geocatalog;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public class GeocatalogClearPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		DataManager dm = Services.getService(DataManager.class);
		execute(dm.getSourceManager());
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getGeocatalog();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_GEOCATALOG_CLEAR },
				Names.POPUP_GEOCATALOG_CLEAR_GROUP, false, OrbisGISIcon.CLEAR,
				wbContext);
	}

	public void execute(SourceManager sourceManager) {
		int option = JOptionPane
				.showConfirmDialog(
						null,
						I18N
								.getText("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.clearAction"),
						I18N
								.getText("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.clear"),
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (option == JOptionPane.YES_OPTION) {
			try {
				sourceManager.removeAll();
				sourceManager.loadSystemTables();
			} catch (IOException e) {
				Services
						.getErrorManager()
						.error(
								I18N
										.getText("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.cannotClear"),
								e);
			}
		}
	}

	public boolean isEnabled() {
		DataManager dm = Services.getService(DataManager.class);
		SourceManager sourceManager = dm.getSourceManager();
		String[] names = sourceManager.getSourceNames();
		int sum = 0;
		for (int i = 0; i < names.length; i++) {
			if (!sourceManager.getSource(names[i]).isSystemTableSource()) {
				sum += i;
			}
		}

		return sum > 0;
	}
}
