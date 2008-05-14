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
package org.orbisgis.editors.map.tools;

import java.awt.Component;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.DataManager;
import org.orbisgis.IProgressMonitor;
import org.orbisgis.Services;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.pluginManager.background.DefaultJobId;
import org.orbisgis.view.ViewManager;
import org.orbisgis.views.table.Table;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.io.WKTWriter;

public class InfoTool extends AbstractRectangleTool {

	private static Logger logger = Logger.getLogger(InfoTool.class);

	@Override
	protected void rectangleDone(Rectangle2D rect,
			boolean smallerThanTolerance, MapContext vc, ToolManager tm)
			throws TransitionException {
		ILayer layer = vc.getSelectedLayers()[0];

		DataSource ds = layer.getDataSource();
		String sql = null;
		try {
			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
			GeometryFactory gf = ToolManager.toolsGeometryFactory;
			double minx = rect.getMinX();
			double miny = rect.getMinY();
			double maxx = rect.getMaxX();
			double maxy = rect.getMaxY();

			Coordinate lowerLeft = new Coordinate(minx, miny);
			Coordinate upperRight = new Coordinate(maxx, maxy);
			LinearRing envelopeShell = gf.createLinearRing(new Coordinate[] {
					lowerLeft, new Coordinate(minx, maxy), upperRight,
					new Coordinate(maxx, miny), lowerLeft, });
			Geometry geomEnvelope = gf.createPolygon(envelopeShell,
					new LinearRing[0]);
			WKTWriter writer = new WKTWriter();
			sql = "select * from \"" + layer.getName() + "\" where intersects("
					+ sds.getDefaultGeometry() + ", geomfromtext('"
					+ writer.write(geomEnvelope) + "'));";
			BackgroundManager bm = (BackgroundManager) Services
					.getService("org.orbisgis.BackgroundManager");
			bm.backgroundOperation(new DefaultJobId(
					"org.orbisgis.jobs.InfoTool"), new PopulateViewJob(sql));
		} catch (DriverException e) {
			throw new TransitionException(e);
		} catch (DriverLoadException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		if (vc.getSelectedLayers().length == 1) {
			try {
				if (vc.getSelectedLayers()[0].isVectorial()) {
					return vc.getSelectedLayers()[0].isVisible();
				}
			} catch (DriverException e) {
				return false;
			}
		}

		return false;
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

	private class PopulateViewJob implements BackgroundJob {

		private String sql;

		public PopulateViewJob(String sql) {
			this.sql = sql;
		}

		public String getTaskName() {
			return "Getting info";
		}

		public void run(IProgressMonitor pm) {
			ViewManager vm = (ViewManager) Services
					.getService("org.orbisgis.ViewManager");
			Component comp = vm.getView("org.orbisgis.views.Table");
			final Table table = (Table) comp;
			try {
				logger.debug("Info query: " + sql);
				final DataSource ds = ((DataManager) Services
						.getService("org.orbisgis.DataManager")).getDSF()
						.getDataSourceFromSQL(sql, pm);
				if (!pm.isCancelled()) {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							try {
								table.setContents(ds);
							} catch (DriverException e) {
								Services.getErrorManager().error(
										"Cannot show the data", e);
							}
						}

					});
				}
			} catch (DataSourceCreationException e) {
				Services.getErrorManager().error("Cannot get the result", e);
			} catch (DriverException e) {
				Services.getErrorManager().error("Cannot access the data", e);
			} catch (ParseException e) {
				Services.getErrorManager().error("Bug: " + sql, e);
			} catch (SemanticException e) {
				Services.getErrorManager().error("Bug: " + sql, e);
			}
		}

	}

}
