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

	public static final double _0_0175 = Math.PI/180.0;

	/**
	 * Compute the perimeter of the shape
	 * @todo test and move
	 * @param area
	 * @return
	 */
	public static double getAreaPerimeterLength(Shape area){
		PathIterator it = area.getPathIterator(null);

		double coords[] = new double[6];

		double p = 0.0;

		Double x1 = null;
		Double y1 = null;

		while (!it.isDone()){
			it.currentSegment(coords);

			double x2 = coords[0];
			double y2 = coords[1];

			if (x1 != null && y1 != null){
				double xx, yy;
				xx = x2-x1;
				yy = y2-y1;
				p += Math.sqrt(xx*xx + yy*yy);
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
	public static double getLineLength(Shape line){
		return getAreaPerimeterLength(line);
	}


	/**
	 * Split a linear feature in the specified number of part, which have all the same length
	 * @param line  the line to split
	 * @param nbPart the number of part to create
	 * @return list of equal-length segment
	 */
	public static ArrayList<Shape> splitLine(Shape line, int nbPart){
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

		while (!it.isDone()){
			it.currentSegment(coords);

			x2 = coords[0];
			y2 = coords[1];

			double xx, yy;
			xx = x2-x1;
			yy = y2-y1;
			p1 = Math.sqrt(xx*xx + yy*yy);
			p += p1;

			if (p > segLength){
				double delta = (p - segLength);

				Point2D.Double pt = getPointAt(x1, y1, x2, y2, p1-delta);

				x1 = pt.x;
				y1 = pt.y;

				segment.lineTo(x1, y1);
				p = 0;
				shapes.add(segment);
				segment = new Path2D.Double();
				segment.moveTo(x1, y1);


			} else {
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
	private static Point2D.Double getPointAt(double x1, double y1, double x2, double y2, double distance){
		double length = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));

		Point2D.Double pt = new Point2D.Double(x1 + distance*(x2-x1)/length, y1 + distance*(y2-y1)/length);

		return pt;
	}



	/**
	 * Go along a line shape and return the point at the specified distance from the beginning of the line
	 * @param shp  the line
	 * @param distance
	 * @return point representing the point at the linear length distance
	 */
	public static Point2D.Double getPointAt(Shape shp, double distance){
		PathIterator it = shp.getPathIterator(null);

		double coords[] = new double[6];

		double p = 0.0;

		Double x1 = null;
		Double y1 = null;

		double x2 = 0.0;
		double y2 = 0.0;
		double segLength = 0.0;

		while (!it.isDone()){
			it.currentSegment(coords);

			x2 = coords[0];
			y2 = coords[1];

			if (x1 != null && y1 != null){
				double xx, yy;
				xx = x2-x1;
				yy = y2-y1;
				segLength = Math.sqrt(xx*xx + yy*yy);
				p += segLength;

				if (p > distance){
					break;
				}
			}

			x1 = x2;
			y1 = y2;

			it.next();
		}

		return getPointAt(x1, y1, x2, y2, segLength - (p-distance));
	}

	private static Polygon perpendicularOffsetForArea(){
		return null;
	}

	private static Path2D.Double perpendicularOffsetForLine(Shape shp, double offset){
		Path2D.Double newShp = new Path2D.Double();

		offset *= -1;

		PathIterator it = shp.getPathIterator(null);
		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> y = new ArrayList<Double>();

		double absOffset = Math.abs(offset);

		double coords[] = new double[6];

		// Want a direct access to coordinates !!!
		while (!it.isDone()){
			it.currentSegment(coords);
			x.add(coords[0]);
			y.add(coords[1]);
			it.next();
		}

		int i;

		double gamma;
		double theta = Math.PI/2;

		if (offset < 0){
			theta *= -1;
		}

		for (i=0;i<x.size();i++){
			if (i==0){
				// First point (linear case)
				gamma = Math.atan2(y.get(i+1)-y.get(i), x.get(i+1) - x.get(i)) + theta;

				newShp.moveTo(x.get(i) + Math.cos(gamma)*absOffset,
							  y.get(i) + Math.sin(gamma)*absOffset);

			} else if (i == x.size()-1){
				// Last point (linear case)
				gamma = Math.atan2(y.get(i)-y.get(i-1), x.get(i) - x.get(i-1)) + theta;

				newShp.lineTo(x.get(i) + Math.cos(gamma)*absOffset,
							  y.get(i) + Math.sin(gamma)*absOffset);
			} else {

				// other point

				/*
				 *    a  (i-1)
				 *    \
				 *     \
				 *      \ (i)          (i+1)
				 *       \_____________
				 *       b             c
				 */

				// AB line orientation
				double alpha = Math.atan2(y.get(i-1) - y.get(i), x.get(i-1) - x.get(i));
				// BC line orientation
				double beta = Math.atan2(y.get(i+1) - y.get(i), x.get(i+1) - x.get(i));

				System.out.println ("Alpha: " + alpha / _0_0175);
				System.out.println ("Beta: " + beta / _0_0175);

				// ABC Angle
				gamma = alpha - beta;

				System.out.println ("gamma: " + gamma / _0_0175);

				System.out.println ("Offset: " + offset);


				if (offset < 0 && gamma > Math.PI

				//if (Math.abs(gamma) > _0_0175
				//	&& (   offset < 0 && gamma > Math.PI
						|| offset > 0 && gamma < Math.PI
						|| offset < 0 && gamma < 0 && gamma > -Math.PI){

					gamma = (alpha + beta)/2;

					double a1, a2;
					double b1, b2;

					a1 = (y.get(i-1) - y.get(i)) / (x.get(i-1) - x.get(i));
					a2 = (y.get(i+1) - y.get(i)) / (x.get(i+1) - x.get(i));

					b1 = y.get(i) - a1*x.get(i);
					b2 = y.get(i) - a2*x.get(i);

					b1 -= offset/Math.cos(alpha);
					b2 += offset/Math.cos(beta);

					double x2 = 0, y2 = 0;

					double localXOffset;
					if (beta > 0){
						localXOffset = -offset;
					} else {
						localXOffset = offset;
					}

					if (Double.isInfinite(a1) && Double.isInfinite(a2)){
						System.out.println ("Should never occurs !!!");
					} else if (Double.isInfinite(a1)){
							x2 = x.get(i) + localXOffset;
							y2 = a2*x2 + b2;
							newShp.lineTo(x2, y2);
					} else if (Double.isInfinite(a2)){
							x2 = x.get(i) + localXOffset;
							y2 = a1*x2 + b1;
							newShp.lineTo(x2, y2);
					} else {
						x2 = (b2 - b1) / (a1 - a2);
						y2 = a1*x2 + b1;
						newShp.lineTo(x2, y2);
					}
				} else {
					double a1 = alpha - theta;

					double x1, y1;
					double x2, y2;

					// Arc beginning
					x1 = x.get(i) + Math.cos(a1)*absOffset;
				  	y1 = y.get(i) + Math.sin(a1)*absOffset;

					// Arc end
					double a2 = beta + theta;

					x2 = x.get(i) + Math.cos(a2)*absOffset;
					y2 = y.get(i) + Math.sin(a2)*absOffset;

					// Move to arc begin
					newShp.lineTo(x1, y1);

					double dx = (x1 + x2)/2 - x.get(i);
					double dy = (y1 + y2)/2 - y.get(i);

					double h = Math.sqrt(dx*dx + dy*dy);

					double x3;
					double y3;

					System.out.println ("H: " + h);

					if (Math.abs(h) < 0.001){
						dx = x.get(i) - x1;
						dy = y.get(i) - y1;
						x3 = x.get(i) + dy;
						y3 = y.get(i) - dx;
					} else {
						x3 = x.get(i) + dx*absOffset / h;
						y3 = y.get(i) + dy*absOffset / h;
					}

					newShp.quadTo(x3, y3, x2, y2);

					System.out.println ("A: " + x1 + " " + y1);
					System.out.println ("B: " + x2 + " " + y2);
					System.out.println ("Ctrl: " + x3 + " " + y3);
				}
			}
		}
		return newShp;
	}

	public static Shape perpendicularOffset(Shape shp, double offset){
		return perpendicularOffsetForLine(shp, offset);
	}
}
