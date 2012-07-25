/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.common;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Shape;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Provides utility methods to handle Shape instances.
 * @author Maxence Laurent, Alexis Guéganno
 */
public final class ShapeHelper {

    public static final double ONE_DEG_IN_RAD = Math.PI / 180.0;
    private static final boolean ENABLE_QUAD = true;
    private static final double FLATNESS = 1e-5;
    private static final Logger LOGGER = Logger.getLogger(ShapeHelper.class);
    private static final I18n I18N = I18nFactory.getI18n(ShapeHelper.class);
    
    private ShapeHelper(){
    }

    /**
     * Compute the perimeter of the shape
     * @todo test and move
     * @param area
     * @return
     */
    public static double getAreaPerimeterLength(Shape area) {
        PathIterator it = area.getPathIterator(null, FLATNESS);

        double coords[] = new double[6];

        double p = 0.0;

        Double x1 = null;
        Double y1 = null;

        Double xFirst = null;
        Double yFirst = null;

        while (!it.isDone()) {
            int type = it.currentSegment(coords);

            double x2;
            double y2;

            if (type == PathIterator.SEG_CLOSE) {
                x2 = xFirst;
                y2 = yFirst;
            } else {
                x2 = coords[0];
                y2 = coords[1];
            }

            if (x1 != null && y1 != null) {
                double xx, yy;
                xx = x2 - x1;
                yy = y2 - y1;
                p += Math.sqrt(xx * xx + yy * yy);
            } else {
                xFirst = x2;
                yFirst = y2;
            }

            x1 = x2;
            y1 = y2;

            it.next();
        }

        return p;
    }

    /**
     * @see getAreaPerimeter
     */
    public static double getLineLength(Shape line) {
        return getAreaPerimeterLength(line);
    }

