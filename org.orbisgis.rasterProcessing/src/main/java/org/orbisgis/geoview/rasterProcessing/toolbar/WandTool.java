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

			if (!dsf.exists(WAND_LAYER_NAME)) {
				createWandObjectMemoryDriver();
				populateTheDriver(jtsCoords);
				addVectorLayer(vc);
			} else {
				populateTheDriver(jtsCoords);
			}
		} catch (IOException e) {
			PluginManager.error(
					"Problem to access the GeoRaster ImagePlus values", e);
		} catch (GeoreferencingException e) {
			PluginManager
					.error(
							"GeoReferencing problem while accessing the GeoRaster ImagePlus values",
							e);
		} catch (LayerException e) {
			PluginManager.error("Problem adding the wand VectorLayer", e);
		} catch (CRSException e) {
			PluginManager.error("CRS error while adding the wand VectorLayer",
					e);
		} catch (DriverLoadException e) {
			PluginManager.error("Problem while accessing the wand DataSource",
					e);
		} catch (NoSuchTableException e) {
			PluginManager.error("Problem while accessing the wand DataSource",
					e);
		} catch (DataSourceCreationException e) {
			PluginManager
					.error("Problem while creating the wand DataSource", e);
		} catch (DriverException e) {
			PluginManager.error("Problem while editing the wand DataSource", e);
		} catch (FreeingResourcesException e) {
			PluginManager.error("Problem while committing the wand DataSource",
					e);
		} catch (NonEditableDataSourceException e) {
			PluginManager.error("Wand DataSource is not editable", e);
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
		vc.getLayerModel().addLayer(wandLayer);
	}
}