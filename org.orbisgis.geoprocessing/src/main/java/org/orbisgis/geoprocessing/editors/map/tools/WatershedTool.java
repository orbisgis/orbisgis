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
package org.orbisgis.geoprocessing.editors.map.tools;

import ij.ImagePlus;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpDirection;
import org.grap.processing.operation.hydrology.D8OpWatershedFromOutletIndex;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.editors.map.tools.AbstractPointTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class WatershedTool extends AbstractPointTool {
	public boolean isEnabled(MapContext vc, ToolManager tm) {
		try {
			if ((vc.getSelectedLayers().length == 1)
					&& vc.getSelectedLayers()[0].isRaster()
					&& vc.getSelectedLayers()[0].isVisible()) {
				final int type = vc.getSelectedLayers()[0].getRaster()
						.getType();
				return (type == ImagePlus.GRAY16) || (type == ImagePlus.GRAY32);
			}
		} catch (DriverException e) {
		} catch (IOException e) {
		}
		return false;
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

	private int fromRealWorldCoordinateToOutletIndex(final GeoRaster geoRaster,
			final Coordinate realWorldCoordinate) throws IOException {
		final Point2D pixelGridCoord = geoRaster
				.fromRealWorldToPixel(realWorldCoordinate.x,
						realWorldCoordinate.y);
		final int pixelX = (int) pixelGridCoord.getX();
		final int pixelY = (int) pixelGridCoord.getY();
		return pixelY * geoRaster.getWidth() + pixelX;
	}

	@Override
	protected void pointDone(Point point, MapContext vc, ToolManager tm)
			throws TransitionException {
		try {
			final GeoRaster geoRaster = vc.getSelectedLayers()[0].getRaster();
			final Coordinate realWorldCoordinate = point.getCoordinate();

			final int outletIndex = fromRealWorldCoordinateToOutletIndex(
					geoRaster, realWorldCoordinate);

			if ((outletIndex >= 0)
					&& (outletIndex < geoRaster.getMetadata().getNRows()
							* geoRaster.getMetadata().getNCols())) {
				// compute the slopes directions
				final Operation slopesDirections = new D8OpDirection();
				final GeoRaster grSlopesDirections = geoRaster
						.doOperation(slopesDirections);

				// find the good watershed starting from the outletIndex
				final Operation watershedFromOutletIndex = new D8OpWatershedFromOutletIndex(
						outletIndex);
				final GeoRaster grWatershedFromOutletIndex = grSlopesDirections
						.doOperation(watershedFromOutletIndex);

				// save the computed GeoRaster in a tempFile
				final DataSourceFactory dsf = ((DataManager) Services
						.getService(DataManager.class)).getDSF();
				final String tempFile = dsf.getTempFile() + ".tif";
				grWatershedFromOutletIndex.save(tempFile);

				// populate the GeoView TOC with a new RasterLayer
				DataManager dataManager = (DataManager) Services
						.getService(DataManager.class);
				final ILayer newLayer = dataManager.createLayer(new File(
						tempFile));
				vc.getLayerModel().insertLayer(newLayer, 0);
			}
		} catch (IOException e) {
			Services.getErrorManager().error("Problem to access the GeoRaster",
					e);
		} catch (LayerException e) {
			Services.getErrorManager().error("Problem adding the new layer", e);
		} catch (OperationException e) {
			Services.getErrorManager().error(
					"Operation error with thie GeoRaster", e);
		} catch (DriverLoadException e) {
			Services.getErrorManager().error(
					"Cannot create the resulting layer of raster type ", e);
		} catch (DriverException e) {
			Services.getErrorManager().error("Problem to access the GeoRaster",
					e);
		}
	}
}