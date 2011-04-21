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
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.distance.GeometryLocation;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.operation.union.UnaryUnionOp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.orbisgis.utils.I18N;

/**
 *
 * @author ebocher
 */
public class GeometryEdit {

        static GeometryFactory geometryFactory = new GeometryFactory();

        public GeometryEdit(GeometryFactory geometryFactory) {
                this.geometryFactory = geometryFactory;
        }

        /**
         * Update all z ordinate by a new value for the first and the last
         * coordinates.
         *
         * @param geom
         * @param value
         * @return
         */
        public static Geometry force_3DStartEnd(Geometry geom, final double startZ,
                final double endZ) {

                final double D = geom.getLength();

                final double Z = endZ - startZ;

                final Coordinate coordEnd = geom.getCoordinates()[geom.getCoordinates().length - 1];

                geom.apply(new CoordinateSequenceFilter() {

                        boolean done = false;

                        public boolean isGeometryChanged() {
                                return true;
                        }

                        public boolean isDone() {
                                return done;
                        }

                        public void filter(CoordinateSequence seq, int i) {
                                double x = seq.getX(i);
                                double y = seq.getY(i);
                                if (i == 0) {
                                        seq.setOrdinate(i, 0, x);
                                        seq.setOrdinate(i, 1, y);
                                        seq.setOrdinate(i, 2, startZ);
                                } else if (i == seq.size() - 1) {
                                        seq.setOrdinate(i, 0, x);
                                        seq.setOrdinate(i, 1, y);
                                        seq.setOrdinate(i, 2, endZ);
                                } else {

                                        double d = seq.getCoordinate(i).distance(coordEnd);
                                        double factor = d / D;
                                        seq.setOrdinate(i, 0, x);
                                        seq.setOrdinate(i, 1, y);
                                        seq.setOrdinate(i, 2, startZ + (factor * Z));
                                }

                                if (i == seq.size()) {
                                        done = true;
                                }
                        }
                });

                return geom;

        }

        /**
         * Reverse a linestring according to z value. The z first point must be
         * greater than the z end point
         *
         * @param lineString
         * @return
         */
        public static LineString zReverse(LineString lineString) {

                double startZ = lineString.getStartPoint().getCoordinate().z;
                double endZ = lineString.getEndPoint().getCoordinate().z;
                if (Double.isNaN(startZ) || Double.isNaN(endZ)) {
                } else {
                        if (startZ < endZ) {
                                lineString = (LineString) lineString.reverse();
                        }
                }

                return lineString;
        }

        /**
         * Convert a xyz geometry to xy.
         *
         * @param geom
         * @param value
         * @return
         */
        public static Geometry force_2D(Geometry geom) {

                // return new Geometry2DTransformer().transform(geom);
                geom.apply(new CoordinateSequenceFilter() {

                        boolean done = false;

                        public boolean isGeometryChanged() {
                                return true;
                        }

                        public boolean isDone() {
                                return done;
                        }

                        public void filter(CoordinateSequence seq, int i) {
                                seq.setOrdinate(i, 2, Double.NaN);

                                if (i == seq.size()) {
                                        done = true;
                                }
                        }
                });

                return geom;

        }

        /**
         * Convert a xy geometry to xyz. If the z does not exits a z equals to zero
         * is added.
         *
         * @param geom
         * @param value
         * @return
         */
        public static Geometry force_3D(Geometry geom) {

                geom.apply(new CoordinateSequenceFilter() {

                        boolean done = false;

                        public boolean isGeometryChanged() {
                                return true;
                        }

                        public boolean isDone() {
                                return done;
                        }

                        public void filter(CoordinateSequence seq, int i) {
                                Coordinate coord = seq.getCoordinate(i);
                                double x = coord.x;
                                double y = coord.y;
                                double z = coord.z;
                                seq.setOrdinate(i, 0, x);
                                seq.setOrdinate(i, 1, y);
                                if (Double.isNaN(z)) {
                                        seq.setOrdinate(i, 2, 0);
                                }
                                if (i == seq.size()) {
                                        done = true;
                                }
                        }
                });

                return geom;

        }

