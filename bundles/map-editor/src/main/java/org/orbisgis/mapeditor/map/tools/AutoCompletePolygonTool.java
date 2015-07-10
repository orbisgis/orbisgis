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
package org.orbisgis.mapeditor.map.tools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import javax.sql.DataSource;
import javax.swing.ImageIcon;

import org.h2gis.utilities.GeometryTypeCodes;
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
                try {
                        List<Handler> handlers = tm.getCurrentHandlers();
                        Geometry geom = pol;
                        for (Handler handler : handlers) {
                                geom = execute(handler, geom, sds);
                        }
                        if (ToolUtilities.geometryTypeIs(mc, TypeFactory.createType(Type.POLYGON))) {
                                for (int i = 0; i < geom.getNumGeometries(); i++) {
                                        row[sds.getSpatialFieldIndex()] = ValueFactory.createValue(geom.getGeometryN(i));
                                        row = ToolUtilities.populateNotNullFields(sds, row);
                                        sds.insertFilledRow(row);
                                }
                        } else if (ToolUtilities.geometryTypeIs(
                                mc,
                                TypeFactory.createType(Type.MULTIPOLYGON),
                                TypeFactory.createType(Type.GEOMETRY,
                                ConstraintFactory.createConstraint(Constraint.DIMENSION_3D_GEOMETRY,
                                GeometryDimensionConstraint.DIMENSION_SURFACE)),
                                TypeFactory.createType(Type.GEOMETRYCOLLECTION,
                                ConstraintFactory.createConstraint(Constraint.DIMENSION_3D_GEOMETRY,
                                GeometryDimensionConstraint.DIMENSION_SURFACE)))) {
                                if (geom instanceof Polygon) {
                                        Polygon polygon = (Polygon) geom;
                                        geom = geom.getFactory().createMultiPolygon(new Polygon[]{polygon});
                                        row[sds.getSpatialFieldIndex()] = ValueFactory.createValue(geom);
                                        row = ToolUtilities.populateNotNullFields(sds, row);
                                        sds.insertFilledRow(row);
                                } else if (geom instanceof MultiPolygon) {
                                        for (int i = 0; i < geom.getNumGeometries(); i++) {
                                                Polygon polygon = (Polygon) geom.getGeometryN(i);
                                                geom = geom.getFactory().createMultiPolygon(new Polygon[]{polygon});
                                                row[sds.getSpatialFieldIndex()] = ValueFactory.createValue(geom);
                                                row = ToolUtilities.populateNotNullFields(sds, row);
                                                sds.insertFilledRow(row);
                                        }

                                }
                        }


                } catch (DriverException e) {
                        throw new TransitionException(i18n.tr("Cannot Autocomplete the polygon"), e);
                }
        }

        /**
         * Compute the difference between two geometry A and B
         * @param geometryA
         * @param geometryB
         * @return geometry
         * @throws DriverException
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

        private Geometry execute(Handler handler, Geometry drawingGeom, DataSource sds) throws SQLException {
                System.out.println(drawingGeom.toText());
                Geometry result = drawingGeom;

                if (handler instanceof MultiPolygonHandler) {
                        Geometry geom = sds.getGeometry(handler.getGeometryIndex());
                        for (int i = 0; i < geom.getNumGeometries(); i++) {
                                result = computeGeometryDifference(geom.getGeometryN(i), result);
                        }
                } else if (handler instanceof PolygonHandler) {
                        Geometry geom = sds.getGeometry(handler.getGeometryIndex());
                        result = computeGeometryDifference(geom, result);


                }

                return result;




        }

        @Override
        public boolean isEnabled(MapContext vc, ToolManager tm) {
            try(Connection connection = vc.getDataManager().getDataSource().getConnection()) {
                return ToolUtilities.geometryTypeIs(connection,vc, GeometryTypeCodes.POLYGON,
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
        public double getInitialZ(MapContext mapContext) {
                return ToolUtilities.getActiveLayerInitialZ(mapContext);


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
