/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.mapeditor.map.tools;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.ReadTable;
import org.orbisgis.corejdbc.common.IntegerUnion;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.renderer.ResultSetProviderFactory;
import org.orbisgis.coremap.ui.editors.map.tool.Rectangle2DDouble;
import org.orbisgis.mapeditor.map.tool.*;
import org.orbisgis.mapeditor.map.tools.generated.Selection;
import org.orbisgis.progress.NullProgressMonitor;

import javax.swing.*;
import org.orbisgis.coremap.renderer.se.Style;

/**
 * Tool to select geometries
 * 
 * @author Fernando Gonzalez Cortes
 * @author Nicolas Fortin
 */
public abstract class AbstractSelectionTool extends Selection {
        private static final Color FILL_COLOR = new Color(255, 204, 51, 50);
        private static final Color SELECTED_COLOR = new Color(125, 100, 25);



    private Rectangle2DDouble rect = new Rectangle2DDouble();
        protected ArrayList<Handler> selected = new ArrayList<>();

        @Override
        public void transitionTo_Standby(MapContext vc, ToolManager tm)
                throws TransitionException {
                if (ToolUtilities.activeSelectionGreaterThan(vc, 0)) {
                        setStatus(Status.SELECTION);
                }
        }

    /**
     * @param mc
     * @param tm
     * @throws TransitionException
     * @throws FinishedAutomatonException
     */
    @Override
    public void transitionTo_OnePoint(MapContext mc, ToolManager tm)
            throws TransitionException, FinishedAutomatonException {
        transition(Code.NO_SELECTION);
        rect.setRect(tm.getValues()[0], tm.getValues()[1], 0, 0);
    }

        protected abstract ILayer getLayer(MapContext mc);

    
        @Override
        public void transitionTo_OnePointLeft(MapContext vc, ToolManager tm)
                throws TransitionException {
        }

        @Override
        public void transitionTo_TwoPoints(MapContext mc, ToolManager tm)
                throws TransitionException, FinishedAutomatonException {           
            boolean intersects = true;
            if (rect.getMinX() < tm.getValues()[0]) {
                intersects = false;
            }
            rect.add(tm.getValues()[0], tm.getValues()[1]);
            Geometry selectionRect = rect.getEnvelope(ToolManager.toolsGeometryFactory);
            for (ILayer iLayer : getAvailableLayers(mc)) {                
                SelectionWorker selectionWorker = new SelectionWorker(this,selectionRect, mc, tm,
                    (tm.getMouseModifiers() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK,intersects, iLayer);
            selectionWorker.execute();
            }
        }

        @Override
        public void transitionTo_Selection(MapContext vc, ToolManager tm)
                throws TransitionException {
                rect = new Rectangle2DDouble();
        }

    
        @Override
        public void transitionTo_PointWithSelection(MapContext mc, ToolManager tm)
                throws TransitionException, FinishedAutomatonException {
                Point2D p = new Point2D.Double(tm.getValues()[0], tm.getValues()[1]);

                BitSet geom = new BitSet();
                ArrayList<Handler> handlers = tm.getCurrentHandlers();
                selected.clear();
                for (Handler handler : handlers) {
                            /*
                             * Don't select two handlers from the same geometry
                             */
                    if (geom.get(handler.getGeometryIndex())) {
                        continue;
                    }

                    if (p.distance(handler.getPoint()) < tm.getTolerance()) {
                        try{
                            if (!ToolUtilities.isActiveLayerEditable(mc)) {
                                throw new TransitionException(
                                        i18n.tr("Cannot modify the theme"));
                            }
                        } catch (SQLException ex) {
                            throw new TransitionException(ex.getLocalizedMessage(), ex);
                        }
                        selected.add(handler);
                        geom.set(handler.getGeometryIndex());
                    }
                }

                if (selected.isEmpty()) {
                        transition(Code.OUT_HANDLER);
                        rect.setRect(tm.getValues()[0], tm.getValues()[1], 0, 0);
                } else {
                        transition(Code.IN_HANDLER);
                }
        }

        @Override
        public void transitionTo_Movement(MapContext vc, ToolManager tm)
                throws TransitionException {
        }

        @Override
        public void transitionTo_MakeMove(MapContext mc, ToolManager tm)
                throws TransitionException, FinishedAutomatonException {
            for (Handler handler : selected) {
                Geometry g;
                try {
                    g = handler.moveTo(tm.getValues()[0], tm.getValues()[1]);
                } catch (CannotChangeGeometryException e1) {
                    throw new TransitionException(e1);
                }
            }
		    transition(Code.EMPTY);
	}

        @Override
        public void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm)
                throws DrawingException {
        }

        @Override
        public void drawIn_OnePoint(Graphics g, MapContext vc, ToolManager tm) {
        }

        @Override
        public void drawIn_OnePointLeft(Graphics g, MapContext vc, ToolManager tm) {
                Point p = tm.getMapTransform().fromMapPoint(
                        new Point2D.Double(rect.getX(), rect.getY()));
                int minx = Math.min(p.x, tm.getLastMouseX());
                int miny = Math.min(p.y, tm.getLastMouseY());
                int width = Math.abs(p.x - tm.getLastMouseX());
                int height = Math.abs(p.y - tm.getLastMouseY());
                Color fillColor = FILL_COLOR;
                if (tm.getLastMouseX() < p.x) {
			((Graphics2D) g).setStroke(new BasicStroke(1,
					BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10,
					new float[] { 10, 3 }, 0));
                } else {
                        ((Graphics2D) g).setStroke(new BasicStroke());
                }
                Rectangle2DDouble shape = new Rectangle2DDouble(minx, miny, width,
                        height);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(fillColor);
                g2.fill(shape);
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g2.setColor(SELECTED_COLOR);
                g2.draw(shape);
        }