        /**
         * Update all z ordinate by a new value
         *
         * @param geom
         * @param value
         * @return
         */
        public static Geometry force_3D(Geometry geom, final double value) {

                geom.apply(new CoordinateSequenceFilter() {

                        boolean done = false;

                        public boolean isGeometryChanged() {
                                return true;
                        }

                        public boolean isDone() {
                                return done;
                        }

                        public void filter(CoordinateSequence seq, int i) {
                                double x = seq.getX(i);
                                double y = seq.getY(i);
                                seq.setOrdinate(i, 0, x);
                                seq.setOrdinate(i, 1, y);
                                seq.setOrdinate(i, 2, value);
                                if (i == seq.size()) {
                                        done = true;
                                }
                        }
                });
                return geom;
        }

        /**
         * Split a LineString according a point         * 
         * @param line
         * @param pointToSplit
         * @return
         */
        public static LineString[] splitLineString(LineString line, Point pointToSplit) {
                return splitLineString(line, pointToSplit, -1);
        }

        /**
         * Split a LineString according a point with a given distance
         * @param line
         * @param geometryLocation
         * @return
         */
        public static LineString[] splitLineString(LineString line, Point pointToSplit, double tolerance) {
                Coordinate[] coords = line.getCoordinates();
                ArrayList<Coordinate> firstLine = new ArrayList<Coordinate>();
                firstLine.add(coords[0]);
                ArrayList<Coordinate> secondLine = new ArrayList<Coordinate>();
                GeometryLocation geometryLocation = getVertexToSnap(line, pointToSplit, tolerance);
                if (geometryLocation != null) {
                        int segmentIndex = geometryLocation.getSegmentIndex();
                        Coordinate coord = geometryLocation.getCoordinate();
                        int index = -1;
                        for (int i = 1; i < coords.length; i++) {
                                index = i - 1;
                                if (index < segmentIndex) {
                                        firstLine.add(coords[i]);
                                } else if (index == segmentIndex) {
                                        coord.z = CoordinatesUtils.interpolate(coords[i - 1], coords[i], coord);
                                        firstLine.add(coord);
                                        secondLine.add(coord);
                                        if (!coord.equals2D(coords[i])) {
                                                secondLine.add(coords[i]);
                                        }
                                } else {
                                        secondLine.add(coords[i]);
                                }

                        }
                        LineString lineString1 = line.getFactory().createLineString(firstLine.toArray(new Coordinate[firstLine.size()]));
                        LineString lineString2 = line.getFactory().createLineString(secondLine.toArray(new Coordinate[firstLine.size()]));
                        return new LineString[]{lineString1, lineString2};
                }
                return null;
        }

        /**
         * Split a multilinestring with a point
         * @param multiLineString
         * @param pointToSplit
         * @return
         */
        public static MultiLineString splitMultiLineString(MultiLineString multiLineString, Point pointToSplit, double tolerance) {
                ArrayList<LineString> linestrings = new ArrayList<LineString>();
                boolean notChanged = true;
                int nb = multiLineString.getNumGeometries();
                for (int i = 0; i < nb; i++) {
                        LineString subGeom = (LineString) multiLineString.getGeometryN(i);
                        LineString[] result = splitLineString(subGeom, pointToSplit, tolerance);
                        if (result != null) {
                                Collections.addAll(linestrings, result);
                                notChanged = false;
                        } else {
                                linestrings.add(subGeom);
                        }
                }
                if (!notChanged) {
                        return geometryFactory.createMultiLineString(linestrings.toArray(new LineString[linestrings.size()]));
                }
                return null;


        }

