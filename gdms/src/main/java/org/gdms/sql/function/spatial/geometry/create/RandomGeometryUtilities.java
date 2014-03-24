/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.function.spatial.geometry.create;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Utility class for generating random Geometry objects.
 */
public final class RandomGeometryUtilities {

        private static final Random RAND = new Random();
        private static final GeometryFactory GF = new GeometryFactory();
        private int maxHeight = 80;
        private int maxNodesPerLineMinus3;

        /**
         * Creates a new <tt>RandomGeometryUtilities</tt> with a maximum
         * number (minus 3) of points per line of 12.
         */
        public RandomGeometryUtilities() {
                this(12);
        }

        /**
         * Creates a new <tt>RandomGeometryUtilities</tt>
         * @param maxNodesPerLineMinus3 the maximum number (minus 3) of points per line
         */
        public RandomGeometryUtilities(final int maxNodesPerLineMinus3) {
                this.maxNodesPerLineMinus3 = maxNodesPerLineMinus3;
        }

        /**
         * Gets a random number of points per line
         * @return
         */
        private int getNumberOfNodesPerLine() {
                // In a LinearRing number of points must be 0 or >3
                return RAND.nextInt(maxNodesPerLineMinus3) + 3;
        }

        /**
         * Gets a new random Coordinate
         * @return
         */
        public Coordinate nextCoordinate() {
                return new Coordinate(RAND.nextLong(), RAND.nextLong(), RAND.nextLong());
        }

        /**
         * Gets a new random Coordinate inside the given Envelope
         * @param envelope
         * @return
         */
        public Coordinate nextCoordinate(final Envelope envelope) {
                return new Coordinate(RAND.nextInt((int) envelope.getWidth())
                        + envelope.getMinX(), RAND.nextInt((int) envelope.getHeight())
                        + envelope.getMinY(), RAND.nextInt(maxHeight));
        }

        /**
         * Gets a array of length n full of random Coordinate
         * @param n
         * @return
         */
        public Coordinate[] nextCoordinates(final int n) {
                final Coordinate[] result = new Coordinate[n];
                for (int i = 0; i < n; i++) {
                        result[i] = nextCoordinate();
                }
                return result;
        }

        /**
         * Gets a point with random coordinates
         * @return
         */
        public Point nextPoint() {
                return GF.createPoint(nextCoordinate());
        }

        /**
         * Gets a point inside an envelope with random coordinate
         * @param envelope
         * @return
         */
        public Point nextPoint(final Envelope envelope) {
                return GF.createPoint(nextCoordinate(envelope));
        }

        /**
         * Gets an array of n random points
         * @param n
         * @return
         */
        public Point[] nextPoints(final int n) {
                final Point[] result = new Point[n];
                for (int i = 0; i < n; i++) {
                        result[i] = nextPoint();
                }
                return result;
        }

        /**
         * Gets a random LineSegment
         * @return
         */
        public LineSegment nextLineSegment() {
                return new LineSegment(nextCoordinate(), nextCoordinate());
        }

        /**
         * Gets an array of n random LineSegment
         * @param n
         * @return
         */
        public LineSegment[] nextLineSegments(final int n) {
                final LineSegment[] result = new LineSegment[n];
                for (int i = 0; i < n; i++) {
                        result[i] = nextLineSegment();
                }
                return result;
        }

        /**
         * Gets a random LineString
         * @return
         */
        public LineString nextLineString() {
                final int n = getNumberOfNodesPerLine();
                final SortedSet<Coordinate> nodes = new TreeSet<Coordinate>();
                while (n > nodes.size()) {
                        nodes.add(nextCoordinate());
                }
                return GF.createLineString(nodes.toArray(new Coordinate[nodes.size()]));
        }

        /**
         * Gets a random LineString contained in an envelope
         * @param envelope
         * @return
         */
        public LineString nextLineString(final Envelope envelope) {
                final int n = getNumberOfNodesPerLine();
                final SortedSet<Coordinate> nodes = new TreeSet<Coordinate>();
                while (n > nodes.size()) {
                        nodes.add(nextCoordinate(envelope));
                }
                return GF.createLineString(nodes.toArray(new Coordinate[nodes.size()]));
        }

