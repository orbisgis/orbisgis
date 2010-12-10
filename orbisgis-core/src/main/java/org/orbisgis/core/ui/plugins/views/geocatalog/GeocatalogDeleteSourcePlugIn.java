/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Lead Erwan BOCHER, scientific researcher,
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer.
 *
 *  User support lead : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/

package org.orbisgis.core.ui.plugins.views.geocatalog;

import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class GeocatalogDeleteSourcePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		DataManager dm = Services.getService(DataManager.class);
		String[] res = getPlugInContext().getSelectedSources();
		for (String resource : res) {
			execute(dm.getSourceManager(), resource);
		}

		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getGeocatalog();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_GEOCATALOG_DELETE_SRC },
				Names.POPUP_GEOCATALOG_DELETE_SRC_GROUP, false,
				OrbisGISIcon.REMOVE, wbContext);

	}

	public void execute(SourceManager sourceManager, String currentNode) {
		try {
			sourceManager.remove(currentNode);
		} catch (IllegalStateException e) {
			Services.getErrorManager().error(
					"Cannot remove the resource: " + currentNode, e);
		}
	}

	public boolean isEnabled() {
		WorkbenchContext workbenchContext = getPlugInContext()
				.getWorkbenchContext();
		String[] res = workbenchContext.getWorkbench().getFrame()
				.getGeocatalog().getSelectedSources();
		boolean acceptsAllSources = false;
		if (res.length > 0) {
			DataManager dm = Services.getService(DataManager.class);
			SourceManager sourceManager = dm.getSourceManager();
			for (int i = 0; i < res.length; i++) {
				if (sourceManager.getSource(res[i]).isSystemTableSource()) {
					return false;
				}
			}
			acceptsAllSources = true;
		}

		return acceptsAllSources;
	}
}