        /**
         * A method to find the coordinate along a linestring
         * @param g
         * @param p
         * @param tolerance
         * @return
         */
        public static GeometryLocation getVertexToSnap(Geometry g, Point p, double tolerance) {
                DistanceOp distanceOp = new DistanceOp(g, p);
                GeometryLocation snapedPoint = distanceOp.nearestLocations()[0];
                if (tolerance == -1 || snapedPoint.getCoordinate().distance(p.getCoordinate()) <= tolerance) {
                        return snapedPoint;
                }

                return null;

        }

        /**
         * A method to find the coordinate along a linestring
         * @param g
         * @param p
         * @param tolerance
         * @return
         */
        public static GeometryLocation getVertexToSnap(Geometry g, Point p) {
                return getVertexToSnap(g, p, -1);
        }

        /**
         * Insert a vertex into a lineString
         * @param lineString
         * @param vertexPoint
         * @return
         * @throws GeometryException
         */
        public static Geometry insertVertexInLineString(LineString lineString, Point vertexPoint) throws GeometryException {
                return insertVertexInLineString(lineString, vertexPoint, -1);
        }

        /**
         * Insert a vertex into a lineString with a given tolerance
         * @param lineString
         * @param vertexPoint
         * @param tolerance
         * @return
         * @throws GeometryException
         */
        public static LineString insertVertexInLineString(LineString lineString, Point vertexPoint,
                double tolerance) throws GeometryException {
                GeometryLocation geomLocation = getVertexToSnap(lineString, vertexPoint, tolerance);
                if (geomLocation != null) {
                        Coordinate[] coords = lineString.getCoordinates();
                        int index = geomLocation.getSegmentIndex();
                        Coordinate coord = geomLocation.getCoordinate();
                        if (!CoordinatesUtils.contains2D(coords, coord)) {
                                Coordinate[] ret = new Coordinate[coords.length + 1];
                                System.arraycopy(coords, 0, ret, 0, index + 1);
                                ret[index + 1] = coord;
                                System.arraycopy(coords, index + 1, ret, index + 2, coords.length
                                        - (index + 1));
                                return geometryFactory.createLineString(ret);
                        }
                        return null;
                } else {
                        return null;
                }


        }

        /**
         * Insert a vertex into a linearRing
         * @param lineString
         * @param vertexPoint
         * @return
         */
        public static LinearRing insertVertexInLinearRing(LineString lineString,
                Point vertexPoint) {
                return insertVertexInLinearRing(lineString, vertexPoint, -1);

        }

        /**
         * Insert a vertex into a linearRing with a given tolerance
         * @param lineString
         * @param vertexPoint
         * @param tolerance
         * @return
         */
        public static LinearRing insertVertexInLinearRing(LineString lineString,
                Point vertexPoint, double tolerance) {
                GeometryLocation geomLocation = getVertexToSnap(lineString, vertexPoint, tolerance);
                if (geomLocation != null) {
                        Coordinate[] coords = lineString.getCoordinates();
                        int index = geomLocation.getSegmentIndex();
                        Coordinate coord = geomLocation.getCoordinate();
                        if (!CoordinatesUtils.contains2D(coords, coord)) {
                                Coordinate[] ret = new Coordinate[coords.length + 1];
                                System.arraycopy(coords, 0, ret, 0, index + 1);
                                ret[index + 1] = coord;
                                System.arraycopy(coords, index + 1, ret, index + 2, coords.length
                                        - (index + 1));
                                return geometryFactory.createLinearRing(ret);
                        }
                        return null;
                } else {
                        return null;
                }
        }

        /**
         * Insert a vertex into a polygon
         * @param geometry
         * @param vertexPoint
         * @return
         * @throws GeometryException
         */
        public static Geometry insertVertexInPolygon(Polygon polygon,
                Point vertexPoint) throws GeometryException {
                return insertVertexInPolygon(polygon, vertexPoint, -1);

        }

