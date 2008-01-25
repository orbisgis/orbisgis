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
import java.util.ArrayList;

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
import org.orbisgis.geoview.views.table.Table;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.AbstractPointTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class WandTool extends AbstractPointTool {
	private final static DataSourceFactory dsf = OrbisgisCore.getDSF();

	private DataSource dsResult;

	
	private final String wandLayerName = "wand";
	
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
		ILayer layer = vc.getSelectedLayers()[0];
		final GeoRaster geoRaster = ((RasterLayer) layer).getGeoRaster();
		final Coordinate realWorldCoordinate = point.getCoordinate();
		final Point2D mapContextCoordinate = geoRaster.getPixelCoords(
				realWorldCoordinate.x, realWorldCoordinate.y);
		final int pixelX = (int) mapContextCoordinate.getX();
		final int pixelY = (int) mapContextCoordinate.getY();
		
		Wand w;
		try {
			w = new Wand(geoRaster.getGrapImagePlus().getProcessor());
			w.autoOutline(pixelX, pixelY);
			System.out.println("Points:" + w.npoints);
			
			int xWand;
			int yWand;
			Point2D worldXY ;
			Coordinate[] jtsCoords = new Coordinate[w.npoints];
			Coordinate jtsCoord;
			for (int i = 0; i < w.npoints; i++) {
				
				xWand = w.xpoints[i];
				yWand = w.ypoints[i];
				
				
				worldXY = vc.toMapPoint(xWand, yWand);
				jtsCoord = new Coordinate(worldXY.getX(), worldXY.getY());
				System.out.println("jtsCoord:" + jtsCoord);
				jtsCoords[i]=jtsCoord;			
			}
			
			
			buildWandDatasource(jtsCoords);
			layer = LayerFactory.createVectorialLayer(wandLayerName, dsResult);
			BasicStyle style = new BasicStyle(Color.RED, 10, null);

			layer.setStyle(style);
			vc.getLayerModel().addLayer(layer);
			
		
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (GeoreferencingException e) {
			e.printStackTrace();
		} catch (LayerException e) {
			e.printStackTrace();
		} catch (CRSException e) {
			e.printStackTrace();
		}
		
		
			
		
	}
	
	
	private String buildWandDatasource(Coordinate[] jtsCoords) {

		ObjectMemoryDriver driver;
		try {
			driver = new ObjectMemoryDriver(new String[] { "the_geom" },
					new Type[] { TypeFactory.createType(Type.GEOMETRY) });

			if (!dsf.getSourceManager().exists(wandLayerName)) {
				dsf.getSourceManager().register(wandLayerName, driver);
			}

			dsResult = dsf.getDataSource(wandLayerName);

			dsResult.open();

			while (dsResult.getRowCount() > 0) {
				dsResult.deleteRow(0);
			}

			if (dsResult.getFieldCount() == 0) {
				dsResult.addField("the_geom", TypeFactory
						.createType(Type.GEOMETRY));
			}
			
			GeometryFactory gf = new GeometryFactory();
			
			LineString g = gf.createLineString(jtsCoords);
			
			
			dsResult.insertFilledRow(new Value[] { ValueFactory.createValue(g) });

			dsResult.commit();

			return dsResult.getName();
		} catch (DriverLoadException e) {
			throw new RuntimeException(e);
		} catch (DataSourceCreationException e) {
			throw new RuntimeException(e);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		} catch (FreeingResourcesException e) {
			throw new RuntimeException(e);
		} catch (NonEditableDataSourceException e) {
			throw new RuntimeException(e);
		} catch (NoSuchTableException e) {
			throw new RuntimeException(e);
		}

	}
}