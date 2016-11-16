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
 * Copyright (C) 2015-2016 CNRS (Lab-STICC UMR CNRS 6285)
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import com.vividsolutions.jts.geom.Geometry;
import java.util.Set;
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
import org.orbisgis.sif.edition.EditorManager;
import org.orbisgis.tableeditorapi.TableEditableElementImpl;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Show selected geometry information.
 */
public class InfoTool extends AbstractRectangleTool {

    private static Logger UILOGGER = LoggerFactory.getLogger("gui." + InfoTool.class);
    private static Logger POPUPLOGGER = LoggerFactory.getLogger("popup." + InfoTool.class);
    private final EditorManager editorManager;
       
    public InfoTool(EditorManager editorManager){
        this.editorManager=editorManager;
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
        new PopulateViewJob(new Envelope(minx, maxx, miny, maxy), layer, intersects, editorManager).execute();

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
        private final boolean intersects;
        private final EditorManager editorManager;

        private PopulateViewJob(Envelope envelope, ILayer layer, boolean intersects, EditorManager editorManager) {
            this.envelope = envelope;
            this.layer = layer;
            this.intersects=intersects;
            this.editorManager=editorManager;
        }

        @Override
        public String toString() {
            return I18N.tr("Open table with selected features");
        }

        @Override
        protected Object doInBackground() throws Exception {            
            Geometry envGeom = ToolManager.toolsGeometryFactory.toGeometry(envelope);
            TableLocation tableLocation = TableLocation.parse(layer.getTableReference());
            try(Connection connection = layer.getDataManager().getDataSource().getConnection()) {
                List<String> geomFields = SFSUtilities.getGeometryFields(connection, tableLocation);
                if(geomFields.isEmpty()) {
                    return null;
                }
                Set<Long> newSelection = ReadTable.getTablePkByEnvelope(layer.getDataManager(),
                        layer.getTableReference(), geomFields.get(0), envGeom, !intersects);
                if (newSelection.size() > 0) {
                    layer.setSelection(newSelection);
                    TableEditableElementImpl tabe = new TableEditableElementImpl(newSelection, layer.getTableReference(), layer.getDataManager());
                    tabe.setFiltered(true);
                    editorManager.openEditable(tabe);
                }                
                
            } catch (SQLException ex) {
                UILOGGER.error(ex.getLocalizedMessage(), ex);
            }
            return null;
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
