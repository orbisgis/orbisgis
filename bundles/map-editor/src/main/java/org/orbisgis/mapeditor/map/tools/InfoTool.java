/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly: info_at_ orbisgis.org
 */
package org.orbisgis.mapeditor.map.tools;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Observable;
import javax.sql.DataSource;
import javax.swing.ImageIcon;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.apache.log4j.Logger;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.Services;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.corejdbc.ReadTable;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.renderer.ResultSetProviderFactory;
import org.orbisgis.mapeditor.map.CachedResultSetContainer;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.DefaultJobId;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;

/**
 * Show selected geometry information.
 */
public class InfoTool extends AbstractRectangleTool {

    private static Logger UILOGGER = Logger.getLogger("gui." + InfoTool.class);
    private static Logger POPUPLOGGER = Logger.getLogger("popup." + InfoTool.class);
    private static final int MAX_PRINTED_ROWS = 100;
    private static final int MAX_FIELD_LENGTH = 512;
    /** Info is shown on popup if the attributes length is not superior than this constant*/
    private static final int POPUP_MAX_LENGTH = 200;

    @Override
    public void update(Observable o, Object arg) {
        //PlugInContext.checkTool(this);
    }

    @Override
    protected void rectangleDone(Rectangle2D rect,
            boolean smallerThanTolerance, MapContext vc, ToolManager tm)
            throws TransitionException {
        ILayer layer = vc.getSelectedLayers()[0];
        double minx = rect.getMinX();
        double miny = rect.getMinY();
        double maxx = rect.getMaxX();
        double maxy = rect.getMaxY();
        BackgroundManager bm = Services.getService(BackgroundManager.class);
        bm.backgroundOperation(new DefaultJobId(
                "org.orbisgis.jobs.InfoTool"), new PopulateViewJob(new Envelope(minx, maxx, miny, maxy), vc.getDataManager().getDataSource(),layer));

    }

    @Override
    public boolean isEnabled(MapContext vc, ToolManager tm) {
        if (vc.getSelectedLayers().length == 1) {
            if (!vc.getSelectedLayers()[0].getTableReference().isEmpty()) {
                return vc.getSelectedLayers()[0].isVisible();
            }
        }
        return false;
    }

    @Override
    public boolean isVisible(MapContext vc, ToolManager tm) {
        return true;
    }

    /**
     * This class is used to print the selected features in the console, or in the popup if there is only one feature.
     */
    private class PopulateViewJob implements BackgroundJob {

        private final Envelope envelope;
        private final DataSource sds;
        private final ILayer layer;

        private PopulateViewJob(Envelope envelope, DataSource sds, ILayer layer) {
            this.envelope = envelope;
            this.sds = sds;
            this.layer = layer;
        }

        @Override
        public String getTaskName() {
            return "Getting info";
        }

        @Override
        public void run(ProgressMonitor pm) {
            try(ResultSetProviderFactory.ResultSetProvider rsCont =
                        tm.getCachedResultSetContainer().getResultSetProvider(layer, pm);
                    ResultSet rs = rsCont.execute(pm, envelope)) {
                String lines = ReadTable.resultSetToString(rs, MAX_FIELD_LENGTH, MAX_PRINTED_ROWS, false, false,
                        new EnvelopeFilter(envelope));
                UILOGGER.info(lines);
                if (lines.length() <= POPUP_MAX_LENGTH) {
                    POPUPLOGGER.info(lines);
                }
            } catch (SQLException ex) {
                UILOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }
    }

    private static class EnvelopeFilter implements ReadTable.ResultSetFilter {
        private Envelope envelope;
        private String geomFieldName = "";

        private EnvelopeFilter(Envelope envelope) {
            this.envelope = envelope;
        }

        @Override
        public boolean printRow(ResultSet rs) throws SQLException {
            if(geomFieldName.isEmpty()) {
                List<String> geomFields = SFSUtilities.getGeometryFields(rs);
                if(!geomFields.isEmpty()) {
                    geomFieldName = geomFields.get(0);
                }
            }
            Object geomObj = rs.getObject(geomFieldName);
            return geomObj instanceof Geometry && ((Geometry) geomObj).getEnvelopeInternal().intersects(envelope);
        }
    }
    @Override
    public String getTooltip() {
        return i18n.tr("Get feature attributes");
    }

    @Override
    public String getName() {
        return i18n.tr("Get feature attributes");
    }

    @Override
    public ImageIcon getImageIcon() {
        return OrbisGISIcon.getIcon("information");
    }
}
