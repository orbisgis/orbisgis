/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.rasterProcessing.action.terrainAnalysis.hydrology;

import ij.ImagePlus;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.hydrology.AllOutlets;
import org.grap.processing.hydrology.AllWatersheds;
import org.grap.processing.hydrology.GridAccumulation;
import org.grap.processing.hydrology.GridDirection;
import org.grap.processing.hydrology.WatershedsWithThreshold;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;
import org.sif.multiInputPanel.IntType;
import org.sif.multiInputPanel.MultiInputPanel;

public class ProcessAllWatersheds implements
		org.orbisgis.geoview.views.toc.ILayerAction {

	public boolean accepts(ILayer layer) {
		if (layer instanceof RasterLayer){
			RasterLayer rs = (RasterLayer) layer;
			
			try {
				int type = rs.getGeoRaster().getType();
				
				if ((type == ImagePlus.GRAY16)||(type == ImagePlus.GRAY32)) {
					return true;
					
				}
				
			} catch (IOException e) {			
			
			} catch (GeoreferencingException e) {			
				
			}
			}
		return false;
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount >= 1;
	}

	public void execute(GeoView2D view, ILayer resource) {
		final Integer watershedThreshold = getWatershedThreshold();

		if (null != watershedThreshold) {
			final GeoRaster geoRasterSrc = ((RasterLayer) resource)
					.getGeoRaster();
			try {
				geoRasterSrc.open();

				// compute the slopes directions
				final Operation slopesDirections = new GridDirection();
				final GeoRaster grSlopesDirections = geoRasterSrc
						.doOperation(slopesDirections);
				// compute all watersheds
				final Operation allWatersheds = new AllWatersheds();
				final GeoRaster grAllWatersheds = grSlopesDirections
						.doOperation(allWatersheds);

				GeoRaster watershedsResult;

				if (-1 == watershedThreshold) {
					watershedsResult = grAllWatersheds;
				} else {
					// compute the slopes accumulations
					final Operation slopesAccumulations = new GridAccumulation();
					final GeoRaster grSlopesAccumulations = grSlopesDirections
							.doOperation(slopesAccumulations);

					// find all outlets
					final Operation allOutlets = new AllOutlets();
					final GeoRaster grAllOutlets = grSlopesDirections
							.doOperation(allOutlets);

					// extract some "big" watersheds
					final Operation watershedsWithThreshold = new WatershedsWithThreshold(
							grAllWatersheds, grAllOutlets, watershedThreshold);
					watershedsResult = grSlopesAccumulations
							.doOperation(watershedsWithThreshold);
				}

				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = OrbisgisCore.getDSF();
				final String tempFile = dsf.getTempFile() + ".tif";
				watershedsResult.save(tempFile);

				// populate the GeoView TOC with a new RasterLayer
				final ILayer newLayer = LayerFactory
						.createRasterLayer(new File(tempFile));
				view.getViewContext().getLayerModel().addLayer(newLayer);

			} catch (GeoreferencingException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (IOException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (OperationException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (LayerException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (CRSException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			}
		}
	}

	private Integer getWatershedThreshold() {
		final MultiInputPanel mip = new MultiInputPanel(
				"Watershed process initialization");
		mip.addInput("WatershedThreshold", "Watershed threshold value", "-1",
				new IntType(5));
		mip.addValidationExpression("WatershedThreshold >= -1",
				"WatershedThreshold must be greater or equal to -1 !");

		if (UIFactory.showDialog(mip)) {
			return new Integer(mip.getInput("WatershedThreshold"));
		} else {
			return null;
		}
	}

	public void executeAll(GeoView2D view, ILayer[] layers) {
	}
}