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

import java.awt.geom.Point2D;
import java.io.IOException;

import org.gdms.driver.driverManager.DriverLoadException;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.AbstractPointTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class InfoTool extends AbstractPointTool {
	public final static String[] LABELS = new String[] { "pixel X", "pixel Y",
			"pixel value", "Raster width", "Raster height", "RealWorld X",
			"RealWorld Y" };

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

	@Override
	protected void pointDone(Point point, ViewContext vc, ToolManager tm)
			throws TransitionException {
		final ILayer layer = vc.getSelectedLayers()[0];
		final GeoRaster geoRaster = ((RasterLayer) layer).getGeoRaster();
		final Coordinate realWorldCoord = point.getCoordinate();

		final Point2D pixelGridCoord = geoRaster
				.fromRealWorldCoordToPixelGridCoord(realWorldCoord.x,
						realWorldCoord.y);

		final int pixelX = (int) pixelGridCoord.getX();
		final int pixelY = (int) pixelGridCoord.getY();

		try {
			final float pixelValue = geoRaster.getGrapImagePlus()
					.getPixelValue(pixelX, pixelY);
			final int width = geoRaster.getWidth();
			final int height = geoRaster.getHeight();

			// populate the PixelInfoView...
			final PixelInfoPanel pixelInfoPanel = (PixelInfoPanel) vc
					.getView()
					.getView(
							"org.orbisgis.geoview.rasterProcessing.toolbar.PixelInfoView");
			pixelInfoPanel.setValues(new Object[] { pixelX, pixelY, pixelValue,
					width, height, realWorldCoord.x, realWorldCoord.y });

		} catch (IOException e) {
			PluginManager.error("Problem while accessing GeoRaster datas", e);
		} catch (GeoreferencingException e) {
			PluginManager.error("Problem while accessing GeoRaster datas", e);
		} catch (DriverLoadException e) {
			PluginManager.error("Problem with the ObjectMemoryDriver", e);
		}
	}
}