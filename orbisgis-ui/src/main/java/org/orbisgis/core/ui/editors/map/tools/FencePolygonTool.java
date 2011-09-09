/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.editors.map.tools;

import java.awt.Color;
import java.util.Observable;

import javax.swing.AbstractButton;

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
import org.gdms.driver.generic.GenericObjectDriver;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.legend.carto.UniqueSymbolLegend;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class FencePolygonTool extends AbstractPolygonTool {
	private DataSource dsResult;
	private ILayer layer;
	private final String fenceLayerName = "fence";
	AbstractButton button;

	@Override
	public AbstractButton getButton() {
		return button;
	}

	public void setButton(AbstractButton button) {
		this.button = button;
	}

	@Override
	public void update(Observable o, Object arg) {
		PlugInContext.checkTool(this);
	}

	protected void polygonDone(Polygon g, MapContext vc, ToolManager tm)
			throws TransitionException {
		try {
			if (null != layer) {
				vc.getLayerModel().remove(layer);
			}
			buildFenceDatasource(g);
			DataManager dataManager = (DataManager) Services
					.getService(DataManager.class);
			layer = dataManager.createLayer(dsResult);

			vc.getLayerModel().insertLayer(layer, 0);

			final UniqueSymbolLegend l = LegendFactory
					.createUniqueSymbolLegend();
			final Symbol polSym = SymbolFactory.createPolygonSymbol(
					Color.ORANGE, 4, null);
			l.setSymbol(polSym);
			layer.setLegend(l);
		} catch (LayerException e) {
			Services.getErrorManager().error(
					"Cannot use fence tool: " + e.getMessage(), e);
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot apply the legend : " + e.getMessage(), e);
		}
	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return vc.getLayerModel().getLayerCount() > 0;
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

	private String buildFenceDatasource(Geometry g) {
		try {
			final GenericObjectDriver driver = new GenericObjectDriver(
					new String[] { "the_geom" }, new Type[] { TypeFactory
							.createType(Type.GEOMETRY) });

			DataSourceFactory dsf = ((DataManager) Services
					.getService(DataManager.class)).getDataSourceFactory();

			if (!dsf.getSourceManager().exists(fenceLayerName)) {
				dsf.getSourceManager().register(fenceLayerName, driver);
			}
			dsResult = dsf.getDataSource(fenceLayerName);
			dsResult.open();

			while (dsResult.getRowCount() > 0) {
				dsResult.deleteRow(0);
			}

			if (dsResult.getFieldCount() == 0) {
				dsResult.addField("the_geom", TypeFactory
						.createType(Type.GEOMETRY));
			}
			dsResult
					.insertFilledRow(new Value[] { ValueFactory.createValue(g) });
			dsResult.commit();
			dsResult.close();

			return dsResult.getName();
		} catch (DriverLoadException e) {
			Services.getErrorManager().error(
					"Error while recovering fence vectorial layer", e);
		} catch (DataSourceCreationException e) {
			Services.getErrorManager().error(
					"Error while creating fence vectorial layer", e);
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Error while populating fence vectorial layer", e);
		} catch (NonEditableDataSourceException e) {
			Services.getErrorManager().error(
					"Error while committing fence vectorial layer", e);
		} catch (NoSuchTableException e) {
			Services.getErrorManager().error(
					"Error while creating fence vectorial layer", e);
		}
		return null;
	}

	public String getName() {
		return I18N.getString("orbisgis.core.ui.editors.map.tool.fence");
	}
}