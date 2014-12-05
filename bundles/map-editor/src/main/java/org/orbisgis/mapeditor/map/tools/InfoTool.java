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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.apache.log4j.Logger;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.ReadTable;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
        new PopulateViewJob(new Envelope(minx, maxx, miny, maxy), layer).execute();

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
    private static class PopulateViewJob extends SwingWorker {

        private final Envelope envelope;
        private final ILayer layer;
        private static final I18n I18N = I18nFactory.getI18n(PopulateViewJob.class);

        private PopulateViewJob(Envelope envelope, ILayer layer) {
            this.envelope = envelope;
            this.layer = layer;
        }

        @Override
        public String toString() {
            return I18N.tr("Fetch area information");
        }

        @Override
        protected Object doInBackground() throws Exception {
            GeometryFactory geometryFactory = new GeometryFactory();
            Geometry envGeom = geometryFactory.toGeometry(envelope);
            TableLocation tableLocation = TableLocation.parse(layer.getTableReference());
            try(Connection connection = layer.getDataManager().getDataSource().getConnection()) {
                // Fetch SRID for PostGIS constraints
                try(PreparedStatement pst = SFSUtilities.prepareInformationSchemaStatement(connection,
                        tableLocation.getCatalog(), tableLocation.getSchema(), tableLocation.getTable(),
                        "PUBLIC.GEOMETRY_COLUMNS", "");
                    ResultSet rs = pst.executeQuery()) {
                    if(rs.next()) {
                        int srid = rs.getInt("srid");
                        if(srid > 0) {
                            envGeom.setSRID(srid);
                        }
                    }
                }
                List<String> geomFields = SFSUtilities.getGeometryFields(connection, tableLocation);
                if(geomFields.isEmpty()) {
                    return null;
                }
                try(PreparedStatement pst = connection.prepareStatement("SELECT * FROM "+layer.getTableReference()+
                        " WHERE "+TableLocation.quoteIdentifier(geomFields.get(0))+" && ?")) {
                    pst.setObject(1, envGeom);
                    try(ResultSet rs = pst.executeQuery()) {
                        String lines = ReadTable.resultSetToString(rs, MAX_FIELD_LENGTH, MAX_PRINTED_ROWS, false, false,
                                new EnvelopeFilter(envelope));
                        UILOGGER.info(lines);
                        if (lines.length() <= POPUP_MAX_LENGTH) {
                            POPUPLOGGER.info(lines);
                        }
                    }
                }
            } catch (SQLException ex) {
                UILOGGER.error(ex.getLocalizedMessage(), ex);
            }
            return null;
        }
    }

    private static class EnvelopeFilter implements ReadTable.ResultSetFilter {
        private Geometry envelope;
        private String geomFieldName = "";

        private EnvelopeFilter(Envelope envelope) {
            GeometryFactory factory = new GeometryFactory();
            this.envelope = factory.toGeometry(envelope);
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
            return geomObj instanceof Geometry && ((Geometry) geomObj).intersects(envelope);
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
        return MapEditorIcons.getIcon("information");
    }
}