        /**
         * Insert a vertex into a polygon with a given tolerance
         * @param geometry
         * @param vertexPoint
         * @param tolerance
         * @return
         * @throws GeometryException
         */
        public static Polygon insertVertexInPolygon(Polygon polygon,
                Point vertexPoint, double tolerance) throws GeometryException {
                LinearRing inserted = insertVertexInLinearRing(polygon.getExteriorRing(), vertexPoint, tolerance);
                if (inserted != null) {
                        LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
                        for (int i = 0; i < holes.length; i++) {
                                holes[i] = geometryFactory.createLinearRing(polygon.getInteriorRingN(i).getCoordinates());
                        }
                        Polygon ret = geometryFactory.createPolygon(inserted, holes);

                        if (!ret.isValid()) {
                                throw new GeometryException(I18N.getString("gdms.geometryUtils.geometryException.geometryNotValid"));
                        }

                        return ret;
                }

                for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
                        inserted = insertVertexInLinearRing(polygon.getInteriorRingN(i), vertexPoint, tolerance);
                        if (inserted != null) {
                                LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
                                for (int h = 0; h < holes.length; h++) {
                                        if (h == i) {
                                                holes[h] = inserted;
                                        } else {
                                                holes[h] = geometryFactory.createLinearRing(polygon.getInteriorRingN(h).getCoordinates());
                                        }
                                }

                                Polygon ret = geometryFactory.createPolygon(geometryFactory.createLinearRing(polygon.getExteriorRing().getCoordinates()), holes);

                                if (!ret.isValid()) {
                                        throw new GeometryException(I18N.getString("gdms.geometryUtils.geometryException.geometryNotValid"));
                                }

                                return ret;
                        }
                }