        /**
         * Gets an array of n random LineString objects
         * @param n
         * @return
         */
        public LineString[] nextLineStrings(final int n) {
                final LineString[] result = new LineString[n];
                for (int i = 0; i < n; i++) {
                        result[i] = nextLineString();
                }
                return result;
        }

        /**
         * Gets a random LinearRing
         * @return
         */
        public LinearRing nextLinearRing() {
                LinearRing result;
                do {
                        final CoordinateList cl = new CoordinateList(nextLineString().getCoordinates());
                        cl.closeRing();
                        result = GF.createLinearRing(cl.toCoordinateArray());
                } while (!result.isValid());
                return result;
        }

        /**
         * Gets a random LinearString contained in an envelope
         * @param envelope
         * @return
         */
        public LinearRing nextLinearRing(final Envelope envelope) {
                LinearRing result;
                do {
                        final CoordinateList cl = new CoordinateList(nextLineString(
                                envelope).getCoordinates());
                        cl.closeRing();
                        result = GF.createLinearRing(cl.toCoordinateArray());
                } while (!result.isValid());
                return result;
        }

        /**
         * Gets an array of n random LinearRing objects
         * @param n
         * @return
         */
        public LinearRing[] nextLinearRings(final int n) {
                final LinearRing[] result = new LinearRing[n];
                for (int i = 0; i < n; i++) {
                        result[i] = nextLinearRing();
                }
                return result;
        }

        /**
         * Gets a random Polygon with no holes
         * @return
         */
        public Polygon nextNoHolePolygon() {
                return GF.createPolygon(nextLinearRing(), null);
        }

        /**
         * Gets a random Polygon with no holes, included in an envelope
         * @param envelope
         * @return
         */
        public Polygon nextNoHolePolygon(final Envelope envelope) {
                return GF.createPolygon(nextLinearRing(envelope), null);
        }

        /**
         * Gets an array of n random Polygon objects with no holes
         * @param n
         * @return
         */
        public Polygon[] nextNoHolePolygons(final int n) {
                final Polygon[] result = new Polygon[n];
                for (int i = 0; i < n; i++) {
                        result[i] = nextNoHolePolygon();
                }
                return result;
        }

        /**
         * Gets a random polygon
         * @return
         */
        public Polygon nextPolygon() {
                final LinearRing shell = nextLinearRing();
                final int nbHoles = 1;
                final LinearRing[] holes = new LinearRing[nbHoles];
                for (int i = 0; i < nbHoles; i++) {
                        do {
                                holes[i] = nextLinearRing(shell.getEnvelopeInternal());
                        } while (!shell.contains(holes[i]));
                }
                return GF.createPolygon(shell, holes);
        }

        /**
         * Gets a random Geometry. The returned geometry can be of any of the
         * following types:
         *      Point
         *      LineString
         *      LinearRing
         *      Polygon (with no holes)
         * @return
         */
        public Geometry nextGeometry() {
                switch (RAND.nextInt(4)) {
                        case 0:
                                return nextPoint();
                        case 1:
                                return nextLineString();
                        case 2:
                                return nextLinearRing();
                        case 3:
                                return nextNoHolePolygon();
                        default:
                                // unreachable code
                                return null;
                }
        }

        /**
         * Gets a random Geometry contained inside a envelope. The returned
         * geometry can be of any of the following types:
         *      Point
         *      LineString
         *      LinearRing
         *      Polygon (with no holes)
         * @param env an envelope
         * @return
         */
        public Geometry nextGeometry(Envelope env) {
                switch (RAND.nextInt(4)) {
                        case 0:
                                return nextPoint(env);
                        case 1:
                                return nextLineString(env);
                        case 2:
                                return nextLinearRing(env);
                        case 3:
                                return nextNoHolePolygon(env);
                        default:
                                // unreachable code
                                return null;
                }
        }

        /**
         * Gets an array of n random Geometry objects. The returned geometry
         * can be of any of the following types:
         *      Point
         *      LineString
         *      LinearRing
         *      Polygon (with no holes)
         * @param n number of Geometry objects
         * @return
         */
        public Geometry[] nextGeometries(final int n) {
                final Geometry[] result = new Geometry[n];
                for (int i = 0; i < n; i++) {
                        result[i] = nextGeometry();
                }
                return result;
        }
}
