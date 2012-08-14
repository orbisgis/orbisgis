/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.plugins.toc;

import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public class GroupLayersPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {

		MapContext mapContext = getPlugInContext().getMapContext();
		ILayer[] layers = mapContext.getSelectedLayers();

		DataManager dataManager = (DataManager) Services
				.getService(DataManager.class);
		ILayer col = dataManager.createLayerCollection(I18N.getString("orbisgis.org.orbisgis.ui.groupLayersPlugIn.group") //$NON-NLS-1$
				+ System.currentTimeMillis());
		ILayer parent = layers[0].getParent();
		try {
			parent.addLayer(col);
			for (ILayer layer : layers) {
				layer.moveTo(col);
			}
		} catch (LayerException e) {
			throw new RuntimeException(I18N.getString("orbisgis.org.orbisgis.ui.groupLayersPlugIn.bug")); //$NON-NLS-1$
		}

		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_LAYERS_GROUP_PATH1 },
				Names.POPUP_TOC_LAYERS_GROUP_GROUP, false,
				OrbisGISIcon.GROUP_LAYERS, wbContext);
	}

	public boolean isEnabled() {
		MapContext mapContext = getPlugInContext().getMapContext();
		ILayer[] layers = null;
		if (mapContext != null)
			layers = mapContext.getSelectedLayers();
		if(layers==null) return false;
		for (int i = 0; i < layers.length - 1; i++) {
			if (!layers[i].getParent().equals(layers[i + 1].getParent())) {
				return false;
			}
		}
		return getPlugInContext().checkLayerAvailability(
				new SelectionAvailability[]{ SelectionAvailability.SUPERIOR , SelectionAvailability.ACTIVE_MAPCONTEXT},
				1,
				new LayerAvailability[]{});
	}
}