    /**
     * Uses the current logger to print the coordinate of each vertex of the 
     * given {@code Shape}.
     * @param shp 
     */
    public static void printvertices(Shape shp) {
        PathIterator it = shp.getPathIterator(null, FLATNESS);

        double coords[] = new double[6];

        while (!it.isDone()) {
            int type = it.currentSegment(coords);

            switch (type) {
                case PathIterator.SEG_CLOSE:
                    LOGGER.warn(I18N.tr("CLOSE"));
                    break;
                case PathIterator.SEG_CUBICTO:
                    LOGGER.warn(I18N.tr("CUBIC TO {0}:{1}",coords[0],coords[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    LOGGER.warn(I18N.tr("LINE TO {0}:{1}",coords[0],coords[1]));
                    break;
                case PathIterator.SEG_MOVETO:
                    LOGGER.warn(I18N.tr("MOVE TO {0}:{1}",coords[0],coords[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    LOGGER.warn(I18N.tr("QUAD TO {0}:{1}",coords[0],coords[1]));
                    break;
                default:
                    LOGGER.warn(I18N.tr("!DEFAULT!"));
                    break;
            }
            it.next();
        }
    }

    /**
     * Split a line into two lines. The first line length equals firstLineLenght parameter
     * The sum of the two line length equals the length of the initial line.
     *
     * If firstLineLenght > initial line length, only one line (initial line copy) is returned
     *
     * @param line the line to split
     * @param firstLineLength expected length of the first returned line
     *
     * @return Generated lines.
     */
    public static List<Shape> splitLine(Shape line, double firstLineLength) {

        ArrayList<Shape> shapes = new ArrayList<Shape>();

        if (ShapeHelper.getLineLength(line) < firstLineLength) {
            shapes.add(line);
            return shapes;
        }

        PathIterator it = line.getPathIterator(null, FLATNESS);
        double coords[] = new double[6];
        Path2D.Double segment = new Path2D.Double();
        double p = 0.0;
        double p1;

        it.currentSegment(coords);

        double x1 = coords[0];
        double y1 = coords[1];
        segment.moveTo(x1, y1);

        double xFirst = x1;
        double yFirst = y1;

        it.next();

        double x2;
        double y2;

        boolean first = true;

        while (!it.isDone()) {
            int type = it.currentSegment(coords);

            if (type == PathIterator.SEG_CLOSE) {
                x2 = xFirst;
                y2 = yFirst;
            } else {
                x2 = coords[0];
                y2 = coords[1];
            }

            double xx, yy;
            xx = x2 - x1;
            yy = y2 - y1;
            p1 = Math.sqrt(xx * xx + yy * yy);
            p += p1;

            if (first && p > firstLineLength) {
                first = false;
                // Le point courant dépasse la limite de longueur
                double delta = (p - firstLineLength);

                // Obtenir le point qui est exactement à la limite
                Point2D.Double pt = getPointAt(x1, y1, x2, y2, p1 - delta);

                x1 = pt.x;
                y1 = pt.y;

                // On termine le segment, l'ajoute à la liste de shapes
                segment.lineTo(x1, y1);
                p = 0;
                shapes.add(segment);

                // Et commence le nouveau segment à
                segment = new Path2D.Double();
                segment.moveTo(x1, y1);

            } else {
                // Le point courant dépasse la limite de longueur
                segment.lineTo(x2, y2);
                x1 = x2;
                y1 = y2;
                it.next();
            }
        }
        //last segment end with last point

        shapes.add(segment);

        return shapes;
    }

    /**
     * Split a linear feature in segments of specified length. Last one may be shorter
     *
     * @param line  the line to split
     * @param nbPart the number of part to create
     * @return list of equal-length segment
     */
    public static List<Shape> splitLineInSeg(Shape line, double segLength) {
        List<Shape> shapes = new ArrayList<Shape>();
        double totalLength = ShapeHelper.getLineLength(line);
        if (segLength <= 0.0 || segLength >= totalLength) {
            shapes.add(line);
            return shapes;
        }

        PathIterator it = line.getPathIterator(null, FLATNESS);
        double coords[] = new double[6];


        Path2D.Double segment = new Path2D.Double();
        double p = 0.0;
        double p1;

        it.currentSegment(coords);

        double x1 = coords[0];
        double y1 = coords[1];

        double xFirst = x1;
        double yFirst = y1;

        segment.moveTo(x1, y1);

        it.next();

        double x2;
        double y2;

        while (!it.isDone()) {
            int type = it.currentSegment(coords);

            if (type == PathIterator.SEG_CLOSE) {
                x2 = xFirst;
                y2 = yFirst;
            } else {
                x2 = coords[0];
                y2 = coords[1];
            }

            double xx, yy;
            xx = x2 - x1;
            yy = y2 - y1;
            p1 = Math.sqrt(xx * xx + yy * yy);
            p += p1;

            if (p > segLength) {
                // Le point courant dépasse la limite de longueur
                double delta = (p - segLength);

                // Obtenir le point qui est exactement à la limite
                Point2D.Double pt = getPointAt(x1, y1, x2, y2, p1 - delta);

                x1 = pt.x;
                y1 = pt.y;

                // On termine le segment, l'ajoute à la liste de shapes
                segment.lineTo(x1, y1);
                p = 0;
                shapes.add(segment);

                // Et commence le nouveau segment à
                segment = new Path2D.Double();
                segment.moveTo(x1, y1);

            } else {
                // Le point courant ne dépasse pas la limite de longueur
                segment.lineTo(x2, y2);
                x1 = x2;
                y1 = y2;
                it.next();
            }
        }
        shapes.add(segment);
        return shapes;
    }

    /**
     * Split a linear feature in the specified number of part, which have all the same length
     * 
     * @param line  the line to split
     * @param nbPart the number of part to create
     * @return list of equal-length segment
     */
    public static List<Shape> splitLine(Shape line, int nbPart) {
        ArrayList<Shape> shapes = new ArrayList<Shape>();
        double perimeter = getLineLength(line);

        double segLength = perimeter / nbPart;

        PathIterator it = line.getPathIterator(null, FLATNESS);
        double coords[] = new double[6];

        Path2D.Double segment = new Path2D.Double();
        double p = 0.0;
        double p1;

        it.currentSegment(coords);

        double x1 = coords[0];
        double y1 = coords[1];

        double xFirst = x1;
        double yFirst = y1;

        segment.moveTo(x1, y1);

        it.next();

        double x2;
        double y2;

        while (!it.isDone()) {
            int type = it.currentSegment(coords);

            if (type == PathIterator.SEG_CLOSE) {
                x2 = xFirst;
                y2 = yFirst;
            } else {
                x2 = coords[0];
                y2 = coords[1];
            }

            double xx, yy;
            xx = x2 - x1;
            yy = y2 - y1;
            p1 = Math.sqrt(xx * xx + yy * yy);
            p += p1;

            if (p > segLength) {
                // Le point courant dépasse la limite de longueur
                double delta = (p - segLength);

                // Obtenir le point qui est exactement à la limite
                Point2D.Double pt = getPointAt(x1, y1, x2, y2, p1 - delta);

                x1 = pt.x;
                y1 = pt.y;

                // On termine le segment, l'ajoute à la liste de shapes
                segment.lineTo(x1, y1);
                p = 0;
                shapes.add(segment);

                // Et commence le nouveau segment à
                segment = new Path2D.Double();
                segment.moveTo(x1, y1);

            } else {
                // Le point courant dépasse la limite de longueur
                segment.lineTo(x2, y2);
                x1 = x2;
                y1 = y2;
                it.next();
            }
        }
        //last segment end with last point
        //segment.lineTo(x1, y1);

        shapes.add(segment);

        return shapes;
    }

    /**
     * return coordinates which are:
     * 	1) at the specified distance from point (x1,y1)
     *  2) on the line going through points (x1,y1) and (x2,y2)
     * @param x1 first point x coordinate
     * @param y1 first point y coordinate
     * @param x2 second point x coordinate
     * @param y2 second point y coordinate
     * @param distance the distance between first point (x1,y1) and the returned one
     * @return the coordinate, nested in a point
     */
    private static Point2D.Double getPointAt(double x1, double y1, double x2, double y2, double distance) {
        double length = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return new Point2D.Double(x1 + distance * (x2 - x1) / length, y1 + distance * (y2 - y1) / length);
    }

    /**
     * Go along a line shape and return the point at the specified distance from the beginning of the line
     * @param shp  the line
     * @param distance
     * @return point representing the point at the linear length distance
     */
    public static Point2D.Double getPointAt(Shape shp, double distance) {
        PathIterator it = shp.getPathIterator(null, FLATNESS);

        double coords[] = new double[6];

        double p = 0.0;

        Double x1 = null;
        Double y1 = null;

        double x2 = 0.0;
        double y2 = 0.0;
        double segLength = 0.0;

        double xF = 0.0;
        double yF = 0.0;
        while (!it.isDone()) {
            int currentSegment = it.currentSegment(coords);

            x2 = coords[0];
            y2 = coords[1];

            if (currentSegment == PathIterator.SEG_CLOSE) {
                x2 = xF;
                y2 = yF;
            }

            // Since two point are known, we can start to look for our point
            if (x1 != null && y1 != null) {
                double xx, yy;
                xx = x2 - x1;
                yy = y2 - y1;
                segLength = Math.sqrt(xx * xx + yy * yy);
                p += segLength;

                if (p > distance) {
                    // The point is on this segment !
                    break;
                }
            } else {
                xF = x2;
                yF = y2;
            }

            it.next();

            if (!it.isDone()) {
                x1 = x2;
                y1 = y2;
            }
        }

        if (distance < 0.0) {
            return new Point2D.Double(xF, yF);
        } else {
            return getPointAt(x1, y1, x2, y2, segLength - p + distance);
        }
    }

    //private static Polygon perpendicularOffsetForArea() {
    //    return null;
    //}
    private static class Vertex {

        private double x;
        private double y;
        private Double quadX1;
        private Double quadY1;
        private Double quadX2;
        private Double quadY2;
        private Double quadX3;
        private Double quadY3;

        public Vertex(double x, double y) {
            this.x = x;
            this.y = y;
            quadX1 = null;
            quadY1 = null;
            quadX2 = null;
            quadY2 = null;
            quadX3 = null;
            quadY3 = null;
        }

        public void setQuadTo(double x1, double y1, double x2, double y2, double x3, double y3) {
            quadX1 = x1;
            quadY1 = y1;
            quadX2 = x2;
            quadY2 = y2;
            quadX3 = x3;
            quadY3 = y3;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Vertex) {
                Vertex v = (Vertex) o;
                return Math.abs(v.x - this.x) < 0.0001 && Math.abs(v.y - this.y) < 0.0001;
            }
            return false;
        }

        @Override
        public int hashCode() {
            assert false : "hashCode not designed";
            return 42; // any arbitraty constant !
        }

        @Override
        public String toString() {
            return "" + x + ";" + y;
        }
    }

    private static class Edge {

        private int mPos;
        private int mDir;
        private boolean processed;

        public Edge() {
            this.processed = false;
            mPos = 1;
            mDir = 0;
        }

        public boolean hasBeedProcessed() {
            return processed;
        }

        public boolean is11() {
            return mPos == 1 && mDir == 1;
        }

        public boolean is10() {
            return mPos == 0 && mDir == 1;
        }

        public boolean isUnfeasible() {
            return mPos == 0;
        }

        @Override
        public String toString() {
            return "" + mDir + mPos;
        }
    }

    /**
     * Convert Shape into a list of coordinates.
     * Will also convert curves to set of segment
     * @param shp the shape to convert
     * @return array list of coordinate, same order
     */
    private static List<ArrayList<Vertex>> getVertexes(Shape shp) {
        ArrayList<ArrayList<Vertex>> shapes = new ArrayList<ArrayList<Vertex>>();
        PathIterator it = shp.getPathIterator(null, FLATNESS);
        ArrayList<Vertex> vertexes = new ArrayList<Vertex>();
        double coords[] = new double[6];

        Vertex v;

        // Want a direct access to coordinates !!!
        while (!it.isDone()) {
            int type = it.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_CLOSE:
                    shapes.add(vertexes);
                    vertexes = new ArrayList<Vertex>();
                    break;
                case PathIterator.SEG_QUADTO:
                case PathIterator.SEG_CUBICTO:
                    break;
                case PathIterator.SEG_LINETO:
                case PathIterator.SEG_MOVETO:
                    v = new Vertex(coords[0], coords[1]);
                    if (vertexes.size() > 0) {
                        if (!v.equals(vertexes.get(vertexes.size() - 1))) {
                            vertexes.add(v);
                        }
                    } else {
                        vertexes.add(v);
                    }
                    break;
                default:
                    break;

            }
            it.next();
        }

        if (vertexes.size() > 1) {
            shapes.add(vertexes);
        }

        return shapes;
    }

    /**
     * Remove point which stands in the middle of a straight line
     * @param vertexes
     */
    private static void removeUselessVertex(List<Vertex> vertexes) {
        if (isClosed(vertexes)) {
            vertexes.remove(vertexes.size() - 1);
        }
    }

    /**
     * Compute offset vertexes
     * @param vertexes
     * @param offset
     * @return list of corresponding offseted vertex
     */
    private static List<Vertex> createOffsetVertexes(List<Vertex> vertexes, double offset, boolean closed) {
        int i;
        ArrayList<Vertex> offseted = new ArrayList<Vertex>();
        double absOffset = Math.abs(offset);
        double gamma;
        double theta = Math.PI / 2;
        for (i = 0; i < vertexes.size(); i++) {
            if (i == 0 && !closed) {
                // First point (unclosed path case)
                Vertex v = vertexes.get(i);
                Vertex vP1 = vertexes.get(i + 1);
                gamma = Math.atan2(vP1.y - v.y, vP1.x - v.x) + theta;
                Vertex ov = new Vertex(v.x - Math.cos(gamma) * offset, v.y - Math.sin(gamma) * offset);
                offseted.add(ov);
            } else if (i == vertexes.size() - 1 && !closed) {
                // Last point (unclosed path case)
                Vertex v = vertexes.get(i);
                Vertex vM1 = vertexes.get(i - 1);
                gamma = Math.atan2(v.y - vM1.y, v.x - vM1.x) + theta;
                offseted.add(new Vertex(v.x - Math.cos(gamma) * offset, v.y - Math.sin(gamma) * offset));
            } else {

                Vertex v = vertexes.get(i);
                Vertex vM1 = vertexes.get((i - 1 + vertexes.size()) % vertexes.size()); // TODO handle Closed path  Case ! (with modulo...)
                Vertex vP1 = vertexes.get((i + 1) % vertexes.size());

                double eP1X = vP1.x - v.x;
                double eP1Y = vP1.y - v.y;
                double eP1Norm = Math.sqrt(eP1X * eP1X + eP1Y * eP1Y);

                eP1X /= eP1Norm;
                eP1Y /= eP1Norm;

                double eX = v.x - vM1.x;
                double eY = v.y - vM1.y;
                double eNorm = Math.sqrt(eX * eX + eY * eY);

                eX /= eNorm;
                eY /= eNorm;


                double dxTmp;
                double dyTmp;


                // Determine gamma angle : law of cosines
                //a
                dxTmp = vP1.x - v.x;
                dyTmp = vP1.y - v.y;
                double aLength = Math.sqrt(dxTmp * dxTmp + dyTmp * dyTmp);

                //b
                dxTmp = v.x - vM1.x;
                dyTmp = v.y - vM1.y;
                double bLength = Math.sqrt(dxTmp * dxTmp + dyTmp * dyTmp);

                // c
                dxTmp = vP1.x - vM1.x;
                dyTmp = vP1.y - vM1.y;
                double cLength = Math.sqrt(dxTmp * dxTmp + dyTmp * dyTmp);

                gamma = Math.acos((cLength * cLength - aLength * aLength - bLength * bLength) / (-2 * aLength * bLength));

                // Skip straight segment
                if (Double.isNaN(gamma) || Math.abs(gamma - Math.PI) < 2 * ONE_DEG_IN_RAD || Math.abs(gamma) < 2 * ONE_DEG_IN_RAD) {
                    vertexes.remove(i);
                    i--;
                    continue;
                }

                double angleStatus = crossProduct(vM1.x, vM1.y, v.x, v.y, vP1.x, vP1.y) * offset;

                if (angleStatus < 0) {
                    // Interior
                    double dx = eP1X - eX;
                    double dy = eP1Y - eY;
                    double dNorm = Math.sqrt(dx * dx + dy * dy);

                    dx /= dNorm;
                    dy /= dNorm;

                    dx *= absOffset / Math.sin(gamma / 2);
                    dy *= absOffset / Math.sin(gamma / 2);

                    offseted.add(new Vertex(v.x + dx, v.y + dy));

                } else {
                    // Exterior
                    double dx = eX - eP1X;
                    double dy = eY - eP1Y;
                    double dNorm = Math.sqrt(dx * dx + dy * dy);

                    dx /= dNorm;
                    dy /= dNorm;

                    dx *= absOffset / Math.cos((Math.PI - gamma) / 2);
                    dy *= absOffset / Math.cos((Math.PI - gamma) / 2);

                    gamma = Math.atan2(v.y - vM1.y, v.x - vM1.x) + theta;

                    double quadx3 = v.x - Math.cos(gamma) * offset;
                    double quady3 = v.y - Math.sin(gamma) * offset;

                    gamma = Math.atan2(vP1.y - v.y, vP1.x - v.x) + theta;

                    double quadx1 = v.x - Math.cos(gamma) * offset;
                    double quady1 = v.y - Math.sin(gamma) * offset;

                    double deltaX = (quadx1 + quadx3) / 2 - v.x;
                    double deltaY = (quady1 + quady3) / 2 - v.y;

                    double h = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                    double quadx2;
                    double quady2;

                    if (Math.abs(h) < 0.001) {
                        deltaX = v.x - quadx1;
                        deltaY = v.y - quady1;
                        quadx2 = v.x - deltaY;
                        quady2 = v.y + deltaX;
                    } else {
                        quadx2 = v.x + deltaX * absOffset / h;
                        quady2 = v.y + deltaY * absOffset / h;
                    }

                    Vertex nv = new Vertex(v.x + dx, v.y + dy);

                    nv.setQuadTo(quadx3, quady3, quadx2, quady2, quadx1, quady1);

                    offseted.add(nv);

                }
            }
        }
        return offseted;
    }

    /**
     * Compute the distance between point (x3;y3) and the segment defined by (x1;y2) and (x2;y3).
     *
     *
     * @param x1 x coordinate of segment point 1
     * @param y1 y coordinate of segment point 1
     * @param x2 x coordinate of segment point 2
     * @param y2 y coordinate of segment point 2
     * @param x3 x coordinate of the point
     * @param y3 y coordinate of the point
     * @return
     */
    private static double getDistanceFromSegment(double x1, double y1, double x2, double y2, double x3, double y3) {

        double p4x = x3 + (y2 - y1);
        double p4y = y3 - (x2 - x1);

        // Segment length
        double h = Math.sqrt((x1 - x2) * (x1 - x2) + (y2 - y1) * (y2 - y1));

        // Case 1 : the minimum length is perpendicular to the segment
        if (crossProduct(x3, y3, p4x, p4y, x1, y1) * crossProduct(x3, y3, p4x, p4y, x2, y2) < 0) {
            return Math.abs(crossProduct(x1, y1, x2, y2, x3, y3) / h);
        } else {
            double d1 = (x1 - x3) * (x1 - x3) + (y1 - y3) * (y1 - y3);
            double d2 = (x2 - x3) * (x2 - x3) + (y2 - y3) * (y2 - y3);
            double d3 = Math.min(d1, d2);
            return Math.sqrt(d3);
        }
    }

    /**
     * According to offseted and original vertexes. compoute edge status
     * @param vertexes
     * @param offsetVertexes
     * @param offset
     * @param closed
     * @return
     */
    private static List<Edge> computeEdges(List<Vertex> vertexes, List<Vertex> offsetVertexes, double offset, boolean closed) {
        ArrayList<Edge> offstedEdges = new ArrayList<Edge>();

        int i;

        double absOffset = Math.abs(offset);


        for (i = 0; i < vertexes.size(); i++) {

            if (i < vertexes.size() - 1 || closed) {
                int j = (i + 1) % vertexes.size();
                Vertex v1 = vertexes.get(i);
                Vertex v2 = vertexes.get(j);
                Vertex ov1 = offsetVertexes.get(i);
                Vertex ov2 = offsetVertexes.get(j);

                Edge e = new Edge();
                e.mDir = (isSegIntersect(v1.x, v1.y, ov1.x, ov1.y, v2.x, v2.y, ov2.x, ov2.y) ? 0 : 1);
                offstedEdges.add(e);
            }
        }

        for (i = 0; i < offstedEdges.size() - (closed ? 0 : 1); i++) {
            Edge e = offstedEdges.get(i);
            if (e.mDir == 0) {
                e.mPos = 0;
            } else if (closed) {
                Vertex p31 = offsetVertexes.get(i);
                Vertex p32 = offsetVertexes.get((i + 1) % vertexes.size());

                double d1 = absOffset;
                double d2 = absOffset;
                int j;
                for (j = 0; j < offstedEdges.size() - (closed ? 0 : 1); j++) {
                    Vertex p1 = vertexes.get(j);
                    Vertex p2 = vertexes.get((j + 1) % vertexes.size());

                    double d = getDistanceFromSegment(p1.x, p1.y, p2.x, p2.y, p31.x, p31.y);
                    if (d < d1) {
                        d1 = d;
                    }

                    d = getDistanceFromSegment(p1.x, p1.y, p2.x, p2.y, p32.x, p32.y);

                    if (d < d2) {
                        d2 = d;
                    }


                    if (d1 < absOffset - 0.1 && d2 < absOffset - 0.1) {
                        e.mPos = 0;
                        break;
                    }
                }
            }
        }
        return offstedEdges;
    }

    /**
     * Transform list of vertex into Shape
     *
     * @param vertexes list of vertex
     * @param closed is the vertexes represent a ring ?
     * @return shape corresponding to vertexes
     */
    private static Shape createShapeFromVertexes(List<Vertex> vertexes, boolean closed) {
        if (vertexes.size() < 2) {
            return null;
        }

        Path2D.Double shp = new Path2D.Double();
        Vertex v1 = vertexes.get(0);

        if (v1.quadX1 != null) {
            double dx = v1.quadX2 - v1.x;
            double dy = v1.quadY2 - v1.y;

            if (ENABLE_QUAD && dx * dx + dy * dy > 9) {
                //if (dx * dx + dy * dy < -9) { // i.e. never  (a² + b² > 0) !
                shp.moveTo(v1.quadX1, v1.quadY1);
                shp.quadTo(v1.quadX2, v1.quadY2, v1.quadX3, v1.quadY3);
            } else {
                shp.moveTo(v1.x, v1.y);
            }

        } else {
            shp.moveTo(v1.x, v1.y);
        }

        int i;

        for (i = 1; i < vertexes.size(); i++) {
            Vertex v = vertexes.get(i);
            if (v.quadX1 != null) {
                double dx = v.quadX2 - v.x;
                double dy = v.quadY2 - v.y;

                if (ENABLE_QUAD && dx * dx + dy * dy > 9) {
                    //if (dx * dx + dy * dy < -9) { // i.e. never  (a² + b² > 0) !
                    shp.lineTo(v.quadX1, v.quadY1);
                    shp.quadTo(v.quadX2, v.quadY2, v.quadX3, v.quadY3);
                } else {
                    shp.lineTo(v.x, v.y);
                }
            } else {
                shp.lineTo(v.x, v.y);
            }
        }

        if (closed) {
            shp.closePath();
        }

        return shp;
    }

    /**
     * According to edges status and offseted vertexes, determine which vertexes will be part of the offset contour
     * @param edges
     * @param vertexes
     * @return
     */
    private static List<Vertex> computeRawLink(List<Edge> edges, List<Vertex> vertexes, boolean closed) {

        ArrayList<Vertex> rawLink = new ArrayList<Vertex>();

        ArrayList<Integer> offsetLinkList = new ArrayList<Integer>();
        ArrayList<Integer> bufferLinkList = new ArrayList<Integer>();

        int i;

        for (i = 0; i < edges.size(); i++) {
            //Edge e = edges.get(i);
            offsetLinkList.add(i);
        }

        int backward = 0;
        int forward = 0;

        int inDir = 0;
        int inPos = 0;

        if (!closed) {
            rawLink.add(vertexes.get(0));
        }

        if (edges.size() == 1) {
            rawLink.add(vertexes.get(0));
            rawLink.add(vertexes.get(1));
        }

        for (i = 0; i < offsetLinkList.size(); i++) {
            int id = offsetLinkList.get(i);
            Edge e = edges.get(id);
            if (e.is11()) {
                backward = i;
                break;
            } else {
                offsetLinkList.remove(i);
                offsetLinkList.add(id);
            }
        }

        while (backward < offsetLinkList.size()) {
            for (i = (backward + 1) % offsetLinkList.size(); i != backward; i = (i + 1) % offsetLinkList.size()) {
                int id = offsetLinkList.get(i);
                Edge e = edges.get(id);
                if (e.hasBeedProcessed()) {
                    break;
                }
                if (e.is11()) {
                    forward = i;
                    e.processed = true;
                    break;
                } else {
                    if (e.mDir == 0) {
                        inDir += 1;
                    }
                    if (e.mPos == 0) {
                        inPos += 1;
                    }
                    if (e.is10()) {
                        bufferLinkList.add(id);
                        bufferLinkList.add((id + 1) % vertexes.size());
                    }
                }
            }
            if (backward == forward) {
                break;
            }
            int bn = (offsetLinkList.get(backward) + 1) % vertexes.size();
            int fn = (offsetLinkList.get(forward) + 1) % vertexes.size();

            if (inDir == 0 && inPos == 0) {
                // Add backward edge 2nd point
                rawLink.add(vertexes.get(bn));
            } else if (inDir == 0 && inPos > 0) {
                for (Integer j : bufferLinkList) {
                    rawLink.add(vertexes.get(j));
                }
            } else if (inDir >= 1) {
                Vertex v1 = vertexes.get(offsetLinkList.get(backward));
                Vertex v2 = vertexes.get(bn);
                Vertex v3 = vertexes.get(offsetLinkList.get(forward));
                Vertex v4 = vertexes.get(fn);
                Point2D.Double inter = getLineIntersection(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y);

                if (inter != null
                        && isPointOnSegement(v1.x, v1.y, v2.x, v2.y, inter.x, inter.y)
                        && isPointOnSegement(v3.x, v3.y, v4.x, v4.y, inter.x, inter.y)) {
                    // 3.1
                    Vertex nv = new Vertex(inter.x, inter.y);
                    rawLink.add(nv);
                } else {
                    // 3.2
                    for (int j = backward + 1; j < forward; j++) {
                        Vertex vn = vertexes.get(offsetLinkList.get(j));
                        Vertex vm = vertexes.get((offsetLinkList.get(j) + 1) % vertexes.size());

                        Point2D.Double i1 = getLineIntersection(v1.x, v1.y, v2.x, v2.y, vn.x, vn.y, vm.x, vm.y);
                        Point2D.Double i2 = getLineIntersection(vn.x, vn.y, vm.x, vm.y, v3.x, v3.y, v4.x, v4.y);

                        if (i1 != null && i2 != null
                                && isPointOnSegement(v1.x, v1.y, v2.x, v2.y, i1.x, i1.y)
                                && isPointOnSegement(v3.x, v3.y, v4.x, v3.y, i2.x, i2.y)) {
                            //rawLink.add(new Vertex(i1.x, i1.y));
                            //rawLink.add(new Vertex(i2.x, i2.y));

                            Vertex nv = new Vertex(i1.x, i1.y);
                            rawLink.add(nv);
                            nv = new Vertex(i2.x, i2.y);
                            rawLink.add(nv);
                        }
                    }
                }
            }

            backward = forward;
            inDir = 0;
            inPos = 0;
            bufferLinkList.clear();
        }

        return rawLink;
    }

    private static boolean isPointOnSegement(double x1, double y1, double x2, double y2, double x3, double y3) {

        double dist12 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        double dist13 = (x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1);
        double dist23 = (x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2);

        return Math.abs(dist12 - dist13 - dist23) < 0.01;

        /*if (Math.abs(x1 - x2) < 0.0001) {
        // segment is vertical, check against y value
        
        double ymin = Math.min(y1, y2);
        double ymax = Math.max(y1, y2);
        return y3 > ymin && y3 < ymax;
        } else {
        // check against X values
        double xmin = Math.min(x1, x2);
        double xmax = Math.max(x1, x2);
        return x3 > xmin && x3 < xmax;
        }*/
    }

    private static boolean isClosed(List<Vertex> vertexes) {
        return vertexes.get(0).equals(vertexes.get(vertexes.size() - 1));
    }
    
    private static List<Shape> contourParallelShape(Shape shp, double offset) {

        List<ArrayList<Vertex>> shapes = getVertexes(shp);
        ArrayList<Shape> rawShapes = new ArrayList<Shape>();
        for (List<Vertex> vertexes : shapes) {
            boolean closed = isClosed(vertexes);
            removeUselessVertex(vertexes);
            //if (closed) {
            //    normalize(vertexes);
            //}

            List<Vertex> offsetVertexes = createOffsetVertexes(vertexes, offset, closed);
            if (offsetVertexes.size() < 2) {
                LOGGER.error(I18N.tr("Unable to compute perpendicular offset"));
                return rawShapes;
            }

            List<Edge> edges = computeEdges(vertexes, offsetVertexes, offset, closed);
            List<Vertex> rawLink = computeRawLink(edges, offsetVertexes, closed);
            Shape finalShape = createShapeFromVertexes(rawLink, closed);
            if (finalShape != null) {
                rawShapes.add(finalShape);
            }
        }

        return rawShapes;
        //return createShapeFromVertexes(rawLink, closed);

        //return null;
    }

    /**
     * Compute cross product :
     *
     *           o(x2,y2)
     *          /
     * cp > 0  /
     *        /    cp < 0
     *       /
     *      /
     *     o (x1, y1)
     *
     * @param x1 seg first point x coord
     * @param y1 seg first point y coord
     * @param x2 seg second point x coord
     * @param y2 seg second point y coord
     * @param x3 the point to check x coord
     * @param y3 the point to check y coord
     *
     */
    static double crossProduct(double x1, double y1, double x2, double y2, double x3, double y3) {
        return (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1);
    }

    /**
     * Is (x1y1)(x2y2) (strictly) intersects (x3y3)(x4y4) ?
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     * @return
     */
    private static boolean isSegIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double cp1, cp2, cp3, cp4;

        cp1 = crossProduct(x1, y1, x2, y2, x3, y3);
        cp2 = crossProduct(x1, y1, x2, y2, x4, y4);
        cp3 = crossProduct(x3, y3, x4, y4, x1, y1);
        cp4 = crossProduct(x3, y3, x4, y4, x2, y2);

        return (cp1 * cp2 < 0 && cp3 * cp4 < 0);
    }

    private static Point2D.Double computeSegmentIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        /*
        double cp1, cp2, cp3, cp4;
        
        cp1 = crossProduct(x1, y1, x2, y2, x3, y3);
        cp2 = crossProduct(x1, y1, x2, y2, x4, y4);
        cp3 = crossProduct(x3, y3, x4, y4, x1, y1);
        cp4 = crossProduct(x3, y3, x4, y4, x2, y2);
         */

        if (isSegIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
            // 1 intersection point !
            return getLineIntersection(x1, y1, x2, y2, x3, y3, x4, y4);
        } else {
            // none or many intersection point !
            return null;
        }
    }

    /**
     * Compute intersection between two line.
     * The first line is passing by points (x1,y1) & (x2, y2). The second by (x3,y3) and (x4,y4)
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     * @return null if lines are parallel, the intersection point otherwise
     */
    private static Point2D.Double getLineIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {

        double denom1 = x2 - x1;
        double denom2 = x4 - x3;

        if (Math.abs(denom1) < 0.0001) {
            denom1 = 0;
        }
        if (Math.abs(denom2) < 0.0001) {
            denom2 = 0;
        }

        double a1 = (y2 - y1) / denom1;
        double a2 = (y4 - y3) / denom2;

        double b1 = y2 - a1 * x2;
        double b2 = y4 - a2 * x4;

        double x;
        double y;


        if (Double.isInfinite(a1) && Double.isInfinite(a2)) {
            return null;
        } else if (Double.isInfinite(a1)) {
            x = x1;
            y = a2 * x + b2;
        } else if (Double.isInfinite(a2)) {
            x = x3;
            y = a1 * x + b1;
        } else {
            x = (b2 - b1) / (a1 - a2);
            y = a1 * x + b1;
            if (Double.isNaN(x) || Double.isInfinite(x)) {
                return null;
            }
        }
        return new Point2D.Double(x, y);
    }

    /**
     * REF : http://www.springerlink.com/content/nx71u48201887310/fulltext.pdf 
     * @param shp
     * @param offset
     * @return 
     */
    public static List<Shape> perpendicularOffset(Shape shp, double offset) {

        return contourParallelShape(shp, offset);
        //return perpendicularOffsetForLine(shp, offset);
    }

    public static Line2D.Double intersection(Line2D.Double line, Rectangle2D.Double bounds) {
        //line.x1, line.y1, line.x2, line.y2
        Point2D.Double bottom = computeSegmentIntersection(line.x1, line.y1, line.x2, line.y2,
                bounds.getMinX(), bounds.getMaxY(), bounds.getMaxX(), bounds.getMaxY());

        Point2D.Double right = computeSegmentIntersection(line.x1, line.y1, line.x2, line.y2,
                bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxX(), bounds.getMinY());

        Point2D.Double top = computeSegmentIntersection(line.x1, line.y1, line.x2, line.y2,
                bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMinY());

        Point2D.Double left = computeSegmentIntersection(line.x1, line.y1, line.x2, line.y2,
                bounds.getMinX(), bounds.getMinY(), bounds.getMinX(), bounds.getMaxY());

        ArrayList<Point2D.Double> pts = new ArrayList<Point2D.Double>();
        if (bottom != null) {
            pts.add(bottom);
        }

        if (right != null) {
            pts.add(right);
        }

        if (top != null) {
            pts.add(top);
        }

        if (left != null) {
            pts.add(left);
        }

        if (pts.size() != 2) {
            return null;
        } else {
            return new Line2D.Double(pts.get(0), pts.get(1));
        }
    }

    /**
     * Look for a unique Geometry attribute within the data source
     * @param sds the data source to search geometry attribute in.
     *
     * @return field id of the geometry attribute
     * @throws DriverException If a problem occurs with the data source
     * @throws ParameterException Is thrown if the number of geometry attribute isn't one
     */
    public static int getGeometryFieldId(DataSource sds) throws DriverException, ParameterException {
        Metadata metadata = sds.getMetadata();

        int fieldId = -1;
        // /MetadataUtilities (i.e sds.getGeometry(..) return the first encountered geometry
        // With SE, make sure sds only contains one geometry
        StringBuffer available = new StringBuffer();
        for (int i = 0; i < metadata.getFieldCount(); i++) {
            int typeCode = metadata.getFieldType(i).getTypeCode();
            if ((typeCode & Type.GEOMETRY) != 0 || typeCode == Type.RASTER) {
                if (fieldId == -1) { // -1 means not found yet
                    fieldId = i;
                } else {
                    fieldId = -2; // special value means ambigous
                    available.append(" ");
                    available.append(metadata.getFieldName(i));
                }
            }
        }

        if (fieldId == -2) {
            throw new ParameterException("Reference to geometry attribute is ambigous. Available are :" + available);
        }
        if (fieldId == -1) {
            throw new ParameterException("This data source doesn't contains any geometry");
        }

        return fieldId;
    }

    public static Geometry clipToExtent(Geometry theGeom, Envelope extent) {
        GeometryFactory geometryFactory = new GeometryFactory();

        Envelope incExtent = new Envelope(extent);

        incExtent.expandBy(extent.getWidth() / 10, extent.getHeight() / 10);

        Geometry geometry = theGeom.intersection(geometryFactory.toGeometry(extent));

        if (geometry.isEmpty()) {
            return null;
        } else {
            return geometry;
        }
    }
}