                return null;
        }

        /**
         * Insert a point into a multipoints geometry.
         * @param g
         * @param vertexPoint
         * @param tolerance
         * @return
         * @throws CannotChangeGeometryException
         */
        public static Geometry insertVertexInMultipoint(Geometry g, Point vertexPoint) {
                ArrayList<Point> geoms = new ArrayList<Point>();
                for (int i = 0; i < g.getNumGeometries(); i++) {
                        Point geom = (Point) g.getGeometryN(i);
                        geoms.add(geom);
                }
                geoms.add(geometryFactory.createPoint(new Coordinate(vertexPoint.getX(), vertexPoint.getY())));
                return geometryFactory.createMultiPoint(GeometryFactory.toPointArray(geoms));
        }

        /**
         * Insert a point into a geometry
         * @param geom
         * @param point
         * @return
         */
        public static Geometry insertVertex(Geometry geom, Point point) throws GeometryException {
                return insertVertex(geom, point, -1);
        }

        /**
         * Gets this geometry by adding the specified point as a new vertex
         *
         * @param vertexPoint
         * @param tolerance
         * @return Null if the vertex cannot be inserted
         * @throws CannotChangeGeometryException
         *             If the vertex can be inserted but it makes the geometry to be
         *             in an invalid shape
         */
        public static Geometry insertVertex(Geometry geometry, Point vertexPoint, double tolerance)
                throws GeometryException {
                if (geometry instanceof Point) {
                } else if (geometry instanceof MultiPoint) {
                        return insertVertexInMultipoint(geometry, vertexPoint);
                } else if (geometry instanceof LineString) {
                        return insertVertexInLineString((LineString) geometry, vertexPoint, tolerance);
                } else if (geometry instanceof MultiLineString) {
                        LineString[] linestrings = new LineString[geometry.getNumGeometries()];
                        boolean any = false;
                        for (int i = 0; i < geometry.getNumGeometries(); i++) {
                                LineString line = (LineString) geometry.getGeometryN(i);

                                LineString inserted = (LineString) insertVertexInLineString(line,
                                        vertexPoint, tolerance);
                                if (inserted != null) {
                                        linestrings[i] = inserted;
                                        any = true;
                                } else {
                                        linestrings[i] = line;
                                }
                        }
                        if (any) {
                                return geometryFactory.createMultiLineString(linestrings);
                        } else {
                                return null;
                        }
                } else if (geometry instanceof Polygon) {
                        return insertVertexInPolygon((Polygon) geometry, vertexPoint, tolerance);
                } else if (geometry instanceof MultiPolygon) {
                        Polygon[] polygons = new Polygon[geometry.getNumGeometries()];
                        boolean any = false;
                        for (int i = 0; i < geometry.getNumGeometries(); i++) {
                                Polygon polygon = (Polygon) geometry.getGeometryN(i);

                                Polygon inserted = (Polygon) insertVertexInPolygon(polygon,
                                        vertexPoint, tolerance);
                                if (inserted != null) {
                                        any = true;
                                        polygons[i] = inserted;
                                } else {
                                        polygons[i] = polygon;
                                }
                        }
                        if (any) {
                                return geometryFactory.createMultiPolygon(polygons);
                        } else {
                                return null;
                        }
                }

                throw new UnsupportedOperationException(I18N.getString("gdms.geometryUtils.geometryException.unknownType") + " : " + geometry.getGeometryType()); //$NON-NLS-1$
        }

        /**
         * 
         * @param polygon
         * @param lineString
         * @return
         */
        public static Collection<Polygon> splitPolygonizer(Polygon polygon, LineString lineString) {
                Set<LineString> segments = GeometryConvert.toSegmentsLineString(polygon.getExteriorRing());
                segments.add(lineString);
                int holes = polygon.getNumInteriorRing();

                for (int i = 0; i < holes; i++) {
                        segments.addAll(GeometryConvert.toSegmentsLineString(polygon.getInteriorRingN(i)));
                }

                // Perform union of all extracted LineStrings (the edge-noding process)
                UnaryUnionOp uOp = new UnaryUnionOp(segments);
                Geometry union = uOp.union();

                // Create polygons from unioned LineStrings
                Polygonizer polygonizer = new Polygonizer();
                polygonizer.add(union);
                Collection<Polygon> polygons = polygonizer.getPolygons();

                if (polygons.size() > 1) {
                        return polygons;
                }

                return null;
        }

        /**
         * Split a polygon with a lineString
         * @param polygon
         * @param lineString
         * @return
         */
        public static ArrayList<Polygon> splitPolygon(Polygon polygon, LineString lineString) {
                Collection<Polygon> polygons = splitPolygonizer(polygon, lineString);
                if (polygons != null && polygons.size() > 1) {
                        ArrayList<Polygon> pols = new ArrayList<Polygon>();
                        for (Polygon pol : polygons) {
                                if (polygon.contains(pol.getInteriorPoint())) {
                                        pols.add(pol);
                                }
                        }
                        return pols;
                }
                return null;
        }

        /**
         * Split a multiPolygon with a lineString
         * @param multiPolygon
         * @param lineString
         * @return
         */
        public static MultiPolygon splitMultiPolygon(MultiPolygon multiPolygon, LineString lineString) {
                ArrayList<Polygon> polygons = new ArrayList<Polygon>();
                boolean notChanged = true;
                int nb = multiPolygon.getNumGeometries();
                for (int i = 0; i < nb; i++) {
                        Polygon subGeom = (Polygon) multiPolygon.getGeometryN(i);
                        ArrayList<Polygon> result = splitPolygon(subGeom, lineString);
                        if (result != null) {
                                polygons.addAll(result);
                                notChanged = false;
                        } else {
                                polygons.add(subGeom);
                        }
                }
                if (!notChanged) {
                        return geometryFactory.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));
                }
                return null;

        }

        /**
         * removes the vertex from the JTS geometry
         *
         * @param g
         *
         * @return
         *
         * @throws GeometryException
         */
        public static Coordinate[] removeVertex(int vertexIndex,
                Geometry g, int minNumVertex)
                throws GeometryException {
                Coordinate[] coords = g.getCoordinates();
                if (coords.length <= minNumVertex) {
                        throw new GeometryException(
                                I18N.getString("orbisgis.org.orbisgis.ui.tool.AbstractHandler.invalidGeometryToFewVertex")); //$NON-NLS-1$
                }
                Coordinate[] newCoords = new Coordinate[coords.length - 1];
                for (int i = 0; i < vertexIndex; i++) {
                        newCoords[i] = new Coordinate(coords[i].x, coords[i].y, coords[i].z);
                }
                if (vertexIndex != coords.length - 1) {
                        for (int i = vertexIndex + 1; i < coords.length; i++) {
                                newCoords[i - 1] = new Coordinate(coords[i].x, coords[i].y, coords[i].z);
                        }
                }

                return newCoords;
        }

        /**
         * Remove a vertex on a multipoint.
         * @param geometry
         * @param vertexIndex
         * @return
         * @throws GeometryException
         */
        public static MultiPoint removeVertex(MultiPoint geometry, int vertexIndex) throws GeometryException {
                return geometryFactory.createMultiPoint(removeVertex(vertexIndex, geometry, 1));
        }

        /**
         * Remove a vertex on a linestring
         * @param geometry
         * @param vertexIndex
         * @return
         * @throws GeometryException
         */
        public static LineString removeVertex(LineString geometry, int vertexIndex) throws GeometryException {
                return geometryFactory.createLineString(removeVertex(vertexIndex, geometry, 2));
        }

        /**
         * Move a geometry according a distance displacement in x and y
         * @param geometry
         * @param displacement
         * @return
         */
        public static Geometry moveGeometry(Geometry geometry, final double[] displacement) {
                geometry.apply(new CoordinateFilter() {

                        public void filter(Coordinate coordinate) {
                                coordinate.x += displacement[0];
                                coordinate.y += displacement[1];
                        }
                });
                return geometry;
        }

        /**
         * Move a geometry according a start and end coordinate
         * @param geometry
         * @param start
         * @param end
         * @return
         */
        public static Geometry moveGeometry(Geometry geometry, Coordinate start, Coordinate end) {
                double xDisplacement = end.x - start.x;
                double yDisplacement = end.y - start.y;
                return moveGeometry(geometry, new double[]{xDisplacement, yDisplacement});
        }

        /**
         * Cut a polygon with a polygon
         * @param polygon
         * @param hole
         * @return
         */
        public static ArrayList<Polygon> cutPolygon(Polygon polygon, Polygon extrudePolygon) {
                Geometry geom = polygon.difference(extrudePolygon);
                ArrayList<Polygon> polygons = new ArrayList<Polygon>();
                for (int i = 0; i < geom.getNumGeometries(); i++) {
                        Polygon subGeom = (Polygon) geom.getGeometryN(i);
                        polygons.add(subGeom);

                }
                return polygons;
        }

        /**
         * Cut a multipolygon with a polygon
         * @param multiPolygon
         * @param extrudePolygon
         * @return
         */
        public static MultiPolygon cutMultiPolygon(MultiPolygon multiPolygon, Polygon extrudePolygon) {
                ArrayList<Polygon> polygons = new ArrayList<Polygon>();
                boolean notChanged = true;
                for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                        Polygon subGeom = (Polygon) multiPolygon.getGeometryN(i);
                        if (extrudePolygon.intersects(subGeom)) {
                                ArrayList<Polygon> result = cutPolygon(subGeom, extrudePolygon);
                                if (result != null) {
                                        polygons.addAll(result);
                                        notChanged = false;
                                } else {
                                        polygons.add(subGeom);
                                }
                        } else {
                                polygons.add(subGeom);
                        }
                }
                if (!notChanged) {
                        return geometryFactory.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));
                }
                return null;

        }
}
