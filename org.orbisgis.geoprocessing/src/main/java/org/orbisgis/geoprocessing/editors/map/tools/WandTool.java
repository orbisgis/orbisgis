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

import ij.gui.Wand;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.grap.model.GeoRaster;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.legend.carto.UniqueSymbolLegend;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.editors.map.tools.AbstractPointTool;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class WandTool extends AbstractPointTool {
	private final static String wandLayername = "wand";
	private final static DataSourceFactory dsf = ((DataManager) Services
			.getService(DataManager.class)).getDSF();
	private final static GeometryFactory geometryFactory = new GeometryFactory();
	private final static UniqueSymbolLegend uniqueSymbolLegend = LegendFactory
			.createUniqueSymbolLegend();
	static {
		Symbol polygonSymbol = SymbolFactory.createPolygonSymbol(null,
				Color.ORANGE);
		uniqueSymbolLegend.setSymbol(polygonSymbol);
	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		try {
			if ((vc.getSelectedLayers().length == 1)
					&& vc.getSelectedLayers()[0].isRaster()
					&& vc.getSelectedLayers()[0].isVisible()) {
				return true;
			}
		} catch (DriverException e) {
		}
		return false;
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

	@Override
	protected void pointDone(Point point, MapContext vc, ToolManager tm)
			throws TransitionException {
		try {
			final ILayer layer = vc.getSelectedLayers()[0];
			final GeoRaster geoRaster = layer.getRaster();
			final Coordinate realWorldCoordinate = point.getCoordinate();
			final Point2D gridContextCoordinate = geoRaster
					.fromRealWorldToPixel(realWorldCoordinate.x,
							realWorldCoordinate.y);
			final int pixelX = (int) gridContextCoordinate.getX();
			final int pixelY = (int) gridContextCoordinate.getY();
			final float halfPixelSize_X = geoRaster.getMetadata()
					.getPixelSize_X() / 2;
			final float halfPixelSize_Y = geoRaster.getMetadata()
					.getPixelSize_Y() / 2;

			final Wand w = new Wand(geoRaster.getImagePlus().getProcessor());
			w.autoOutline(pixelX, pixelY);

			final Coordinate[] jtsCoords = new Coordinate[w.npoints + 1];
			for (int i = 0; i < w.npoints; i++) {
				final Point2D worldXY = geoRaster.fromPixelToRealWorld(
						w.xpoints[i], w.ypoints[i]);
				jtsCoords[i] = new Coordinate(worldXY.getX() - halfPixelSize_X,
						worldXY.getY() - halfPixelSize_Y);
			}
			jtsCoords[w.npoints] = jtsCoords[0];
			final LinearRing shell = geometryFactory
					.createLinearRing(jtsCoords);
			final Polygon polygon = geometryFactory.createPolygon(shell, null);

			if (dsf.getSourceManager().exists(wandLayername)) {
				dsf.remove(wandLayername);
				vc.getLayerModel().remove(wandLayername);
			}
			DataManager dataManager = (DataManager) Services
					.getService(DataManager.class);
			final ILayer wandLayer = dataManager
					.createLayer(buildWandDatasource(polygon));

			vc.getLayerModel().insertLayer(wandLayer, 0);
			wandLayer.setLegend(uniqueSymbolLegend);
		} catch (LayerException e) {
			Services.getErrorManager().error(
					"Cannot use wand tool: " + e.getMessage(), e);
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot apply the legend : " + e.getMessage(), e);
		} catch (IOException e) {
			Services.getErrorManager().error(
					"Error accessing the GeoRaster : " + e.getMessage(), e);
		} catch (DriverLoadException e) {
			Services.getErrorManager().error(
					"Error accessing the wand layer datasource : "
							+ e.getMessage(), e);
		} catch (NoSuchTableException e) {
			Services.getErrorManager().error(
					"Error accessing the wand layer datasource : "
							+ e.getMessage(), e);
		} catch (DataSourceCreationException e) {
			Services.getErrorManager().error(
					"Error accessing the wand layer datasource : "
							+ e.getMessage(), e);
		} catch (NonEditableDataSourceException e) {
			Services.getErrorManager().error(
					"Error committing the wand layer datasource : "
							+ e.getMessage(), e);
		}
	}

	private DataSource buildWandDatasource(final Polygon polygon)
			throws DriverLoadException, NoSuchTableException,
			DataSourceCreationException, DriverException,
			NonEditableDataSourceException {
		final ObjectMemoryDriver driver = new ObjectMemoryDriver(new String[] {
				"the_geom", "area" }, new Type[] {
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.DOUBLE) });
		dsf.getSourceManager().register(wandLayername, driver);

		final DataSource dsResult = dsf.getDataSource(wandLayername);
		dsResult.open();
		dsResult.insertFilledRow(new Value[] {
				ValueFactory.createValue(polygon),
				ValueFactory.createValue(polygon.getArea()) });
		dsResult.commit();
		dsResult.close();

		return dsResult;
	}
}