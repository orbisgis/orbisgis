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

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.geoview.views.table.Table;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.AbstractPointTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class InfoTool extends AbstractPointTool {
	private final static DataSourceFactory dsf = OrbisgisCore.getDSF();

	public boolean isEnabled(ViewContext vc, ToolManager tm) {
		if (vc.getSelectedLayers().length == 1) {
			if (vc.getSelectedLayers()[0] instanceof RasterLayer) {
				return true;
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
		final Coordinate realWorldCoordinate = point.getCoordinate();
		final Point2D mapContextCoordinate = geoRaster.getPixelCoords(
				realWorldCoordinate.x, realWorldCoordinate.y);
		final int pixelX = (int) mapContextCoordinate.getX();
		final int pixelY = (int) mapContextCoordinate.getY();

		try {
			// create and populate a new datasource
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					new String[] { "pixel X", "pixel Y", "pixel value",
							"Raster width", "Raster height" }, new Type[] {
							TypeFactory.createType(Type.INT),
							TypeFactory.createType(Type.INT),
							TypeFactory.createType(Type.DOUBLE),
							TypeFactory.createType(Type.INT),
							TypeFactory.createType(Type.INT) });
			driver.addValues(new Value[] {
					ValueFactory.createValue(pixelX),
					ValueFactory.createValue(pixelY),
					ValueFactory.createValue(geoRaster.getGrapImagePlus()
							.getPixelValue(pixelX, pixelY)),
					ValueFactory.createValue(geoRaster.getWidth()),
					ValueFactory.createValue(geoRaster.getHeight()) });
			final String dsInfo = dsf.getSourceManager()
					.nameAndRegister(driver);

			// populate the table with the previous datasource
			final Table table = (Table) vc.getView().getView(
					"org.orbisgis.geoview.Table");
			table.setContents(dsf.getDataSource(dsInfo));

		} catch (IOException e) {
			PluginManager.error("", e);
		} catch (GeoreferencingException e) {
			PluginManager.error("", e);
		} catch (DriverLoadException e) {
			PluginManager.error("", e);
		} catch (DriverException e) {
			PluginManager.error("", e);
		} catch (NoSuchTableException e) {
			PluginManager.error("", e);
		} catch (DataSourceCreationException e) {
			PluginManager.error("", e);
		}
	}
}