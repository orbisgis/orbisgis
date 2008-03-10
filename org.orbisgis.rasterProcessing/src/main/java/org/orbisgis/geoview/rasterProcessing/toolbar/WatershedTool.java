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
package org.orbisgis.geoview.rasterProcessing.toolbar;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.hydrology.GridDirection;
import org.grap.processing.hydrology.WatershedFromOutletIndex;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.AbstractPointTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class WatershedTool extends AbstractPointTool {
	public boolean isEnabled(ViewContext vc, ToolManager tm) {
		if (vc.getSelectedLayers().length == 1) {
			if (vc.getSelectedLayers()[0] instanceof RasterLayer) {
				return vc.getSelectedLayers()[0].isVisible();
			}
		}
		return false;
	}

	public boolean isVisible(ViewContext vc, ToolManager tm) {
		return true;
	}

	private int fromRealWorldCoordinateToOutletIndex(final GeoRaster geoRaster,
			final Coordinate realWorldCoordinate) throws IOException,
			GeoreferencingException {
		final Point2D pixelGridCoord = geoRaster
				.fromRealWorldCoordToPixelGridCoord(realWorldCoordinate.x,
						realWorldCoordinate.y);
		final int pixelX = (int) pixelGridCoord.getX();
		final int pixelY = (int) pixelGridCoord.getY();
		return pixelY * geoRaster.getWidth() + pixelX;
	}

	@Override
	protected void pointDone(Point point, ViewContext vc, ToolManager tm)
			throws TransitionException {
		final ILayer layer = vc.getSelectedLayers()[0];
		final GeoRaster geoRaster = ((RasterLayer) layer).getGeoRaster();
		final Coordinate realWorldCoordinate = point.getCoordinate();

		try {
			final int outletIndex = fromRealWorldCoordinateToOutletIndex(
					geoRaster, realWorldCoordinate);

			if ((outletIndex >= 0)
					&& (outletIndex < geoRaster.getMetadata().getNRows()
							* geoRaster.getMetadata().getNCols())) {
				// compute the slopes directions
				final Operation slopesDirections = new GridDirection();
				final GeoRaster grSlopesDirections = geoRaster
						.doOperation(slopesDirections);

				// find the good watershed starting from the outletIndex
				final Operation watershedFromOutletIndex = new WatershedFromOutletIndex(
						outletIndex);
				final GeoRaster grWatershedFromOutletIndex = grSlopesDirections
						.doOperation(watershedFromOutletIndex);

				// TODO : remove next instruction ?
				grWatershedFromOutletIndex.setRangeColors(new double[] { -0.5,
						1.5 }, new Color[] { Color.RED });

				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = OrbisgisCore.getDSF();
				final String tempFile = dsf.getTempFile() + ".tif";
				grWatershedFromOutletIndex.save(tempFile);

				// populate the GeoView TOC with a new RasterLayer
				final ILayer newLayer = LayerFactory
						.createRasterLayer(new File(tempFile));
				vc.getLayerModel().insertLayer(newLayer, 0);
			}
		} catch (IOException e) {
			PluginManager.error("Problem to access the GeoRaster", e);
		} catch (GeoreferencingException e) {
			PluginManager.error(
					"GeoReferencing problem while accessing the GeoRaster", e);
		} catch (LayerException e) {
			PluginManager.error("Problem adding the new layer", e);
		} catch (CRSException e) {
			PluginManager.error("CRS error while adding the new layer", e);
		} catch (OperationException e) {
			PluginManager.error("Operation error with thie GeoRaster", e);
		}
	}
}