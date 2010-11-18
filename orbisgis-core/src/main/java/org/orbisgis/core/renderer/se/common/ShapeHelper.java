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


	public static double getLineLength(Shape line){
		return getAreaPerimeterLength(line);
	}


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

	private static Point2D.Double getPointAt(double x1, double y1, double x2, double y2, double distance){
		double length = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));

		Point2D.Double pt = new Point2D.Double(x1 + distance*(x2-x1)/length, y1 + distance*(y2-y1)/length);

		return pt;
	}


	public static Point2D.Double getLineMiddle(Shape shp){

		double m = getLineLength(shp) / 2;


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

				if (p > m)
					break;
			}

			x1 = x2;
			y1 = y2;

			it.next();
		}

		return getPointAt(x1, y1, x2, y2, segLength - (p-m));
	}
}
