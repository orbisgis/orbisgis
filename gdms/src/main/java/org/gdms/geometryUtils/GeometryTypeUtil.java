/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *
 * Copyright (C) 2007-2008 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Antoine GOURLAY, Alexis GUEGANNO, Maxence LAURENT, Gwendall PETIT
 *
 * Copyright (C) 2011 Erwan BOCHER, Antoine GOURLAY, Alexis GUEGANNO, Maxence LAURENT, Gwendall PETIT
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.gdms.geometryUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/**
 *
 * @author ebocher
 */
public class GeometryTypeUtil {

        static GeometryFactory gf = new GeometryFactory();
        public static String POINT_GEOMETRY_TYPE;
        public static String MULTIPOINT_GEOMETRY_TYPE;
        public static String LINESTRING_GEOMETRY_TYPE;
        public static String LINEARRING_GEOMETRY_TYPE;
        public static String MULTILINESTRING_GEOMETRY_TYPE;
        public static String POLYGON_GEOMETRY_TYPE;
        public static String MULTIPOLYGON_GEOMETRY_TYPE;
        public static String GEOMETRYCOLLECTION_GEOMETRY_TYPE;

        static {
                POINT_GEOMETRY_TYPE = gf.createPoint(new Coordinate(0, 0)).getGeometryType();
                MULTIPOINT_GEOMETRY_TYPE = gf.createMultiPoint(
                        new Coordinate[]{new Coordinate(0, 0)}).getGeometryType();
                LineString ls = gf.createLineString(new Coordinate[]{
                                new Coordinate(0, 0), new Coordinate(1, 0)});
                LINESTRING_GEOMETRY_TYPE = ls.getGeometryType();
                MULTILINESTRING_GEOMETRY_TYPE = gf.createMultiLineString(
                        new LineString[]{ls}).getGeometryType();
                LinearRing lr = gf.createLinearRing(new Coordinate[]{
                                new Coordinate(0, 0), new Coordinate(1, 1),
                                new Coordinate(1, 0), new Coordinate(0, 0)});
                LINEARRING_GEOMETRY_TYPE = lr.getGeometryType();
                Polygon pol = gf.createPolygon(lr, new LinearRing[0]);
                POLYGON_GEOMETRY_TYPE = pol.getGeometryType();
                MULTIPOLYGON_GEOMETRY_TYPE = gf.createMultiPolygon(
                        new Polygon[]{pol}).getGeometryType();
                GEOMETRYCOLLECTION_GEOMETRY_TYPE = gf.createGeometryCollection(new Geometry[]{pol, ls}).getGeometryType();
        }

        /**
         * Test if the geometry is a point
         * @param geometry
         * @return
         */
        public static boolean isPoint(Geometry geometry) {
                return geometry.getGeometryType().equals(POINT_GEOMETRY_TYPE);
        }

        /**
         * Test if the geometry is a polygon
         * @param geometry
         * @return
         */
        public static boolean isLineString(Geometry geometry) {
                return geometry.getGeometryType().equals(LINESTRING_GEOMETRY_TYPE);
        }

        /**
         * Test if the geometry is a polygon
         * @param geometry
         * @return
         */
        public static boolean isLinearRing(Geometry geometry) {
                return geometry.getGeometryType().equals(LINEARRING_GEOMETRY_TYPE);
        }

        /**
         * Test if the geometry is a polygon
         * @param geometry
         * @return
         */
        public static boolean isPolygon(Geometry geometry) {
                return geometry.getGeometryType().equals(POLYGON_GEOMETRY_TYPE);
        }

        /**
         * Test if the geometry is a multipoint
         * @param geometry
         * @return
         */
        public static boolean isMultiPoint(Geometry geometry) {
                return geometry.getGeometryType().equals(MULTIPOINT_GEOMETRY_TYPE);
        }

        /**
         * Test if the geometry is a multipoint
         * @param geometry
         * @return
         */
        public static boolean isMultiLineString(Geometry geometry) {
                return geometry.getGeometryType().equals(MULTILINESTRING_GEOMETRY_TYPE);
        }

        /**
         * Test if the geometry is a multipolygon
         * @param geometry
         * @return
         */
        public static boolean isMultiPolygon(Geometry geometry) {
                return geometry.getGeometryType().equals(MULTIPOLYGON_GEOMETRY_TYPE);
        }

        /**
         * Test if the geometry is a multipolygon
         * @param geometry
         * @return
         */
        public static boolean isGeometryCollection(Geometry geometry) {
                return geometry.getGeometryType().equals(GEOMETRYCOLLECTION_GEOMETRY_TYPE);
        }

        /**
         * Return true is a geometry contains at least a z value
         * @param geometry
         * @return
         */
        public static boolean is2_5Geometry(Geometry geometry) {
                Coordinate[] coords = geometry.getCoordinates();
                for (Coordinate coordinate : coords) {
                        if (!Double.isNaN(coordinate.z)) {
                                return true;
                        }
                }
                return false;
        }
}
