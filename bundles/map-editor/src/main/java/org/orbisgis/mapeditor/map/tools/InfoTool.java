/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.mapeditor.map.tools;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.geom.Rectangle2D;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.sif.edition.Editor;
import org.orbisgis.sif.edition.EditorManager;
import org.orbisgis.tableeditorapi.TableEditableElement;
import org.orbisgis.tableeditorapi.TableEditableElementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public static final String POPULATE_VIEW_JOB="POPULATE_VIEW_JOB";
    public static final String JOB_END="JOB_END";

    private static Logger LOGGER = LoggerFactory.getLogger(InfoTool.class);
    private final EditorManager editorManager;
    private static I18n I18N = I18nFactory.getI18n(InfoTool.class);
    private ExecutorService executorService;
    private SwingWorkerPM runningJob;


    public InfoTool(EditorManager editorManager, ExecutorService executorService){
        this.editorManager=editorManager;
        this.executorService = executorService;
    }

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
        boolean intersects = true;
        if (minx < tm.getValues()[0]) {
            intersects = false;
        }
        SwingWorkerPM sw = new PopulateViewJob(new Envelope(minx, maxx, miny, maxy), layer, intersects, editorManager);
        sw.addPropertyChangeListener(EventHandler.create(PropertyChangeListener.class, this, "onPropertyChange", ""));
        if(runningJob != null){
            runningJob.cancel();
        }
        if(executorService == null){
            sw.execute();
        }
        else{
            executorService.execute(sw);
        }
        runningJob = sw;
    }

    public void onPropertyChange(PropertyChangeEvent event){
        if(event.getPropertyName().equals(POPULATE_VIEW_JOB)){
            if(event.getNewValue().equals(JOB_END)){
                runningJob = null;
            }
        }
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
    private static final class PopulateViewJob extends SwingWorkerPM<Object, Object> {

        private final Envelope envelope;
        private final ILayer layer;
        private static final I18n I18N = I18nFactory.getI18n(PopulateViewJob.class);
        private final boolean intersects;
        private final EditorManager editorManager;
        private Set<Long> newSelection = new HashSet<>();

        private PopulateViewJob(Envelope envelope, ILayer layer, boolean intersects, EditorManager editorManager) {
            this.envelope = envelope;
            this.layer = layer;
            this.intersects = intersects;
            this.editorManager = editorManager;
            getProgressMonitor().setTaskName(toString());
        }

        @Override
        public String toString() {
            return I18N.tr("Fetch area information");
        }

        @Override
        protected Object doInBackground() {
            Geometry envGeom = ToolManager.toolsGeometryFactory.toGeometry(envelope);
            TableLocation tableLocation = TableLocation.parse(layer.getTableReference());
            try (Connection connection = layer.getDataManager().getDataSource().getConnection()) {
                List<String> geomFields = SFSUtilities.getGeometryFields(connection, tableLocation);
                if (geomFields.isEmpty()) {
                    return null;
                }
                newSelection = ReadTable.getTablePkByEnvelope(layer.getDataManager(),
                        layer.getTableReference(), geomFields.get(0), envGeom, !intersects);
            } catch (SQLException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
            return null;
        }

        @Override
        protected void done() {
            // Swing thread
            if (newSelection.size() > 0) {
                // Check if the table editor is not already open
                boolean tableEditorAlreadyThere = false;
                for(Editor editor : editorManager.getEditors()) {
                    if(editor.getEditableElement() instanceof TableEditableElement) {
                        TableEditableElement editableElement = (TableEditableElement)editor.getEditableElement();
                        if(TableLocation.parse(editableElement.getTableReference()).equals(
                                TableLocation.parse(layer.getTableReference()))) {
                            // This editor use the same layer
                            tableEditorAlreadyThere = true;
                            if(!editableElement.isFiltered()) {
                                // No filter currently
                                editableElement.setSelection(newSelection);
                                editableElement.setFiltered(true);
                            } else {
                                // There is already a filter
                                // we clear the filter for now
                                // then we will apply the filter later
                                editableElement.setFiltered(false);
                                new PopulateViewJob(envelope, layer, intersects, editorManager).execute();
                            }
                            break;
                        }
                    }
                }
                if(!tableEditorAlreadyThere) {
                    TableEditableElementImpl editableElement = new TableEditableElementImpl(newSelection, layer.getTableReference(), layer.getDataManager());
                    editableElement.setSelection(newSelection);
                    layer.setSelection(newSelection);
                    editableElement.setFiltered(true);
                    editorManager.openEditable(editableElement);
                }
            }
        }
    }


    @Override
    public String getTooltip() {
        return I18N.tr("Get feature attributes");
    }

    @Override
    public String getName() {
        return I18N.tr("Get feature attributes");
    }

    @Override
    public ImageIcon getImageIcon() {
        return MapEditorIcons.getIcon("information");
    }
}
