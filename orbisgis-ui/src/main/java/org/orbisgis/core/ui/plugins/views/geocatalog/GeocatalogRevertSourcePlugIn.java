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
package org.orbisgis.core.ui.plugins.views.geocatalog;

import org.gdms.driver.DriverException;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class GeocatalogRevertSourcePlugIn extends AbstractPlugIn {

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getGeocatalog();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_REVERT_PATH1 },
				Names.POPUP_TOC_INACTIVE_GROUP, false, OrbisGISIcon.REVERT,
				wbContext);
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		String[] res = getPlugInContext().getSelectedSources();
		Catalog catalog = context.getWorkbenchContext().getWorkbench()
				.getFrame().getGeocatalog();
		for (int i = 0; i < res.length; i++) {
			final String name = res[i];
			EditableSource s = catalog.getEditingSource(name);
			try {
				s.getDataSource().syncWithSource();
			} catch (DriverException e) {
				ErrorMessages.error(ErrorMessages.CannotRevertSource, e);
			}
		}

		return true;
	}

	@Override
	public boolean isEnabled() {
		String[] res = getPlugInContext().getSelectedSources();
		if (res.length != 1) {
			return false;
		}
		EditableSource s = getPlugInContext().getWorkbenchContext()
				.getWorkbench().getFrame().getGeocatalog().getEditingSource(
						res[0]);
		return s != null && s.getDataSource() != null && s.isModified();
	}
}
