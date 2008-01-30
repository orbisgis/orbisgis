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
import org.orbisgis.geoview.renderer.style.BasicStyle;
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
	private final static String WAND_LAYER_NAME = "wand";
	private final static DataSourceFactory dsf = OrbisgisCore.getDSF();
	private final static GeometryFactory geometryFactory = new GeometryFactory();
	private VectorLayer wandLayer;

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

	private void printPointForDebug(final ViewContext vc,
			final Coordinate pointCoordinate, final Color color) {
		final ObjectMemoryDriver ptDriver = new ObjectMemoryDriver(
				new String[] { "the_geom" }, new Type[] { TypeFactory
						.createType(Type.GEOMETRY) });
		ptDriver
				.addValues(new Value[] { ValueFactory
						.createValue(new GeometryFactory()
								.createPoint(pointCoordinate)) });
		final String tmpName = dsf.getSourceManager().nameAndRegister(ptDriver);
		try {
			final VectorLayer vectorLayer = LayerFactory
					.createVectorialLayer(dsf.getDataSource(tmpName));
			final BasicStyle style = new BasicStyle(color, 10, color);
			vectorLayer.setStyle(style);
			vc.getLayerModel().addLayer(vectorLayer);
			System.err.printf("%s %s\n", color.toString(), pointCoordinate
					.toString());
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (NoSuchTableException e) {
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			e.printStackTrace();
		} catch (LayerException e) {
			e.printStackTrace();
		} catch (CRSException e) {
			e.printStackTrace();
		}
	}

	private void printPointForDebug(final ViewContext vc, final Point2D world,
			final Color color) {
		final Coordinate c = new Coordinate(world.getX(), world.getY());
		printPointForDebug(vc, c, color);
	}

	@Override
	protected void pointDone(Point point, ViewContext vc, ToolManager tm)
			throws TransitionException {
		final ILayer layer = vc.getSelectedLayers()[0];
		final GeoRaster geoRaster = ((RasterLayer) layer).getGeoRaster();
		final Coordinate realWorldCoordinate = point.getCoordinate();

		final Point2D gridContextCoordinate = geoRaster.fromRealWorldCoordToPixelGridCoord(
				realWorldCoordinate.x, realWorldCoordinate.y);

		final int pixelX = (int) Math.round(gridContextCoordinate.getX());
		final int pixelY = (int) Math.round(gridContextCoordinate.getY());

		try {
			printPointForDebug(vc, realWorldCoordinate, Color.RED); // debug
			printPointForDebug(vc, geoRaster.fromPixelGridCoordToRealWorldCoord(pixelX, pixelY),
					Color.BLUE); // debug
			System.err.println(geoRaster.getGrapImagePlus().getPixelValue(
					pixelX, pixelY)); // debug

			final Wand w = new Wand(geoRaster.getGrapImagePlus().getProcessor());
			w.autoOutline(pixelX, pixelY);

			for (int i = 0; i < w.npoints; i++) {
				System.err.printf("==> %d %d\n", w.xpoints[i], w.ypoints[i]);
				// printPointForDebug(vc, geoRaster.pixelToWorldCoord(pixelX,
				// pixelY), Color.GREEN); // debug
			}

			final Coordinate[] jtsCoords = new Coordinate[w.npoints + 1];
			for (int i = 0; i < w.npoints; i++) {
				final Point2D worldXY = geoRaster.fromPixelGridCoordToRealWorldCoord(
						w.xpoints[i], w.ypoints[i]);
				jtsCoords[i] = new Coordinate(worldXY.getX(), worldXY.getY());
			}
			jtsCoords[w.npoints] = jtsCoords[0];

			if (!dsf.exists(WAND_LAYER_NAME)) {
				createWandObjectMemoryDriver();
				populateTheDriver(jtsCoords);
				addVectorLayer(vc);
			} else {
				populateTheDriver(jtsCoords);
			}
		} catch (IOException e) {
			PluginManager.error("", e);
		} catch (GeoreferencingException e) {
			PluginManager.error("", e);
		} catch (LayerException e) {
			PluginManager.error("", e);
		} catch (CRSException e) {
			PluginManager.error("", e);
		} catch (DriverLoadException e) {
			PluginManager.error("", e);
		} catch (NoSuchTableException e) {
			PluginManager.error("", e);
		} catch (DataSourceCreationException e) {
			PluginManager.error("", e);
		} catch (DriverException e) {
			PluginManager.error("", e);
		} catch (FreeingResourcesException e) {
			PluginManager.error("", e);
		} catch (NonEditableDataSourceException e) {
			PluginManager.error("", e);
		}
	}

	private void createWandObjectMemoryDriver() {
		final ObjectMemoryDriver driver = new ObjectMemoryDriver(new String[] {
				"the_geom", "area" }, new Type[] {
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.DOUBLE) });
		dsf.getSourceManager().register(WAND_LAYER_NAME, driver);
	}

	private void populateTheDriver(final Coordinate[] jtsCoords)
			throws DriverLoadException, NoSuchTableException,
			DataSourceCreationException, DriverException,
			FreeingResourcesException, NonEditableDataSourceException {
		final LinearRing shell = geometryFactory.createLinearRing(jtsCoords);
		final Polygon polygon = geometryFactory.createPolygon(shell, null);

		final DataSource ds = (null == wandLayer) ? dsf
				.getDataSource(WAND_LAYER_NAME) : wandLayer.getDataSource();
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue(polygon),
				ValueFactory.createValue(polygon.getArea()) });
		ds.commit();
	}

	private void addVectorLayer(final ViewContext vc)
			throws DriverLoadException, NoSuchTableException,
			DataSourceCreationException, LayerException, CRSException {
		final DataSource ds = dsf.getDataSource(WAND_LAYER_NAME);
		wandLayer = LayerFactory.createVectorialLayer(ds);
		final BasicStyle style = new BasicStyle(Color.RED, 10, null);

		wandLayer.setStyle(style);
		vc.getLayerModel().addLayer(wandLayer);
	}
}