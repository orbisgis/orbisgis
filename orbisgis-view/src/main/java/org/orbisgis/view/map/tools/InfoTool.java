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
package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.io.WKTWriter;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.DefaultJobId;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;

public class InfoTool extends AbstractRectangleTool {
        private static Logger UILOGGER = Logger.getLogger("gui."+InfoTool.class);

	@Override
	public void update(Observable o, Object arg) {
		//PlugInContext.checkTool(this);
	}

	@Override
	protected void rectangleDone(Rectangle2D rect,
			boolean smallerThanTolerance, MapContext vc, ToolManager tm)
			throws TransitionException {
		ILayer layer = vc.getSelectedLayers()[0];
		DataSource sds = layer.getDataSource();
		String sql;
		try {
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
			sql = "select * from " + layer.getName() + " where ST_intersects("
					+ sds.getMetadata().getFieldName(sds.getSpatialFieldIndex()) + ", ST_geomfromtext('"
					+ writer.write(geomEnvelope) + "'));";
			BackgroundManager bm = (BackgroundManager) Services
					.getService(BackgroundManager.class);
			bm.backgroundOperation(new DefaultJobId(
					"org.orbisgis.jobs.InfoTool"), new PopulateViewJob(sql));
		} catch (DriverException e) {
			throw new TransitionException(e);
		} catch (DriverLoadException e) {
			throw new RuntimeException(e);
		}
	}

        @Override
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

        @Override
	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

	private class PopulateViewJob implements BackgroundJob {

		private String sql;

		public PopulateViewJob(String sql) {
			this.sql = sql;
		}

                @Override
		public String getTaskName() {
			return "Getting info";
		}

                @Override
		public void run(ProgressMonitor pm) {
			try {
				UILOGGER.debug("Info query: " + sql);
                                if (!pm.isCancelled()) {
                                //					try {
                                //						Services.getService(InformationManager.class).setContents(ds);
                                //					} catch (DriverException e) {
//						Services.getErrorManager().error(
                                //								"Cannot show the data", e);
                                //					}
				}
                        } catch (DriverLoadException e) {
				UILOGGER.error("Cannot execute the query", e);
                        }
		}
	}

        @Override
	public String getName() {
		return I18N.tr("Get feature attributes");
	}

        @Override
        public ImageIcon getImageIcon() {
            return OrbisGISIcon.getIcon("information");
        }
}
