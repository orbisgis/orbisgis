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
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.renderer.se.common;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Path2D;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author maxence
 */
public class ShapeHelper {

    public static final double _0_0175 = Math.PI / 180.0;
    private static final boolean _DEBUG_ = false;

    /**
     * Compute the perimeter of the shape
     * @todo test and move
     * @param area
     * @return
     */
    public static double getAreaPerimeterLength(Shape area) {
        PathIterator it = area.getPathIterator(null, 1);

        double coords[] = new double[6];

        double p = 0.0;

        Double x1 = null;
        Double y1 = null;

        while (!it.isDone()) {
            it.currentSegment(coords);

            double x2 = coords[0];
            double y2 = coords[1];

            if (x1 != null && y1 != null) {
                double xx, yy;
                xx = x2 - x1;
                yy = y2 - y1;
                p += Math.sqrt(xx * xx + yy * yy);
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
    public static ArrayList<Shape> splitLine(Shape line, double firstLineLength) {

        ArrayList<Shape> shapes = new ArrayList<Shape>();

        PathIterator it = line.getPathIterator(null, 1);
        double coords[] = new double[6];

        Double x1 = null;
        Double y1 = null;

        Path2D.Double segment = new Path2D.Double();
        double p = 0.0;
        double p1;

        it.currentSegment(coords);

        x1 = coords[0];
        y1 = coords[1];
        segment.moveTo(x1, y1);

        it.next();

        double x2 = 0.0;
        double y2 = 0.0;

        boolean first = true;

        while (!it.isDone()) {
            it.currentSegment(coords);

            x2 = coords[0];
            y2 = coords[1];

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
        segment.lineTo(x1, y1);

        shapes.add(segment);

        return shapes;
    }

    /**
     * Split a linear feature in the specified number of part, which have all the same length
     * @param line  the line to split
     * @param nbPart the number of part to create
     * @return list of equal-length segment
     */
    public static ArrayList<Shape> splitLine(Shape line, int nbPart) {
        ArrayList<Shape> shapes = new ArrayList<Shape>();
        double perimeter = getLineLength(line);

        double segLength = perimeter / nbPart;

        PathIterator it = line.getPathIterator(null, 1);
        double coords[] = new double[6];

        Double x1 = null;
        Double y1 = null;

        Path2D.Double segment = new Path2D.Double();
        double p = 0.0;
        double p1;

        it.currentSegment(coords);

        x1 = coords[0];
        y1 = coords[1];
        segment.moveTo(x1, y1);

        it.next();

        double x2 = 0.0;
        double y2 = 0.0;

        while (!it.isDone()) {
            it.currentSegment(coords);

            x2 = coords[0];
            y2 = coords[1];

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
        segment.lineTo(x1, y1);

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

        Point2D.Double pt = new Point2D.Double(x1 + distance * (x2 - x1) / length, y1 + distance * (y2 - y1) / length);

        return pt;
    }

    /**
     * Go along a line shape and return the point at the specified distance from the beginning of the line
     * @param shp  the line
     * @param distance
     * @return point representing the point at the linear length distance
     */
    public static Point2D.Double getPointAt(Shape shp, double distance) {
        PathIterator it = shp.getPathIterator(null, 1);

        double coords[] = new double[6];

        double p = 0.0;

        Double x1 = null;
        Double y1 = null;

        double x2 = 0.0;
        double y2 = 0.0;
        double segLength = 0.0;

        while (!it.isDone()) {
            it.currentSegment(coords);

            x2 = coords[0];
            y2 = coords[1];

            if (x1 != null && y1 != null) {
                double xx, yy;
                xx = x2 - x1;
                yy = y2 - y1;
                segLength = Math.sqrt(xx * xx + yy * yy);
                p += segLength;

                if (p > distance) {
                    break;
                }
            }

            x1 = x2;
            y1 = y2;

            it.next();
        }

        return getPointAt(x1, y1, x2, y2, segLength - (p - distance));
    }

    private static Polygon perpendicularOffsetForArea() {
        return null;
    }

    private static class Vertex {

        double x;
        double y;
        Double quadX1;
        Double quadY1;
        Double quadX2;
        Double quadY2;
        Double quadX3;
        Double quadY3;

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

        public boolean equals(Vertex v) {
            return Math.abs(v.x - this.x) < 0.0001 && Math.abs(v.y - this.y) < 0.0001;
        }

        @Override
        public String toString() {
            //if (quadX1 != null) {
            //    return "" + x + ";" + y + "   " + quadX1 + ";" + quadY2 + "  " + quadX2 + ";" + quadY2 + "   " + quadX3 + ";" + quadY3;
            //} else {
                return "" + x + ";" + y;
            //}
        }
    }

    private static class Edge {

        int m_pos;
        int m_dir;
        boolean processed;

        public Edge() {
            this.processed = false;
            m_pos = 1;
            m_dir = 0;
        }

        public boolean hasBeedProcessed() {
            return processed;
        }

        public boolean is11() {
            return m_pos == 1 && m_dir == 1;
        }

        public boolean is10() {
            return m_pos == 0 && m_dir == 1;
        }

        public boolean isUnfeasible() {
            return m_pos == 0;
        }

        @Override
        public String toString() {
            return "" + m_dir + m_pos;
        }
    }

    /**
     * Convert Shape into a list of coordinates.
     * Will also convert curves to set of segment
     * @param shp the shape to convert
     * @return array list of coordinate, same order
     */
    private static ArrayList<ArrayList<Vertex>> getVertexes(Shape shp) {

        ArrayList<ArrayList<Vertex>> shapes = new ArrayList<ArrayList<Vertex>>();

        PathIterator it = shp.getPathIterator(null, 0.2);

        ArrayList<Vertex> vertexes = new ArrayList<Vertex>();

        if (_DEBUG_) {
            System.out.println("New subshape:");
        }
        double coords[] = new double[6];

        Vertex v;

        // Want a direct access to coordinates !!!
        while (!it.isDone()) {
            int type = it.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_CLOSE:
                    if (_DEBUG_) {
                        System.out.println("Close;");
                    }
                    shapes.add(vertexes);
                    vertexes = new ArrayList<Vertex>();
                    break;
                case PathIterator.SEG_QUADTO:
                case PathIterator.SEG_CUBICTO:
                    break;
                case PathIterator.SEG_LINETO:
                case PathIterator.SEG_MOVETO:
                    v = new Vertex(coords[0], coords[1]);
                    if (_DEBUG_) {
                        System.out.println("LineTo;" + v);
                    }
                    if (vertexes.size() > 0) {
                        if (!v.equals(vertexes.get(vertexes.size() - 1))) {
                            vertexes.add(v);
                        }
                    } else {
                        vertexes.add(v);
                    }
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
    private static void removeUselessVertex(ArrayList<Vertex> vertexes) {
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
    private static ArrayList<Vertex> createOffsetVertexes(ArrayList<Vertex> vertexes, double offset, boolean closed) {

        int i;

        ArrayList<Vertex> offseted = new ArrayList<Vertex>();

        double absOffset = Math.abs(offset);

        double gamma;
        double theta = Math.PI / 2;

        for (i = 0; i < vertexes.size(); i++) {
            if (i == 0 && !closed) {
                // First point (unclosed path case
                Vertex v = vertexes.get(i);
                Vertex v_p1 = vertexes.get(i + 1);

                gamma = Math.atan2(v_p1.y - v.y, v_p1.x - v.x) + theta;

                Vertex ov = new Vertex(v.x - Math.cos(gamma) * offset, v.y - Math.sin(gamma) * offset);
                offseted.add(ov);

            } else if (i == vertexes.size() - 1 && !closed) {
                // Last point (unclosed path case)

                Vertex v = vertexes.get(i);
                Vertex v_m1 = vertexes.get(i - 1);

                gamma = Math.atan2(v.y - v_m1.y, v.x - v_m1.x) + theta;

                offseted.add(new Vertex(v.x - Math.cos(gamma) * offset, v.y - Math.sin(gamma) * offset));
            } else {

                Vertex v = vertexes.get(i);
                Vertex v_m1 = vertexes.get((i - 1 + vertexes.size()) % vertexes.size()); // TODO handle Closed path  Case ! (with modulo...)
                Vertex v_p1 = vertexes.get((i + 1) % vertexes.size());

                double e_p1_x = v_p1.x - v.x;
                double e_p1_y = v_p1.y - v.y;
                double e_p1_norm = Math.sqrt(e_p1_x * e_p1_x + e_p1_y * e_p1_y);

                e_p1_x /= e_p1_norm;
                e_p1_y /= e_p1_norm;

                double e_x = v.x - v_m1.x;
                double e_y = v.y - v_m1.y;
                double e_norm = Math.sqrt(e_x * e_x + e_y * e_y);

                e_x /= e_norm;
                e_y /= e_norm;


                double dx_tmp;
                double dy_tmp;


                // Determine gamma angle : law of cosines
                //a
                dx_tmp = v_p1.x - v.x;
                dy_tmp = v_p1.y - v.y;
                double a_length = Math.sqrt(dx_tmp * dx_tmp + dy_tmp * dy_tmp);

                //b
                dx_tmp = v.x - v_m1.x;
                dy_tmp = v.y - v_m1.y;
                double b_length = Math.sqrt(dx_tmp * dx_tmp + dy_tmp * dy_tmp);

                // c
                dx_tmp = v_p1.x - v_m1.x;
                dy_tmp = v_p1.y - v_m1.y;
                double c_length = Math.sqrt(dx_tmp * dx_tmp + dy_tmp * dy_tmp);

                gamma = Math.acos((c_length * c_length - a_length * a_length - b_length * b_length) / (-2 * a_length * b_length));

                if (_DEBUG_) {
                    System.out.println("Gamma is : " + gamma / _0_0175);
                }

                // Skip straight segment
                if (Double.isNaN(gamma) || Math.abs(gamma - Math.PI) < 2 * _0_0175 || Math.abs(gamma) < 2 * _0_0175) {
                    vertexes.remove(i);
                    i--;
                    continue;
                }

                double angle_status = crossProduct(v_m1.x, v_m1.y, v.x, v.y, v_p1.x, v_p1.y) * offset;
                if (_DEBUG_) {
                    System.out.println("Status is: " + angle_status);
                }

                if (angle_status < 0) {
                    // Interior
                    if (_DEBUG_) {
                        System.out.println("Interior:");
                    }
                    double dx = e_p1_x - e_x;
                    double dy = e_p1_y - e_y;
                    double d_norm = Math.sqrt(dx * dx + dy * dy);

                    dx /= d_norm;
                    dy /= d_norm;

                    dx *= absOffset / Math.sin(gamma / 2);
                    dy *= absOffset / Math.sin(gamma / 2);

                    offseted.add(new Vertex(v.x + dx, v.y + dy));

                } else {
                    // Exterior
                    if (_DEBUG_) {
                        System.out.println("Exterior:");
                    }
                    double dx = e_x - e_p1_x;
                    double dy = e_y - e_p1_y;
                    double d_norm = Math.sqrt(dx * dx + dy * dy);

                    dx /= d_norm;
                    dy /= d_norm;

                    dx *= absOffset / Math.cos((Math.PI - gamma) / 2);
                    dy *= absOffset / Math.cos((Math.PI - gamma) / 2);

                    gamma = Math.atan2(v.y - v_m1.y, v.x - v_m1.x) + theta;

                    double quadx3 = v.x - Math.cos(gamma) * offset;
                    double quady3 = v.y - Math.sin(gamma) * offset;

                    gamma = Math.atan2(v_p1.y - v.y, v_p1.x - v.x) + theta;

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
    private static ArrayList<Edge> computeEdges(ArrayList<Vertex> vertexes, ArrayList<Vertex> offsetVertexes, double offset, boolean closed) {
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
                e.m_dir = (isSegIntersect(v1.x, v1.y, ov1.x, ov1.y, v2.x, v2.y, ov2.x, ov2.y) ? 0 : 1);
                offstedEdges.add(e);
            }
        }

        for (i = 0; i < offstedEdges.size() - (closed ? 0 : 1); i++) {
            Edge e = offstedEdges.get(i);
            if (e.m_dir == 0) {
                e.m_pos = 0;
            } else if (closed) {
                Vertex p31 = offsetVertexes.get(i);
                Vertex p32 = offsetVertexes.get((i + 1) % vertexes.size());

                double d1 = absOffset;
                double d2 = absOffset;
                int j;
                if (_DEBUG_) {
                    System.out.println("NEW EDGE: " + i);
                }
                for (j = 0; j < offstedEdges.size() - (closed ? 0 : 1); j++) {
                    Vertex p1 = vertexes.get(j);
                    Vertex p2 = vertexes.get((j + 1) % vertexes.size());

                    double d = getDistanceFromSegment(p1.x, p1.y, p2.x, p2.y, p31.x, p31.y);
                    if (d < d1) {
                        if (_DEBUG_) {
                            System.out.println("New shortest : " + " pt" + i + " edge" + j);
                        }
                        d1 = d;
                    }

                    d = getDistanceFromSegment(p1.x, p1.y, p2.x, p2.y, p32.x, p32.y);

                    if (d < d2) {
                        if (_DEBUG_) {
                            System.out.println("New shortest : " + " pt" + (i + 1) + " edge" + j);
                        }
                        d2 = d;
                    }


                    if (d1 < absOffset - 0.1 && d2 < absOffset - 0.1) {
                        if (_DEBUG_) {
                            System.out.println("Invalid !: " + d1 + ";" + d2 + ";" + absOffset);
                        }
                        e.m_pos = 0;
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
    private static Shape createShapeFromVertexes(ArrayList<Vertex> vertexes, boolean closed) {
        if (vertexes.size() < 2) {
            return null;
        }

        Path2D.Double shp = new Path2D.Double();
        Vertex v1 = vertexes.get(0);

        if (v1.quadX1 != null){
                double dx = v1.quadX2 - v1.x;
                double dy = v1.quadY2 - v1.y;

                if (dx * dx + dy * dy > 9) {
                    //if (dx * dx + dy * dy < -9) { // i.e. never  (a² + b² > 0) !
                    shp.moveTo(v1.quadX1, v1.quadY1);
                    shp.quadTo(v1.quadX2, v1.quadY2, v1.quadX3, v1.quadY3);
                } else {
                    shp.moveTo(v1.x, v1.y);
                }

        } else{
            shp.moveTo(v1.x, v1.y);
        }

        int i;

        for (i = 1; i < vertexes.size(); i++) {
            Vertex v = vertexes.get(i);
            if (v.quadX1 != null) {
                double dx = v.quadX2 - v.x;
                double dy = v.quadY2 - v.y;

                //if (dx * dx + dy * dy > 9) {
                    //if (dx * dx + dy * dy < -9) { // i.e. never  (a² + b² > 0) !
                //    shp.lineTo(v.quadX1, v.quadY1);
                //    shp.quadTo(v.quadX2, v.quadY2, v.quadX3, v.quadY3);
                //} else {
                    shp.lineTo(v.x, v.y);
                //}
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
    private static ArrayList<Vertex> computeRawLink(ArrayList<Edge> edges, ArrayList<Vertex> vertexes, boolean closed) {

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

        int in_dir = 0;
        int in_pos = 0;

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
            if (_DEBUG_) {
                System.out.println("Edge is: " + e);
            }
            if (e.is11()) {
                backward = i;
                break;
            } else {
                offsetLinkList.remove(i);
                offsetLinkList.add(id);
            }
        }

        while (backward < offsetLinkList.size()) {

            if (_DEBUG_) {
                System.out.println("Backward edge is " + offsetLinkList.get(backward));
            }
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
                    if (e.m_dir == 0) {
                        in_dir += 1;
                    }
                    if (e.m_pos == 0) {
                        in_pos += 1;
                    }
                    if (e.is10()) {
                        bufferLinkList.add(id);
                        bufferLinkList.add((id + 1) % vertexes.size());
                    }
                }
            }

            if (_DEBUG_) {
                System.out.println("Forward edge is " + offsetLinkList.get(forward));
            }


            if (backward == forward) {
                break;
            }

            if (_DEBUG_) {
                System.out.println("Status : " + edges.get(offsetLinkList.get(backward)) + ";" + edges.get(offsetLinkList.get(forward)));
                System.out.println(" in_dir: " + in_dir + "    in_pos: " + in_pos);
            }

            int bn = (offsetLinkList.get(backward) + 1) % vertexes.size();
            int fn = (offsetLinkList.get(forward) + 1) % vertexes.size();

            if (in_dir == 0 && in_pos == 0) {
                // Add backward edge 2nd point
                if (_DEBUG_) {
                    System.out.println("Add " + vertexes.get(bn));
                }
                rawLink.add(vertexes.get(bn));
            } else if (in_dir == 0 && in_pos > 0) {
                for (Integer j : bufferLinkList) {
                    rawLink.add(vertexes.get(j));
                    if (_DEBUG_) {
                        System.out.println("Add " + vertexes.get(j));
                    }
                }
            /*} else if (in_dir == 1) {
                Vertex v1 = vertexes.get(offsetLinkList.get(backward));
                Vertex v2 = vertexes.get(bn);
                Vertex v3 = vertexes.get(offsetLinkList.get(forward));
                Vertex v4 = vertexes.get(fn);

                Point2D.Double inter = getLineIntersection(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y);

                if (inter != null) {
                    Vertex nv = new Vertex(inter.x, inter.y);
                    rawLink.add(nv);
                    if (_DEBUG_) {
                        System.out.println("Add " + nv);
                    }
                } else {
                    if (_DEBUG_) {
                        System.out.println("Skip");
                    }
                }*/
            } else if (in_dir >= 1) {
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
                    if (_DEBUG_) {
                        System.out.println("Add3.1 " + nv);
                    }
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
                            if (_DEBUG_) {
                                System.out.println("Add3.2a " + nv);
                            }

                            nv = new Vertex(i2.x, i2.y);
                            rawLink.add(nv);
                            if (_DEBUG_) {
                                System.out.println("Add3.2b " + nv);
                            }
                        }
                    }
                }
            }

            backward = forward;
            in_dir = 0;
            in_pos = 0;
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

    private static boolean isClosed(ArrayList<Vertex> vertexes) {
        return vertexes.get(0).equals(vertexes.get(vertexes.size() - 1));
    }

    private static void normalize(ArrayList<Vertex> vertexes) {
        double maxY = Double.NEGATIVE_INFINITY;
        int id = -1;
        int i = 0;

        if (_DEBUG_) {
            System.out.println("Normalize");
        }

        for (Vertex v : vertexes) {
            if (v.y > maxY) {
                maxY = v.y;
                id = i;
            }

            if (_DEBUG_) {
                System.out.println(v);
            }

            i++;
        }

        Vertex p = vertexes.get(id);
        Vertex pp1 = vertexes.get((id + 1) % vertexes.size());
        Vertex pm1 = vertexes.get((id - 1 + vertexes.size()) % vertexes.size());

        if (_DEBUG_) {
            System.out.println("----");
            System.out.println(pm1);
            System.out.println(p);
            System.out.println(pp1);
        }

        double px = (pp1.x + pm1.x) / 2;
        double py = (pp1.y + pm1.y) / 2;

        px = p.x - 2 * (p.x - px);
        py = p.y - 2 * (p.y - py);

        if (_DEBUG_) {
            System.out.println(px + ";" + py);
        }
        double cp = crossProduct(p.x, p.y, px, py, pp1.x, pp1.y);


        if (_DEBUG_) {
            System.out.println("CROSSPRODUCT: " + cp);
        }

        if (cp > 0) {
            Collections.reverse(vertexes);
        }
    }

    private static ArrayList<Shape> contourParallelShape(Shape shp, double offset) {

        ArrayList<ArrayList<Vertex>> shapes = getVertexes(shp);
        ArrayList<Shape> rawShapes = new ArrayList<Shape>();

        Rectangle2D bounds2D = shp.getBounds2D();

        if (Math.min(bounds2D.getWidth(), bounds2D.getHeight()) < Math.abs(offset)){
            return rawShapes;
        }

        for (ArrayList<Vertex> vertexes : shapes) {
            if (_DEBUG_) {
                System.out.println("Original vertexes: ");
                for (Vertex v : vertexes) {
                    System.out.println(v);
                }
                System.out.println(".....................");
            }

            System.out.println ("Num Vertexes: " + vertexes.size());
            boolean closed = isClosed(vertexes);

            removeUselessVertex(vertexes);

            if (closed) {
                normalize(vertexes);
            }

            System.out.println ("Num Cleaned Vertexes: " + vertexes.size());

            ArrayList<Vertex> offsetVertexes = createOffsetVertexes(vertexes, offset, closed);

            if (_DEBUG_) {
                System.out.println("Cleaned vertexes: ");
                for (Vertex v : vertexes) {
                    System.out.println(v);
                }

                System.out.println("Offset vertexes: ");
                for (Vertex v : offsetVertexes) {
                    System.out.println(v);
                }
            }

            System.out.println ("Num offseted Vertexes: " + offsetVertexes.size());

            ArrayList<Edge> edges = computeEdges(vertexes, offsetVertexes, offset, closed);

            if (_DEBUG_) {
                System.out.println("Edges: ");
                for (Edge e : edges) {
                    System.out.println(e);
                }
            }

            System.out.println ("Num edges: " + edges.size());

            ArrayList<Vertex> rawLink = computeRawLink(edges, offsetVertexes, closed);

            if (_DEBUG_) {
                System.out.println("Raw Link: ");
                for (Vertex v : rawLink) {
                    System.out.println(v);
                }
            }

            rawShapes.add(createShapeFromVertexes(rawLink, closed));
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
     */
    static double crossProduct(double x1, double y1, double x2, double y2, double x3, double y3) {
        return (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1);
    }

    private static boolean isSegIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double cp1, cp2, cp3, cp4;

        cp1 = crossProduct(x1, y1, x2, y2, x3, y3);
        cp2 = crossProduct(x1, y1, x2, y2, x4, y4);
        cp3 = crossProduct(x3, y3, x4, y4, x1, y1);
        cp4 = crossProduct(x3, y3, x4, y4, x2, y2);

        return (cp1 * cp2 < 0 && cp3 * cp4 < 0);
    }

    private static Point2D.Double computeSegmentIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double cp1, cp2, cp3, cp4;

        cp1 = crossProduct(x1, y1, x2, y2, x3, y3);
        cp2 = crossProduct(x1, y1, x2, y2, x4, y4);
        cp3 = crossProduct(x3, y3, x4, y4, x1, y1);
        cp4 = crossProduct(x3, y3, x4, y4, x2, y2);

        if (cp1 * cp2 < 0 && cp3 * cp4 < 0) {
            // 1 intersection point !
            return getLineIntersection(x1, y1, x2, y2, x3, y3, x4, y4);
        } else {
            // none or many intersection point !
            return null;
        }
    }

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

        if (_DEBUG_) {
            System.out.println(x1 + ";" + y1 + " --> " + x2 + ";" + y2);
            System.out.println(x3 + ";" + y3 + " --> " + x4 + ";" + y4);
            System.out.println("a1: " + a1);
            System.out.println("a2: " + a2);
        }


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
            if (_DEBUG_) {
                System.out.println("a1-a2:" + (a1 - a2));
                System.out.println("b1-b2:" + (b2 - b1));
            }
            y = a1 * x + b1;
            if (Double.isNaN(x) || Double.isInfinite(x)) {
                return null;
            }
        }

        if (_DEBUG_) {
            System.out.println(" intersection is: " + x + ";" + y);
        }

        return new Point2D.Double(x, y);
    }

    public static ArrayList<Shape> perpendicularOffset(Shape shp, double offset) {

        return contourParallelShape(shp, offset);
        //return perpendicularOffsetForLine(shp, offset);
    }
}
