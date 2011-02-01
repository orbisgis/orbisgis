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

import java.util.ArrayList;

/**
 *
 * @author maxence
 */
public class ShapeHelper {

	public static final double _0_0175 = Math.PI / 180.0;

	/**
	 * Compute the perimeter of the shape
	 * @todo test and move
	 * @param area
	 * @return
	 */
	public static double getAreaPerimeterLength(Shape area) {
		PathIterator it = area.getPathIterator(null);

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

		PathIterator it = line.getPathIterator(null);
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

		PathIterator it = line.getPathIterator(null);
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
		PathIterator it = shp.getPathIterator(null);

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

		public Vertex(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public boolean equals(Vertex v) {
			return Math.abs(v.x - this.x) < 0.0001 && Math.abs(v.y - this.y) < 0.0001;
		}

		@Override
		public String toString(){
			return "" + x + ";" + y;
		}
	}

	private static class Edge {

		int m_pos;
		int m_dir;

		boolean processed;

		public Edge(){
			this.processed = false;
			m_pos = 0;
			m_dir = 0;
		}

		public boolean hasBeedProcessed(){
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
		public String toString(){
			return "" + m_dir + m_pos;
		}
	}

	/**
	 * Convert Shape into a list of coordinates.
	 * Will also convert curves to set of segment
	 * @param shp the shape to convert
	 * @return array list of coordinate, same order
	 */
	private static ArrayList<Vertex> getVertexes(Shape shp) {
		ArrayList<Vertex> vertexes = new ArrayList<Vertex>();

		PathIterator it = shp.getPathIterator(null, 0.2);

		double coords[] = new double[6];


		// Want a direct access to coordinates !!!
		while (!it.isDone()) {
			it.currentSegment(coords);

			Vertex v = new Vertex(coords[0], coords[1]);

			if (vertexes.size() > 0) {
				if (!v.equals(vertexes.get(vertexes.size() - 1))) {
					vertexes.add(v);
				}
			} else {
				vertexes.add(v);
			}


			it.next();
		}

		return vertexes;
	}

	/**
	 * Remove point which stands in the middle of a straight line
	 * @param vertexes
	 */
	private static void removeUselessVertex(ArrayList<Vertex> vertexes) {
		if (isClosed(vertexes)){
			vertexes.remove(vertexes.size()-1);
		}
	}

	/**
	 * Compute the row offset vertexes
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

				Vertex ov = new Vertex(v.x + Math.cos(gamma) * absOffset, v.y + Math.sin(gamma) * absOffset);
				offseted.add(ov);

			} else if (i == vertexes.size() - 1 && ! closed) {
				// Last point (unclosed path case)

				Vertex v = vertexes.get(i);
				Vertex v_m1 = vertexes.get(i - 1);

				gamma = Math.atan2(v.y - v_m1.y, v.x - v_m1.x) + theta;

				offseted.add(new Vertex(v.x + Math.cos(gamma) * absOffset, v.y + Math.sin(gamma) * absOffset));
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

				System.out.println("Gamma is : " + gamma / _0_0175);

				if (Math.abs(gamma - Math.PI) < 0.0001){
					vertexes.remove(i);
					i--;
					continue;
				}

				double angle_status = crossProduct(v_m1.x, v_m1.y, v.x, v.y, v_p1.x, v_p1.y) * offset;

				if (angle_status < 0) {
					// Interior
					System.out.println("Interior:");
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
					System.out.println("Exterior:");
					double dx = e_x - e_p1_x;
					double dy = e_y - e_p1_y;
					double d_norm = Math.sqrt(dx * dx + dy * dy);

					dx /= d_norm;
					dy /= d_norm;

					dx *= absOffset / Math.cos((Math.PI - gamma) / 2);
					dy *= absOffset / Math.cos((Math.PI - gamma) / 2);

					offseted.add(new Vertex(v.x + dx, v.y + dy));

				}
			}
		}
		return offseted;
	}

	private static ArrayList<Edge> computeEdges(ArrayList<Vertex> vertexes, ArrayList<Vertex> offsetVertexes, double offset, boolean closed) {
		ArrayList<Edge> offstedEdges = new ArrayList<Edge>();

		int i;

		offset *= -1;
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

		for (i = 0; i < offstedEdges.size() - (closed ? 0: 1) ; i++) {
			Edge e = offstedEdges.get(i);
			if (e.m_dir == 0) {
				e.m_pos = 0;
			} else {
				Vertex p31 = offsetVertexes.get(i);
				Vertex p32 = offsetVertexes.get((i + 1) % vertexes.size());

				double d1 = absOffset;
				double d2 = absOffset;
				int j;
				e.m_pos = 1;
				for (j = 0; j < offstedEdges.size() - (closed ? 0: 1); j++) {
					Vertex p1 = vertexes.get(j);
					Vertex p2 = vertexes.get((j + 1) % vertexes.size());

					double p41x = p31.x + (p2.y - p1.y);
					double p42x = p32.x + (p2.y - p1.y);

					double p41y = p31.y + (p2.x - p1.x);
					double p42y = p32.y + (p2.x - p1.x);

					double h = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));

					if (crossProduct(p31.x, p31.y, p41x, p41y, p1.x, p1.y) * crossProduct(p31.x, p31.y, p41x, p41y, p2.x, p2.y) < 0) {
						double d = Math.abs(crossProduct(p1.x, p1.y, p2.x, p2.y, p31.x, p31.y) / h);
						//System.out.println(i + ";"+ j + ";" + d + ";" + absOffset);
						if (d < d1) {
							d1 = d;
						}
					}

					if (crossProduct(p32.x, p32.y, p42x, p42y, p1.x, p1.y) * crossProduct(p32.x, p32.y, p42x, p42y, p2.x, p2.y) < 0) {
						double d = Math.abs(crossProduct(p1.x, p1.y, p2.x, p2.y, p32.x, p32.y) / h);
						//System.out.println((i+1) + ";"+ j + ";" + d + ";" + absOffset);
						if (d < d2) {
							d2 = d;
						}
					}


					if (d1 < absOffset && d2 < absOffset) {
						System.out.println ("Invalid !");
						e.m_pos = 0;
						break;
					}
				}
			}
		}
		return offstedEdges;
	}

	private static Shape createShapeFromVertexes(ArrayList<Vertex> vertexes, boolean closed) {
		if (vertexes.size() < 2){
			return null;
		}

		Path2D.Double shp = new Path2D.Double();

		shp.moveTo(vertexes.get(0).x, vertexes.get(0).y);

		int i;

		for (i = 1; i < vertexes.size(); i++) {
			Vertex v = vertexes.get(i);
			shp.lineTo(v.x, v.y);
		}

		if (closed){
			shp.closePath();
		}

		return shp;
	}

	private static ArrayList<Vertex> computeRawLink(ArrayList<Edge> edges, ArrayList<Vertex> vertexes) {

		ArrayList<Vertex> rawLink = new ArrayList<Vertex>();

		ArrayList<Integer> offsetLinkList = new ArrayList<Integer>();
		ArrayList<Integer> bufferLinkList = new ArrayList<Integer>();

		int i;

		for (i = 0; i < edges.size(); i++) {
			Edge e = edges.get(i);
			offsetLinkList.add(i);
		}

		int backward = 0;
		int forward = 0;

		int in_dir = 0;
		int in_pos = 0;

		for (i = 0; i < edges.size(); i++) {
			Edge e = edges.get(i);
			System.out.println ("Edge is: " + e);
			if (e.is11()) {
				backward = i;
				break;
			} else {
				edges.remove(e);
				edges.add(e);
			}
		}

		while (backward < edges.size()){

			System.out.println ("Backward edge is " + backward);
			for (i = (backward + 1) % edges.size(); i != backward ; i = (i+1) % edges.size()) {
				Edge e = edges.get(i);
				if (e.hasBeedProcessed()){
					break;
				}
				if (e.is11()) {
					forward = i;
					e.processed = true;
					break;
				} else {
					if (e.m_dir == 0){
						in_dir += 1;
					}
					if (e.m_pos == 0){
						in_pos += 1;
					}
					if (e.is10()) {
						bufferLinkList.add(i);
						bufferLinkList.add((i + 1) % vertexes.size());
					}
				}
			}

			System.out.println ("Forward edge is " + forward);


			if (backward == forward){
				break;
			}

			System.out.println (" in_dir: " + in_dir + "    in_pos: " + in_pos);

			int bn = (backward + 1) % vertexes.size();
			int fn = (forward + 1) % vertexes.size();

			if (in_dir == 0 && in_pos == 0) {
				System.out.println ("Add " + vertexes.get(bn));
				rawLink.add(vertexes.get(bn));
			} else if (in_dir == 0 && in_pos > 0) {
				for (Integer j : bufferLinkList) {
					rawLink.add(vertexes.get(j));
					System.out.println ("Add " + vertexes.get(j));
				}
			} else if (in_dir == 1) {
				Vertex v1 = vertexes.get(backward);
				Vertex v2 = vertexes.get(bn);
				Vertex v3 = vertexes.get(forward);
				Vertex v4 = vertexes.get(fn);

				Point2D.Double inter = getLineIntersection(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y);

				Vertex nv = new Vertex(inter.x, inter.y);
				rawLink.add(nv);
				System.out.println ("Add " + nv);
			} else if (in_dir > 1) {
				Vertex v1 = vertexes.get(backward);
				Vertex v2 = vertexes.get(bn);
				Vertex v3 = vertexes.get(forward);
				Vertex v4 = vertexes.get(fn);
				Point2D.Double inter = getLineIntersection(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y);

				if (inter != null
						&& isPointOnSegement(v1.x, v1.y, v2.x, v2.y, inter.x, inter.y)
						&& isPointOnSegement(v3.x, v3.y, v4.x, v4.y, inter.x, inter.y)) {
					// 3.1
					Vertex nv = new Vertex(inter.x, inter.y);
					rawLink.add(nv);
					System.out.println ("Add3.1 " + nv);
				} else {
					// 3.2
					for (int j = backward + 1; j < forward; j++) {
						Vertex vn = vertexes.get(j);
						Vertex vm = vertexes.get((j + 1) % vertexes.size());

						Point2D.Double i1 = getLineIntersection(v1.x, v1.y, v2.x, v2.y, vn.x, vn.y, vm.x, vm.y);
						Point2D.Double i2 = getLineIntersection(vn.x, vn.y, vm.x, vm.y, v3.x, v3.y, v4.x, v4.y);

						if (i1 != null && i2 != null
								&& isPointOnSegement(v1.x, v1.y, v2.x, v2.y, i1.x, i1.y)
								&& isPointOnSegement(v3.x, v3.y, v4.x, v3.y, i2.x, i2.y)) {
							rawLink.add(new Vertex(i1.x, i1.y));
							rawLink.add(new Vertex(i2.x, i2.y));

							Vertex nv = new Vertex(i1.x, i1.y);
							rawLink.add(nv);
							System.out.println ("Add3.2a " + nv);

							nv = new Vertex(i2.x, i2.y);
							rawLink.add(nv);
							System.out.println ("Add3.2b " + nv);
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

		double dist12 = (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
		double dist13 = (x3-x1)*(x3-x1) + (y3-y1)*(y3-y1);
		double dist23 = (x3-x2)*(x3-x2) + (y3-y2)*(y3-y2);

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

	private static Shape contourParallelShape(Shape shp, double offset) {
		ArrayList<Vertex> vertexes = getVertexes(shp);

		System.out.println ("Initial vertexes: ");
		for (Vertex v : vertexes){
			System.out.println (v);
		}

		boolean closed = isClosed(vertexes);

		removeUselessVertex(vertexes);

		System.out.println ("Cleaned vertexes: ");
		for (Vertex v : vertexes){
			System.out.println (v);
		}

		ArrayList<Vertex> offsetVertexes = createOffsetVertexes(vertexes, offset, closed);

		System.out.println ("Offset vertexes: ");
		for (Vertex v : offsetVertexes){
			System.out.println (v);
		}



		ArrayList<Edge> edges = computeEdges(vertexes, offsetVertexes, offset, closed);

		System.out.println ("Edges: ");
		for (Edge e : edges){
			System.out.println (e);
		}

		ArrayList<Vertex> rawLink = computeRawLink(edges, offsetVertexes);

		System.out.println ("Raw Link: ");
		for (Vertex v : rawLink){
			System.out.println (v);
		}


		return createShapeFromVertexes(rawLink, closed);

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

		System.out.println(x1 + ";" + y1 + " --> " + x2 + ";" + y2);
		System.out.println(x3 + ";" + y3 + " --> " + x4 + ";" + y4);

		double a1 = (y2 - y1) / (x2 - x1);
		double a2 = (y4 - y3) / (x4 - x3);

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

		System.out.println(" intersection is: " + x + ";" + y);

		return new Point2D.Double(x, y);
	}

	public static Shape perpendicularOffset(Shape shp, double offset) {
		return contourParallelShape(shp, offset);
		//return perpendicularOffsetForLine(shp, offset);
	}
}
