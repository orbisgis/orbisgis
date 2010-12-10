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
package org.orbisgis.core.ui.plugins.toc.raster.threshold;

import ij.ImagePlus;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.multiInputPanel.DoubleType;
import org.orbisgis.core.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;

public class RasterThresholdPlugIn extends AbstractPlugIn {

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		MapContext mapContext = getPlugInContext().getMapContext();
		ILayer[] selectedResources = mapContext.getSelectedLayers();
		ILayer layer = selectedResources[0];
		GeoRaster geoRasterSrc = layer.getRaster();
		final MultiInputPanel mip = new MultiInputPanel(
				"Min - Max pixel extraction");
		mip.addInput("MinValue", "Min value", new Double(geoRasterSrc.getMin())
				.toString(), new DoubleType(12));
		mip.addInput("MaxValue", "Max value", new Float(geoRasterSrc.getMax())
				.toString(), new DoubleType(12));

		if (UIFactory.showDialog(mip)) {
			final double min = new Double(mip.getInput("MinValue"));
			final double max = new Double(mip.getInput("MaxValue"));
			final GeoRaster geoRasterResult = GeoRasterFactory.createGeoRaster(
					geoRasterSrc.getImagePlus(), geoRasterSrc.getMetadata());
			geoRasterResult.setRangeValues(min, max);
			geoRasterResult.setNodataValue((float) geoRasterSrc.getMetadata()
					.getNoDataValue());

			if (null != geoRasterResult) {
				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = ((DataManager) Services
						.getService(DataManager.class)).getDataSourceFactory();
				final String tempFile = dsf.getTempFile() + ".tif";
				geoRasterResult.save(tempFile);

				// populate the GeoView TOC with a new RasterLayer
				DataManager dataManager = (DataManager) Services
						.getService(DataManager.class);
				final ILayer newLayer = dataManager.createLayer(new File(
						tempFile));
				mapContext.getLayerModel().insertLayer(newLayer, 0);
			}
		}
		return false;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_RASTER_THRESHOLD },
				Names.POPUP_TOC_LEGEND_GROUP, false,
				IconLoader.getIcon("color_swatch.png"), wbContext);
	}

	@Override
	public boolean isEnabled() {

		try {
			MapContext mapContext = getPlugInContext().getMapContext();
			if (mapContext != null) {
				ILayer[] selectedLayers = mapContext.getSelectedLayers();

				if (selectedLayers != null) {

					if (selectedLayers.length == 1) {

						ILayer layer = selectedLayers[0];
						if (layer.isRaster()) {
							final int type = layer.getRaster().getType();
							if ((type == ImagePlus.GRAY8)
									|| (type == ImagePlus.GRAY16)
									|| (type == ImagePlus.GRAY32)) {
								return true;
							}
						}
					}
				}
			}
		} catch (DriverException e) {
		} catch (IOException e) {
			Services.getErrorManager().error(
					"Raster type unreadable for this layer", e);
		}

		return false;
	}
}