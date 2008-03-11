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

import ij.gui.Wand;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
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
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.renderer.legend.LegendFactory;
import org.orbisgis.geoview.renderer.legend.Symbol;
import org.orbisgis.geoview.renderer.legend.SymbolFactory;
import org.orbisgis.geoview.renderer.legend.UniqueSymbolLegend;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.AbstractPointTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class WandTool extends AbstractPointTool {
	private final static String wandLayername = "wand";
	private final static DataSourceFactory dsf = OrbisgisCore.getDSF();
	private final static GeometryFactory geometryFactory = new GeometryFactory();

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
		final Coordinate realWorldCoordinate = point.getCoordinate();
		final Point2D gridContextCoordinate = geoRaster
				.fromRealWorldCoordToPixelGridCoord(realWorldCoordinate.x,
						realWorldCoordinate.y);
		final int pixelX = (int) gridContextCoordinate.getX();
		final int pixelY = (int) gridContextCoordinate.getY();
		final float halfPixelSize_X = geoRaster.getMetadata().getPixelSize_X() / 2;
		final float halfPixelSize_Y = geoRaster.getMetadata().getPixelSize_Y() / 2;

		try {
			final Wand w = new Wand(geoRaster.getGrapImagePlus().getProcessor());
			w.autoOutline(pixelX, pixelY);

			final Coordinate[] jtsCoords = new Coordinate[w.npoints + 1];
			for (int i = 0; i < w.npoints; i++) {
				final Point2D worldXY = geoRaster
						.fromPixelGridCoordToRealWorldCoord(w.xpoints[i],
								w.ypoints[i]);
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
			final VectorLayer wandLayer = LayerFactory
					.createVectorialLayer(buildWandDatasource(polygon));

			final UniqueSymbolLegend uniqueSymbolLegend = LegendFactory
					.createUniqueSymbolLegend();
			final Symbol polygonSymbol = SymbolFactory.createPolygonSymbol(
					null, Color.ORANGE);
			uniqueSymbolLegend.setSymbol(polygonSymbol);
			wandLayer.setLegend(uniqueSymbolLegend);

			vc.getLayerModel().insertLayer(wandLayer, 0);

			// TODO : patch line to remove...
			vc.getView().getMap().setExtent(vc.getExtent());
		} catch (LayerException e) {
			PluginManager.error("Cannot use wand tool: " + e.getMessage(), e);
		} catch (DriverException e) {
			PluginManager.error("Cannot apply the legend : " + e.getMessage(),
					e);
		} catch (IOException e) {
			PluginManager.error("Error accessing the GeoRaster : "
					+ e.getMessage(), e);
		} catch (GeoreferencingException e) {
			PluginManager.error(
					"GeoReferencing Error accessing the GeoRaster : "
							+ e.getMessage(), e);
		} catch (DriverLoadException e) {
			PluginManager.error("Error accessing the wand layer datasource : "
					+ e.getMessage(), e);
		} catch (NoSuchTableException e) {
			PluginManager.error("Error accessing the wand layer datasource : "
					+ e.getMessage(), e);
		} catch (DataSourceCreationException e) {
			PluginManager.error("Error accessing the wand layer datasource : "
					+ e.getMessage(), e);
		} catch (FreeingResourcesException e) {
			PluginManager.error("Error committing the wand layer datasource : "
					+ e.getMessage(), e);
		} catch (NonEditableDataSourceException e) {
			PluginManager.error("Error committing the wand layer datasource : "
					+ e.getMessage(), e);
		} catch (CRSException e) {
			PluginManager.error(
					"CRS Error trying to add the wand layer to the TOC : "
							+ e.getMessage(), e);
		}
	}

	private DataSource buildWandDatasource(final Polygon polygon)
			throws DriverLoadException, NoSuchTableException,
			DataSourceCreationException, DriverException,
			FreeingResourcesException, NonEditableDataSourceException {
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

		return dsResult;
	}
}