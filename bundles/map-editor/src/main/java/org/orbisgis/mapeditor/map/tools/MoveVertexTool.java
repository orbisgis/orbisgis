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

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import javax.swing.ImageIcon;

import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.tool.CannotChangeGeometryException;
import org.orbisgis.mapeditor.map.tool.DrawingException;
import org.orbisgis.mapeditor.map.tool.FinishedAutomatonException;
import org.orbisgis.mapeditor.map.tool.Handler;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;

/**
 * A tool to move vertex of the edited layer.
 */
public class MoveVertexTool extends AbstractSelectionTool {

        @Override
        public void update(Observable o, Object arg) {
                //PlugInContext.checkTool(this);
        }

        @Override
        public boolean isEnabled(MapContext vc, ToolManager tm) {
            try {
                return ToolUtilities.isActiveLayerEditable(vc)
                        && ToolUtilities.isActiveLayerVisible(vc) && ToolUtilities.isSelectionGreaterOrEqualsThan(vc,1);
            } catch (SQLException ex) {
                return false;
            }
        }

        @Override
        public boolean isVisible(MapContext vc, ToolManager tm) {
                return isEnabled(vc, tm);
        }

        @Override
        protected ILayer getLayer(MapContext mc) {
                return mc.getActiveLayer();
        }

        @Override
        public void transitionTo_OnePoint(MapContext mc, ToolManager tm) {
        }

        @Override
        public void transitionTo_TwoPoints(MapContext mc, ToolManager tm){
                
        }

    @Override
    public void transitionTo_MakeMove(MapContext mc, ToolManager tm) throws TransitionException,
            FinishedAutomatonException {

        // Compute ROW index of selected entities
        Map<Long, Integer> pkToRowIndex;
        try {
            pkToRowIndex = tm.getHandlersRowId(selected);
        } catch (SQLException ex) {
            throw new FinishedAutomatonException(ex);
        }
        // Record moving of points
        ReversibleRowSet rowSet = tm.getActiveLayerRowSet();
        for (Handler handler : selected) {
            Geometry g;
            try {
                g = handler.moveTo(tm.getValues()[0], tm.getValues()[1]);
            } catch (CannotChangeGeometryException e1) {
                throw new TransitionException(e1);
            }

            Lock lock = rowSet.getReadLock();
            if (lock.tryLock() && pkToRowIndex.containsKey(handler.getGeometryPK())) {
                try {
                    rowSet.absolute(pkToRowIndex.get(handler.getGeometryPK()));
                    rowSet.updateGeometry(g);
                    rowSet.updateRow();
                } catch (SQLException e) {
                    throw new TransitionException(i18n.tr("Cannot move point(s)"), e);
                } finally {
                    lock.unlock();
                }
            }
        }
        transition(Code.EMPTY);
    }

        @Override
        public void drawIn_Movement(Graphics g, MapContext vc, ToolManager tm)
                throws DrawingException {
                Point2D p = tm.getLastRealMousePosition();
                try {
                    for (Handler handler : selected) {
                        Geometry geom = handler.moveTo(p.getX(), p.getY());
                        tm.addGeomToDraw(geom);
                    }
                } catch (CannotChangeGeometryException e) {
                        throw new DrawingException(
                                i18n.tr("Cannot update the geometry {0}",e.getMessage()));
                }
        }

        @Override
        public String getName() {
                return i18n.tr("Move vertex");
        }

        @Override
        public String getTooltip() {
            return i18n.tr("Move vertex");
        }

        @Override
        public ImageIcon getImageIcon() {
            return MapEditorIcons.getIcon("edition/movevertex");
        }
}
