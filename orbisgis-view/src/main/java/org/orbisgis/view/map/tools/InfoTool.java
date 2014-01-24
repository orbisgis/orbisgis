/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Observable;
import javax.sql.DataSource;
import javax.swing.ImageIcon;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.Services;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.core.jdbc.MetaData;
import org.orbisgis.core.jdbc.ReadTable;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.DefaultJobId;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.edition.EditorManager;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;
import org.orbisgis.view.table.TableEditableElement;
import org.orbisgis.view.table.TableEditor;

/**
 * Show selected geometry information.
 */
public class InfoTool extends AbstractRectangleTool {

    private static Logger UILOGGER = Logger.getLogger("gui." + InfoTool.class);
    private static Logger POPUPLOGGER = Logger.getLogger("popup." + InfoTool.class);
    private static final int MAX_PRINTED_ROWS = 100;
    private static final int MAX_FIELD_LENGTH = 10;
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
                "org.orbisgis.jobs.InfoTool"), new PopulateViewJob(new Envelope(minx, maxx, miny, maxy), vc.getDataManager().getDataSource(),layer.getTableReference()));

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
        private final String tableReference;

        private PopulateViewJob(Envelope envelope, DataSource sds, String tableReference) {
            this.envelope = envelope;
            this.sds = sds;
            this.tableReference = tableReference;
        }

        @Override
        public String getTaskName() {
            return "Getting info";
        }

        @Override
        public void run(ProgressMonitor pm) {
            GeometryFactory factory = new GeometryFactory();
            String envelopeWKT = factory.toGeometry(envelope).toText();
            try(Connection connection = SFSUtilities.wrapConnection(sds.getConnection())) {
                String geomFieldName = SFSUtilities.getGeometryFields(connection,
                        TableLocation.parse(tableReference)).get(0);
                String query = String.format("SELECT * FROM %s WHERE %s && ST_GeomFromText('%s')",tableReference,MetaData.escapeFieldName(geomFieldName),envelopeWKT);
                String lines = ReadTable.resultSetToString(query, connection.createStatement(), MAX_FIELD_LENGTH, MAX_PRINTED_ROWS, false);
                UILOGGER.info(lines);
                if(lines.length() <= POPUP_MAX_LENGTH) {
                    POPUPLOGGER.info(lines);
                }
            } catch (SQLException ex) {
                UILOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }
    }

    /**
     * Return true is the current TableEditableElement is already openned.
     *
     * @param tableEditableElement
     * @return
     */
    public TableEditableElement getOpennedTable(String sourceName) {
        EditorManager em = Services.getService(EditorManager.class);
        for (EditorDockable editor : em.getEditors()) {
            if (editor instanceof TableEditor && editor.getEditableElement().getId().equals(sourceName)) {
                return (TableEditableElement) editor.getEditableElement();
            }
        }
        return null;
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