        @Override
        public void drawIn_TwoPoints(Graphics g, MapContext vc, ToolManager tm) {
        }

        @Override
        public void drawIn_Selection(Graphics g, MapContext vc, ToolManager tm) {
        }

        @Override
        public void drawIn_PointWithSelection(Graphics g, MapContext vc,
                ToolManager tm) {
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
                                i18n.tr("Cannot move {0}",e.getMessage()));
                }
        }

        @Override
        public void drawIn_MakeMove(Graphics g, MapContext vc, ToolManager tm) {
        }

        private static class SelectionWorker extends SwingWorker<Set<Integer>, Set<Integer>> {
            AbstractSelectionTool automaton;
            Geometry selectionRect;
            MapContext mc;
            ToolManager tm;
            boolean controlDown;
            boolean intersects;
            ILayer activeLayer;

            private SelectionWorker(AbstractSelectionTool automaton, Geometry selectionRect,
                                    MapContext mc, ToolManager tm, boolean controlDown,
                                    boolean intersects, ILayer activeLayer) {
                this.automaton = automaton;
                this.selectionRect = selectionRect;
                this.mc = mc;
                this.tm = tm;
                this.controlDown = controlDown;
                this.intersects = intersects;
                this.activeLayer = activeLayer;
            }

            @Override
            protected Set<Integer> doInBackground() throws Exception {
                Set<Integer> newSelection;
                if(tm.getCachedResultSetContainer().getIndexMap(activeLayer) == null) {
                    // Get all primary value where default geometry intersects a bounding box
                    Map<Object, Integer> keyToRowId;
                    try (Connection connection = SFSUtilities.wrapConnection(activeLayer.getDataManager().getDataSource().getConnection())) {
                            String loadedTable = activeLayer.getTableReference();
                            String pkName = MetaData.getPkName(connection, loadedTable, false);
                            if (!pkName.isEmpty()) {
                                keyToRowId = MetaData.primaryKeyToRowId(connection, loadedTable, pkName);
                            } else {
                                keyToRowId = new HashMap<>();
                            }
                        String geomFieldName = SFSUtilities.getGeometryFields(connection,
                                TableLocation.parse(activeLayer.getTableReference())).get(0);
                        newSelection = ReadTable.getTableRowIdByEnvelope(mc.getDataManager(),
                                activeLayer.getTableReference(), geomFieldName, selectionRect, !intersects, keyToRowId);

                    } catch (SQLException e) {
                        automaton.transition(Code.NO_SELECTION);
                        throw new TransitionException(e);
                    }
                } else {
                    // Index query available
                    try(ResultSetProviderFactory.ResultSetProvider resultSetProvider = tm.getCachedResultSetContainer()
                            .getResultSetProvider(activeLayer, new NullProgressMonitor());
                        SpatialResultSet rs =  resultSetProvider.execute(new NullProgressMonitor(), selectionRect.getEnvelopeInternal())) {
                        newSelection = new HashSet<>();
                        while (rs.next()) {
                            if((intersects && selectionRect.intersects(rs.getGeometry())) ||
                                    (!intersects && selectionRect.contains(rs.getGeometry()))) {
                                newSelection.add(rs.getRow());
                            }
                        }
                    } catch (SQLException ex) {
                        automaton.transition(Code.NO_SELECTION);
                        throw new TransitionException(ex);
                    }
                }
                return newSelection;
            }

            @Override
            protected void done() {
                try {
                    if (controlDown) {
                        IntegerUnion newSel = new IntegerUnion(activeLayer.getSelection());
                        for (int el : get()) {
                            if (!newSel.remove(el)) {
                                newSel.add(el);
                            }
                        }
                        activeLayer.setSelection(newSel);
                    } else {
                        activeLayer.setSelection(get());
                    }
                    if (activeLayer.getSelection().isEmpty()) {
                        automaton.transition(Code.NO_SELECTION);
                    } else {
                        automaton.transition(Code.SELECTION);
                    }
                } catch (Exception ex) {
                    try {
                        automaton.transition(Code.NO_SELECTION);
                    } catch (TransitionException | FinishedAutomatonException e) {
                        // Ignore
                    }
                    automaton.logger.error(ex.getLocalizedMessage(), ex);
                }
            }
        }
    
    /**
     * Retrieves all layers that are selected, visible and have reference to
     * a table.
     * @param mapContext
     * @return 
     */    
    public ILayer[] getAvailableLayers(MapContext mapContext) {
        Set<ILayer> availableLayers = new HashSet<ILayer>();
        if(mapContext!=null){
        for (ILayer layer : mapContext.getSelectedLayers()) {
            if (layer.isVisible() && !layer.getTableReference().isEmpty()) {
                availableLayers.add(layer);
            }
        }
        for (Style style : mapContext.getSelectedStyles()) {
            ILayer layer = style.getLayer();
            if (layer.isVisible() && !layer.getTableReference().isEmpty()) {
                availableLayers.add(layer);
            }
        }
        }
        return availableLayers.toArray(new ILayer[availableLayers.size()]);
        
    }
}
