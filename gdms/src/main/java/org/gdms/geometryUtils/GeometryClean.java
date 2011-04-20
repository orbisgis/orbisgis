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
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;

/**
 *
 * @author ebocher
 */
public class GeometryClean {

        static GeometryFactory geometryFactory = new GeometryFactory();

        /**
         * Remove duplicate points into a geometry
         * @param geom
         * @return
         */
        public static Geometry removeDuplicateCoordinates(Geometry geom) {
                if (geom.isEmpty()) {
                        return geom;
                } else if (GeometryTypeUtil.isPoint(geom) || GeometryTypeUtil.isMultiPoint(geom)) {
                        return geom;
                } else if (GeometryTypeUtil.isLineString(geom)) {
                        return removeDuplicateCoordinates((LineString) geom);
                } else if (GeometryTypeUtil.isMultiLineString(geom)) {
                        return removeDuplicateCoordinates((MultiLineString) geom);
                } else if (GeometryTypeUtil.isPolygon(geom)) {
                        return removeDuplicateCoordinates((Polygon) geom);
                } else if (GeometryTypeUtil.isMultiPolygon(geom)) {
                        return removeDuplicateCoordinates((MultiPolygon) geom);
                } else if (GeometryTypeUtil.isGeometryCollection(geom)) {
                        return removeDuplicateCoordinates((GeometryCollection) geom);
                }
                return null;
        }

        /**
         * Remove duplicate coordinates in a linestring
         * @param g
         * @return
         */
        public static LineString removeDuplicateCoordinates(LineString g) {
                Coordinate[] coords = CoordinateArrays.removeRepeatedPoints(g.getCoordinates());
                return geometryFactory.createLineString(coords);
        }

        /**
         * Remove duplicate coordinates in a linearRing
         * @param g
         * @return
         */
        public static LinearRing removeDuplicateCoordinates(LinearRing g) {
                Coordinate[] coords = CoordinateArrays.removeRepeatedPoints(g.getCoordinates());
                return geometryFactory.createLinearRing(coords);
        }

        /**
         * Remove duplicate coordinates in a multiLineString
         * @param g
         * @return
         */
        public static MultiLineString removeDuplicateCoordinates(MultiLineString g) {
                ArrayList<LineString> lines = new ArrayList<LineString>();
                for (int i = 0; i < g.getNumGeometries(); i++) {
                        LineString line = (LineString) g.getGeometryN(i);
                        lines.add(removeDuplicateCoordinates(line));
                }
                return geometryFactory.createMultiLineString(GeometryFactory.toLineStringArray(lines));
        }

        /**
         * Remove duplicate coordinates in a polygon
         * @param g
         * @return
         */
        public static Polygon removeDuplicateCoordinates(Polygon poly) {
                Coordinate[] shellCoords = CoordinateArrays.removeRepeatedPoints(poly.getExteriorRing().getCoordinates());
                LinearRing shell = geometryFactory.createLinearRing(shellCoords);
                ArrayList<LinearRing> holes = new ArrayList<LinearRing>();
                for (int i = 0; i < poly.getNumInteriorRing(); i++) {
                        Coordinate[] holeCoords = CoordinateArrays.removeRepeatedPoints(poly.getInteriorRingN(i).getCoordinates());
                        holes.add(geometryFactory.createLinearRing(holeCoords));
                }
                return geometryFactory.createPolygon(shell, GeometryFactory.toLinearRingArray(holes));
        }

        /**
         * Remove duplicate coordinates in a multiPolygon
         * @param g
         * @return
         */
        public static MultiPolygon removeDuplicateCoordinates(MultiPolygon g) {
                ArrayList<Polygon> polys = new ArrayList<Polygon>();
                for (int i = 0; i < g.getNumGeometries(); i++) {
                        Polygon poly = (Polygon) g.getGeometryN(i);
                        polys.add(removeDuplicateCoordinates(poly));
                }
                return geometryFactory.createMultiPolygon(GeometryFactory.toPolygonArray(polys));
        }

        /**
         * Remove duplicate coordinates in a geometryCollection
         * @param g
         * @return
         */
        public static GeometryCollection removeDuplicateCoordinates(GeometryCollection g) {
                ArrayList<Geometry> geoms = new ArrayList<Geometry>();
                for (int i = 0; i < g.getNumGeometries(); i++) {
                        Geometry geom = g.getGeometryN(i);
                        geoms.add(removeDuplicateCoordinates(geom));
                }
                return geometryFactory.createGeometryCollection(GeometryFactory.toGeometryArray(geoms));
        }
}
