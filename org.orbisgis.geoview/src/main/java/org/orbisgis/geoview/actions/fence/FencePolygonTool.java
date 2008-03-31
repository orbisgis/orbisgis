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
package org.orbisgis.geoview.actions.fence;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.renderer.legend.LegendFactory;
import org.orbisgis.geoview.renderer.legend.Symbol;
import org.orbisgis.geoview.renderer.legend.SymbolFactory;
import org.orbisgis.geoview.renderer.legend.UniqueSymbolLegend;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.workspace.Workspace;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.AbstractPolygonTool;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class FencePolygonTool extends AbstractPolygonTool {
	private final DataSourceFactory dsf = OrbisgisCore.getDSF();

	private VectorLayer layer;

	private final String fenceLayerName = "fence";

	protected void polygonDone(Polygon g, ViewContext vc, ToolManager tm)
			throws TransitionException {
		
			if (null != layer) {
				try {
					vc.getLayerModel().remove(layer);
				} catch (LayerException e) {
					PluginManager.error("Cannot removed the layer", e);
				}
			}
			buildFenceDatasource(g, vc);
			
		
	}

	public boolean isEnabled(ViewContext vc, ToolManager tm) {
		return vc.getLayerModel().getLayerCount() > 0;
	}

	public boolean isVisible(ViewContext vc, ToolManager tm) {
		return true;
	}

	private void buildFenceDatasource(Geometry g, ViewContext vc) {

		
		//TODO : Register the source in the geocatalog
		try {
			
			if (dsf.getSourceManager().exists(fenceLayerName)) {
				dsf.getSourceManager().remove(fenceLayerName);
			}
			
			DefaultMetadata metadata = new DefaultMetadata();

			{
				metadata.addField("area", Type.DOUBLE );
				metadata.addField("perimeter", Type.DOUBLE );
			}

			{
				Constraint geometryTypeConstraint = new GeometryConstraint(
						GeometryConstraint.POLYGON);
				metadata.addField("the_geom", Type.GEOMETRY,
						new Constraint[] { geometryTypeConstraint });
			}

			Workspace workspace = PluginManager.getWorkspace();
			File tempDir = workspace.getFile("temp");

			String filePathName = tempDir.getAbsolutePath() + "/"
					+ fenceLayerName + ".shp";

			File shpFile = new File(filePathName);
			shpFile.delete();

			FileSourceCreation fileSourceCreation = new FileSourceCreation(
					shpFile, metadata);
			dsf.createDataSource(fileSourceCreation);

			DataSource ds = dsf.getDataSource(shpFile);
			
			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
			sds.open();
			ds.insertEmptyRow();
			sds.setGeometry(0, g);
			sds.setDouble(0, "area", g.getArea());
			sds.setDouble(0, "perimeter", g.getLength());
			ds.commit();

			final VectorLayer layer = LayerFactory.createVectorialLayer(ds);
			final UniqueSymbolLegend l = LegendFactory
					.createUniqueSymbolLegend();
			final Symbol polSym = SymbolFactory.createPolygonSymbol(
					new BasicStroke(4), Color.ORANGE, null);
			l.setSymbol(polSym);
			layer.setLegend(l);
			
			try {
				layer.setName(fenceLayerName);
				vc.getLayerModel().insertLayer(layer, 0);
			} catch (LayerException e) {
				PluginManager.error("Impossible to create the layer:"
						+ layer.getName(), e);

			} catch (CRSException e) {
				PluginManager
						.error("CRS error in layer: " + layer.getName(), e);

			}

		} catch (DriverLoadException e) {
			PluginManager.error("Error while recovering fence vectorial layer", e);
		} catch (DataSourceCreationException e) {
			PluginManager.error("Error while creating fence vectorial layer", e);
		} catch (DriverException e) {
			PluginManager.error("Error while populating fence vectorial layer", e);
		} catch (FreeingResourcesException e) {
			PluginManager.error("Error while committing fence vectorial layer", e);
		} catch (NonEditableDataSourceException e) {
			PluginManager.error("Error while committing fence vectorial layer", e);
		} 

	}
}