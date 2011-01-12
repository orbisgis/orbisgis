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

import javax.swing.JOptionPane;

import org.gdms.data.NonEditableDataSourceException;
import org.gdms.driver.DriverException;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public class GeocatalogSaveChangesPlugIn extends AbstractPlugIn {

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getGeocatalog();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_SAVE_PATH1 },
				Names.POPUP_TOC_INACTIVE_GROUP, false, OrbisGISIcon.SAVE,
				wbContext);
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		String[] res = getPlugInContext().getSelectedSources();
		final Catalog geocatalog = context.getWorkbenchContext().getWorkbench()
				.getFrame().getGeocatalog();
		for (int i = 0; i < res.length; i++) {
			final String name = res[i];
			final EditableSource s = geocatalog.getEditingSource(name);
			if (s.isModified()) {
				try {
					s.getDataSource().commit();
				} catch (DriverException e) {
					ErrorMessages.error(ErrorMessages.CannotSaveSource, e);
				} catch (NonEditableDataSourceException e) {
					ErrorMessages.error(ErrorMessages.CannotSaveSource, e);
				}
			}
			JOptionPane
					.showMessageDialog(
							geocatalog,
							I18N
									.getText("orbisgis.org.orbisgis.core.geocatalog.sourceSaved"));

		}
		// DO NOT REMOVE
		// this call is needed to work around a strange Swing painting problem
		// when using for the first time our custom SourceListRender
		// to display a change in the font of a listed source
		geocatalog.repaint();

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
