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

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.locks.Lock;
import javax.swing.ImageIcon;

import org.h2gis.utilities.GeometryTypeCodes;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.geometryUtils.GeometryEdit;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.tool.Handler;
import org.orbisgis.mapeditor.map.tool.MultiPolygonHandler;
import org.orbisgis.mapeditor.map.tool.PolygonHandler;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;

/**
 * This tool subtract polygons in Handler by the drawing one, using {@link GeometryEdit#cutMultiPolygon}.
 */
public class CutPolygonTool extends AbstractPolygonTool {

        @Override
        public void update(Observable o, Object arg) {
                //PlugInContext.checkTool(this);
        }

        @Override
        protected void polygonDone(Polygon pol, MapContext mc, ToolManager tm) throws TransitionException {
            try {

                Map<Long, Integer> pkToRowIndex;
                try {
                    pkToRowIndex = tm.getHandlersRowId(tm.getCurrentHandlers());
                } catch (SQLException ex) {
                    throw new TransitionException(ex);
                }
                ReversibleRowSet rowSet = tm.getActiveLayerRowSet();

                List<Handler> handlers = new ArrayList<>(tm.getCurrentHandlers());
                for (Handler handler : handlers) {
                    Lock lock = rowSet.getReadLock();
                    if (lock.tryLock() && pkToRowIndex.containsKey(handler.getGeometryPK())) {
                        try {
                            if (handler instanceof MultiPolygonHandler) {
                                rowSet.absolute(pkToRowIndex.get(handler.getGeometryPK()));
                                MultiPolygon mpolygon = (MultiPolygon) rowSet.getGeometry();
                                MultiPolygon result = GeometryEdit.cutMultiPolygonWithPolygon(mpolygon, pol);
                                if (result != null) {
                                    rowSet.updateGeometry(result);
                                    rowSet.updateRow();
                                }
                            } else if (handler instanceof PolygonHandler) {
                                rowSet.absolute(pkToRowIndex.get(handler.getGeometryPK()));
                                Polygon polygon = (Polygon) rowSet.getGeometry();
                                Collection<Polygon> polygons = GeometryEdit.cutPolygonWithPolygon(polygon, pol);
                                if (polygons != null) {
                                    Object[] oldRow = new Object[rowSet.getMetaData().getColumnCount()];
                                    for(int idColumn = 0; idColumn < oldRow.length; idColumn ++) {
                                        oldRow[idColumn] = rowSet.getObject(idColumn + 1);
                                    }
                                    rowSet.deleteRow();
                                    rowSet.moveToInsertRow();
                                    for (Polygon result : polygons) {
                                        for(int idColumn = 0; idColumn < oldRow.length; idColumn ++) {
                                            rowSet.updateObject(idColumn + 1, oldRow[idColumn]);
                                        }
                                        rowSet.updateGeometry(result);
                                        rowSet.insertRow();
                                    }
                                    rowSet.moveToCurrentRow();
                                }
                            }
                        } finally {
                            lock.unlock();
                        }
                    }
                }

            } catch (SQLException e) {
                throw new TransitionException(i18n.tr("Cannot cut the polygon"), e);
            }
        }

        @Override
        public boolean isEnabled(MapContext vc, ToolManager tm) {
            try {
                return ToolUtilities.geometryTypeIs(vc, GeometryTypeCodes.POLYGON, GeometryTypeCodes.MULTIPOLYGON) &&
                        ToolUtilities.isActiveLayerEditable(vc) && ToolUtilities.isSelectionEqualsTo(vc, 1);
            } catch (SQLException ex) {
                return false;
            }
        }

        @Override
        public boolean isVisible(MapContext vc, ToolManager tm) {
                return isEnabled(vc, tm);
        }

        @Override
        public double getInitialZ(MapContext mapContext) {
                return ToolUtilities.getActiveLayerInitialZ(mapContext);
        }

        @Override
        public String getName() {
                return i18n.tr("Cut a polygon");
        }

        @Override
        public String getTooltip() {
                return i18n.tr("Select a polygon then draw another polygon to subtract the first one");
        }

        @Override
        public ImageIcon getImageIcon() {
            return MapEditorIcons.getIcon("edition/cutpolygon");
        }
}
