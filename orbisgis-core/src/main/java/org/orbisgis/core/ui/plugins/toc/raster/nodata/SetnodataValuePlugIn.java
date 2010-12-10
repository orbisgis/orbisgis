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
package org.orbisgis.core.ui.plugins.toc.raster.nodata;

import ij.ImagePlus;

import java.io.IOException;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.multiInputPanel.DoubleType;
import org.orbisgis.core.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;

public class SetnodataValuePlugIn extends AbstractPlugIn {

	public boolean accepts(MapContext mc, ILayer layer) {
		try {
			if (layer.isRaster()) {
				SpatialDataSourceDecorator ds = layer.getDataSource();
				if (ds.getRaster(0).getType() != ImagePlus.COLOR_RGB) {
					return true;
				}
			}
		} catch (IOException e) {
		} catch (DriverException e) {
		}
		return false;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		MapContext mapContext = getPlugInContext().getMapContext();
		ILayer[] selectedResources = mapContext.getSelectedLayers();
		ILayer layer = selectedResources[0];
		try {
			GeoRaster geoRasterSrc = layer.getRaster();

			final MultiInputPanel mip = new MultiInputPanel("Set nodatavalue");

			mip.addInput("minvalue", "Min value", new Float(geoRasterSrc
					.getMin()).toString(), new DoubleType(10, false));
			mip.addInput("maxvalue", "Max value", new Float(geoRasterSrc
					.getMax()).toString(), new DoubleType(10, false));

			double noDataValue = geoRasterSrc.getNoDataValue();
			if (Double.isNaN(noDataValue)) {
				mip.addInput("nodatavalue", "Nodata value", null,
						new NullableDoubleType(10));
			} else {
				mip.addInput("nodatavalue", "Nodata value", new Float(
						noDataValue).toString(), new NullableDoubleType(10));
			}

			mip.group("Range values", new String[] { "minvalue", "maxvalue" });
			mip.group("Change nodata", new String[] { "nodatavalue" });

			if (UIFactory.showDialog(mip)) {

				String ndv = mip.getInput("nodatavalue");
				if (ndv == null) {
					geoRasterSrc.setNodataValue(Float.NaN);
				} else {
					final float nodata = new Float(ndv);
					geoRasterSrc.setNodataValue((float) nodata);
				}
			}
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot read the raster from the layer ", e);
		} catch (IOException e) {
			Services.getErrorManager().error(
					"Cannot compute " + layer.getName(), e);
		}
		return false;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { "Change nodata" }, Names.POPUP_TOC_LEGEND_GROUP,
				false, IconLoader.getIcon("contrast.png"), wbContext);

	}

	@Override
	public boolean isEnabled() {
		return getPlugInContext().checkLayerAvailability(
				new SelectionAvailability[] { SelectionAvailability.EQUAL }, 1,
				new LayerAvailability[] { LayerAvailability.RASTER });
	}
}