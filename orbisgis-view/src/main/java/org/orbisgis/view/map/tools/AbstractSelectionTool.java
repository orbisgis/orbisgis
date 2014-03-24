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
package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import org.gdms.data.DataSource;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.driver.DriverException;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.Rectangle2DDouble;
import org.orbisgis.view.map.tool.*;
import org.orbisgis.view.map.tools.generated.Selection;

/**
 * Tool to select geometries
 * 
 * @author Fernando Gonzalez Cortes
 */
public abstract class AbstractSelectionTool extends Selection {
        private static final Color FILL_COLOR = new Color(255, 204, 51, 50);
        private static final Color SELECTED_COLOR = new Color(255, 204, 51);

        private Rectangle2DDouble rect = new Rectangle2DDouble();
        protected ArrayList<Handler> selected = new ArrayList<Handler>();

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
                Rectangle2DDouble p = new Rectangle2DDouble(tm.getValues()[0]
                        - tm.getTolerance() / 2, tm.getValues()[1] - tm.getTolerance()
                        / 2, tm.getTolerance(), tm.getTolerance());

                Geometry selectionRect = p.getEnvelope(ToolManager.toolsGeometryFactory);

                ILayer activeLayer = getLayer(mc);
                DataSource ds = activeLayer.getDataSource();
                try {
                        Iterator<Integer> l = queryLayer(activeLayer.getDataSource(), p);
                        while (l.hasNext()) {
                                int rowIndex = l.next();
                                Geometry g = ds.getGeometry(rowIndex);
                                if (g != null) {
                                        if (g.intersects(selectionRect)) {
                                                if ((tm.getMouseModifiers() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
                                                        IntegerUnion newSelection = new IntegerUnion(activeLayer.getSelection());
                                                        if(!newSelection.remove(rowIndex)) {
                                                                newSelection.add(rowIndex);
                                                        }
                                                        activeLayer.setSelection(newSelection);
                                                        if (!newSelection.isEmpty()) {
                                                                transition(Code.SELECTION);
                                                        } else {
                                                                transition(Code.INIT);
                                                        }

                                                        return;
                                                } else {
                                                        IntegerUnion newSelection =  new IntegerUnion(rowIndex);
                                                        activeLayer.setSelection(newSelection);
                                                        transition(Code.SELECTION);
                                                        return;
                                                }
                                        }
                                }
                        }

                        transition(Code.NO_SELECTION);
                        rect.setRect(tm.getValues()[0], tm.getValues()[1], 0, 0);
                } catch (DriverException e) {
                        transition(Code.NO_SELECTION);
                        throw new TransitionException(e);
                }
        }

        protected abstract ILayer getLayer(MapContext mc);

        private Iterator<Integer> queryLayer(DataSource ds,
                Rectangle2DDouble rect) throws DriverException {
                String geomFieldName = ds.getMetadata().getFieldName(
                        ds.getSpatialFieldIndex());
                Envelope env = new Envelope(rect.getMinX(), rect.getMaxX(), rect.getMinY(), rect.getMaxY());

            return ds.queryIndex(new DefaultSpatialIndexQuery(geomFieldName,
                    env));
        }

    
        @Override
        public void transitionTo_OnePointLeft(MapContext vc, ToolManager tm)
                throws TransitionException {
        }
    
        @Override
        public void transitionTo_TwoPoints(MapContext mc, ToolManager tm)
                throws TransitionException, FinishedAutomatonException {
                ILayer activeLayer = getLayer(mc);
                boolean intersects = true;
                if (rect.getMinX() < tm.getValues()[0]) {
                        intersects = false;
                }
                rect.add(tm.getValues()[0], tm.getValues()[1]);

                Geometry selectionRect = rect.getEnvelope(ToolManager.toolsGeometryFactory);

                DataSource ds = activeLayer.getDataSource();
                try {
                        IntegerUnion newSelection = new IntegerUnion();
                        Iterator<Integer> l = queryLayer(ds, rect);
                        while (l.hasNext()) {
                                int index = l.next();
                                Geometry g = ds.getGeometry(index);
                                if (g != null) {
                                        if (intersects) {
                                                if (g.intersects(selectionRect)) {
                                                        newSelection.add(index);
                                                }
                                        } else {
                                                if (selectionRect.contains(g)) {
                                                        newSelection.add(index);
                                                }
                                        }
                                }
                        }

                        if ((tm.getMouseModifiers() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
                                IntegerUnion newSel = new IntegerUnion(activeLayer.getSelection());
                                for(int el : newSelection) {
                                        if(!newSel.remove(el)) {
                                                newSel.add(el);
                                        }
                                }                                
                                activeLayer.setSelection(newSel);
                        } else {
                                activeLayer.setSelection(newSelection);
                        }
                        if (activeLayer.getSelection().isEmpty()) {
                                transition(Code.NO_SELECTION);
                        } else {
                                transition(Code.SELECTION);
                        }
                } catch (DriverException e) {
                        transition(Code.NO_SELECTION);
                        throw new TransitionException(e);
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
                        if (!ToolUtilities.isActiveLayerEditable(mc)) {
                            throw new TransitionException(
                                    i18n.tr("Cannot modify the theme"));
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
		DataSource ds = getLayer(mc).getDataSource();
            for (Handler handler : selected) {
                Geometry g;
                try {
                    g = handler.moveTo(tm.getValues()[0], tm.getValues()[1]);
                } catch (CannotChangeGeometryException e1) {
                    throw new TransitionException(e1);
                }

                try {
                    ds.setGeometry(handler.getGeometryIndex(), g);
                } catch (DriverException e) {
                    throw new TransitionException(e);
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
}
