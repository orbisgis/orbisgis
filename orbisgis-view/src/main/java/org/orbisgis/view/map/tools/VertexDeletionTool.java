/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import org.gdms.data.DataSource;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.geometryUtils.GeometryException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.*;
import org.orbisgis.view.map.tools.generated.VertexDeletion;

public class VertexDeletionTool extends VertexDeletion {

        @Override
        public void update(Observable o, Object arg) {
                //PlugInContext.checkTool(this);
        }

        @Override
        public void transitionTo_Standby(MapContext vc, ToolManager tm)
                throws FinishedAutomatonException, TransitionException {
        }

        @Override
        public void transitionTo_Done(MapContext mc, ToolManager tm)
                throws FinishedAutomatonException, TransitionException {
                Point2D p = tm.getLastRealMousePosition();
                ArrayList<Handler> handlers = tm.getCurrentHandlers();
                ILayer activeLayer = mc.getActiveLayer();
                DataSource sds = activeLayer.getDataSource();

                for (Handler handler : handlers) {
                        if (p.distance(handler.getPoint()) < tm.getTolerance()) {
                                try {
                                        if (p.distance(handler.getPoint()) < tm.getTolerance()) {
                                                sds.setGeometry(handler.getGeometryIndex(), handler.remove());
                                                break;
                                        }
                                } catch (GeometryException e) {
                                        throw new TransitionException(e.getMessage());
                                } catch (DriverException e) {
                                        throw new TransitionException(e.getMessage());
                                }
                        }
                }
                transition(Code.INIT);
        }

        @Override
        public void transitionTo_Cancel(MapContext vc, ToolManager tm)
                throws FinishedAutomatonException, TransitionException {
        }

        @Override
        public void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm)
                throws DrawingException {
                Point2D p = tm.getLastRealMousePosition();
                ArrayList<Handler> handlers = tm.getCurrentHandlers();

                for (Handler handler : handlers) {
                        try {
                                if (p.distance(handler.getPoint()) < tm.getTolerance()) {
                                        Geometry geom = handler.remove();
                                        if (geom != null) {
                                                tm.addGeomToDraw(geom);
                                                break;
                                        }
                                }
                        } catch (GeometryException e) {
                                throw new DrawingException(
                                        I18N.tr("Cannot delete vertex"));
                        }
                }
        }

        @Override
        public void drawIn_Done(Graphics g, MapContext vc, ToolManager tm)
                throws DrawingException {
        }

        @Override
        public void drawIn_Cancel(Graphics g, MapContext vc, ToolManager tm)
                throws DrawingException {
        }

	@Override
        public boolean isEnabled(MapContext vc, ToolManager tm) {
                return ToolUtilities.activeSelectionGreaterThan(vc, 0)
                        && ToolUtilities.isActiveLayerEditable(vc) && ToolUtilities.isSelectionGreaterOrEqualsThan(vc, 1)
                        && !ToolUtilities.geometryTypeIs(vc, TypeFactory.createType(Type.POINT));
        }

	@Override
        public boolean isVisible(MapContext vc, ToolManager tm) {
                return isEnabled(vc, tm);
        }

	@Override
        public String getName() {
                return I18N.tr("Delete vertex");
        }
}
