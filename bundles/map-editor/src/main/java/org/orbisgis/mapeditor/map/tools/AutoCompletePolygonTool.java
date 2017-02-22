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
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import javax.swing.ImageIcon;

import org.h2gis.utilities.GeometryTypeCodes;
import org.h2gis.utilities.SpatialResultSet;
import org.orbisgis.commons.progress.NullProgressMonitor;
import org.orbisgis.corejdbc.ReadTable;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.tool.Handler;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class permits to complete a polygon based on the geometry difference.
 * @author Erwan Bocher
 */
public class AutoCompletePolygonTool extends AbstractPolygonTool {

        private static final Logger LOGGER = LoggerFactory.getLogger(AutoCompletePolygonTool.class);

        @Override
        protected void polygonDone(Polygon pol,
                MapContext mc, ToolManager tm) throws TransitionException {
                ReversibleRowSet rowSet = tm.getActiveLayerRowSet();
                Lock lock = rowSet.getReadLock();
                if(lock.tryLock()) {
                    try {
                        List<Handler> handlers = tm.getCurrentHandlers();
                        Geometry geom = pol;

                        //Compute difference
                        SortedSet<Long> selectedGeometries = new TreeSet<>();
                        for (Handler handler : handlers) {
                            selectedGeometries.add(handler.getGeometryPK());
                        }
                        try (Connection connection = mc.getDataManager().getDataSource().getConnection();
                             ReadTable.FilteredResultSet filteredResultSet = new ReadTable.FilteredResultSet(connection, mc.getActiveLayer().getTableReference(), selectedGeometries, new NullProgressMonitor(), true, "")) {

                            SpatialResultSet rs = filteredResultSet.getResultSet();
                            while (rs.next()) {
                                geom = geometryDifference(geom, rs.getGeometry());
                            }
                        } catch (IOException ex) {
                            throw new SQLException(ex.getLocalizedMessage(), ex);
                        }
                        try {
                            if (ToolUtilities.geometryTypeIs(mc, GeometryTypeCodes.POLYGON)) {
                                rowSet.moveToInsertRow();
                                for (int i = 0; i < geom.getNumGeometries(); i++) {
                                    rowSet.updateGeometry(geom.getGeometryN(i));
                                    rowSet.insertRow();
                                }
                            } else if (ToolUtilities.geometryTypeIs(mc, GeometryTypeCodes.MULTIPOLYGON, GeometryTypeCodes.GEOMETRY)) {
                                rowSet.moveToInsertRow();
                                if (geom instanceof Polygon) {
                                    Polygon polygon = (Polygon) geom;
                                    geom = geom.getFactory().createMultiPolygon(new Polygon[]{polygon});
                                    rowSet.updateGeometry(geom);
                                    ToolUtilities.populateNotNullFields(mc.getDataManager().getDataSource(), rowSet.getTable(), rowSet);
                                    rowSet.insertRow();
                                } else if (geom instanceof MultiPolygon) {
                                    for (int i = 0; i < geom.getNumGeometries(); i++) {
                                        Polygon polygon = (Polygon) geom.getGeometryN(i);
                                        geom = geom.getFactory().createMultiPolygon(new Polygon[]{polygon});
                                        rowSet.updateGeometry(geom);
                                        ToolUtilities.populateNotNullFields(mc.getDataManager().getDataSource(), rowSet.getTable(), rowSet);
                                        rowSet.insertRow();
                                    }
                                }
                            }
                        } finally {
                            rowSet.moveToCurrentRow();
                        }
                    } catch (SQLException e) {
                        throw new TransitionException(i18n.tr("Cannot Autocomplete the polygon"), e);
                    } finally {
                        lock.unlock();
                    }
                }
        }

        /**
         * Compute the difference between two geometry A and B
         * @param geometryA
         * @param geometryB
         * @return geometry
         * @throws java.sql.SQLException
         */
        private Geometry computeGeometryDifference(Geometry geometryA, Geometry geometryB) throws SQLException {
                if (geometryA.intersects(geometryB)) {
                        Geometry newGeomDiff = geometryB.difference(geometryA);
                        if (newGeomDiff.isValid()) {
                                geometryB = newGeomDiff;

                        }
                }
                return geometryB;


        }

        private Geometry geometryDifference(Geometry drawingGeom, Geometry geom) throws SQLException {
            Geometry result = drawingGeom;
            if (geom instanceof MultiPolygon) {
                for (int i = 0; i < geom.getNumGeometries(); i++) {
                        result = computeGeometryDifference(geom.getGeometryN(i), result);
                }
            } else if (geom instanceof Polygon) {
                result = computeGeometryDifference(geom, result);
            }
            return result;
        }

        @Override
        public boolean isEnabled(MapContext vc, ToolManager tm) {
            try {
                return ToolUtilities.geometryTypeIs(vc, GeometryTypeCodes.POLYGON,
                        GeometryTypeCodes.MULTIPOLYGON, GeometryTypeCodes.GEOMETRY, GeometryTypeCodes.GEOMCOLLECTION)
                     && ToolUtilities.isActiveLayerEditable(vc) && ToolUtilities.isSelectionGreaterOrEqualsThan(vc, 1);
            } catch (SQLException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
                return false;
            }
        }

        @Override
        public boolean isVisible(MapContext vc, ToolManager tm) {
                return isEnabled(vc, tm);


        }

        @Override
        public double getInitialZ(MapContext mapContext) throws TransitionException {
                try(Connection connection = mapContext.getDataManager().getDataSource().getConnection()) {
                    return ToolUtilities.getActiveLayerInitialZ(connection, mapContext);
                } catch (SQLException ex) {
                    throw new TransitionException(ex.getLocalizedMessage(), ex);
                }
        }

        @Override
        public String getName() {
                return i18n.tr("Autocomplete a polygon");

        }

        @Override
        public String getTooltip() {
            return getName();
        }

        @Override
        public ImageIcon getImageIcon() {
            return MapEditorIcons.getIcon("edition/autocompletepolygon");
        }

        @Override
        public void update(Observable o, Object o1) {
        
    }
}
