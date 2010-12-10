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
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.plugins.toc.raster.style;

import java.awt.image.ColorModel;
import java.io.IOException;

import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class RasterDefaultStylePlugIn extends AbstractPlugIn {

	@Override
	public boolean execute(PlugInContext context) throws Exception {

		try {
			MapContext mapContext = getPlugInContext().getMapContext();
			ILayer[] selectedResources = mapContext.getSelectedLayers();
			ILayer layer = selectedResources[0];
			RasterLegend legend = (RasterLegend) layer.getRasterLegend()[0];
			final RasterDefaultStyleUIPanel rasterDefaultStyleUIClass = new RasterDefaultStyleUIPanel(
					legend, layer.getRaster().getDefaultColorModel());

			if (UIFactory.showDialog(rasterDefaultStyleUIClass)) {
				ColorModel colorModel = rasterDefaultStyleUIClass
						.getColorModel();
				float opacity = rasterDefaultStyleUIClass.getOpacity();
				if (colorModel == null) {
					colorModel = legend.getColorModel();
				}
				RasterLegend newLegend = new RasterLegend(colorModel, opacity);
				layer.setLegend(newLegend);
			}

		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot get the legend", e);
		} catch (IOException e) {
			Services.getErrorManager().error("Cannot get the default style", e);

			Services.getErrorManager().error("Cannot get the default style", e);
		}

		return false;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_LEGEND_RASTER },
				Names.POPUP_TOC_LEGEND_GROUP, false, OrbisGISIcon.EDIT_LEGEND,
				wbContext);

	}

	@Override
	public boolean isEnabled() {
		return getPlugInContext().checkLayerAvailability(
				new SelectionAvailability[] { SelectionAvailability.EQUAL }, 1,
				new LayerAvailability[] { LayerAvailability.RASTER });
	}
}