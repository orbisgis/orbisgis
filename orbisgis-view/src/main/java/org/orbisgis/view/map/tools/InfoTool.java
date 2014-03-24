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
package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.io.WKTWriter;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Observable;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.DefaultJobId;
import org.orbisgis.view.edition.EditableElementException;
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

        try {
            double minx = rect.getMinX();
            double miny = rect.getMinY();
            double maxx = rect.getMaxX();
            double maxy = rect.getMaxY();
            BackgroundManager bm = Services.getService(BackgroundManager.class);
            bm.backgroundOperation(new DefaultJobId(
                    "org.orbisgis.jobs.InfoTool"), new PopulateViewJob(new Envelope(minx, maxx, miny, maxy), sds));
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

    /**
     * This class is used to open the selected features in a table component. If
     * the table is closed a new table is openned. The selected feature replace
     * the current selection if there is one.
     */
    private class PopulateViewJob implements BackgroundJob {

        private final Envelope envelope;
        private final DataSource sds;

        private PopulateViewJob(Envelope envelope, DataSource sds) {
            this.envelope = envelope;
            this.sds = sds;
        }

        @Override
        public String getTaskName() {
            return "Getting info";
        }

        @Override
        public void run(ProgressMonitor pm) {
            try {
                UILOGGER.debug("Info query: " + envelope.toString());
                if (!pm.isCancelled()) {
                    DataManager dm = Services.getService(DataManager.class);
                    DataSourceFactory dsf = dm.getDataSourceFactory();
                    int geomFieldindex = MetadataUtilities.getGeometryFieldIndex(sds.getMetadata());
                    String geomField = sds.getMetadata().getFieldName(geomFieldindex);
                    if (!dsf.getIndexManager().isIndexed(sds, geomField)) {
                        dsf.getIndexManager().buildIndex(sds, geomField, pm);
                    }
                    DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery(geomField, envelope);
                    Iterator<Integer> iterator = sds.queryIndex(dsf, query);
                    if (iterator.hasNext()) {
                        IntegerUnion newSel = new IntegerUnion();
                        newSel.addAll(IteratorUtils.toList(iterator));
                        TableEditableElement tableOpenned = getOpennedTable(sds.getName());
                        if (tableOpenned == null) {
                            tableOpenned = new TableEditableElement(sds);
                            EditorManager em = Services.getService(EditorManager.class);
                            em.openEditable(tableOpenned);
                        }
                        tableOpenned.setSelection(newSel);
                    }
                }
            } catch (DriverLoadException e) {
                UILOGGER.error("Cannot execute the query", e);
            } catch (DriverException e) {
                UILOGGER.error("Cannot obtain the geometry field", e);
            } catch (NoSuchTableException e) {
                UILOGGER.error("Cannot obtain the data", e);
            } catch (IndexException e) {
                UILOGGER.error("Cannot build the spatial index", e);
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
