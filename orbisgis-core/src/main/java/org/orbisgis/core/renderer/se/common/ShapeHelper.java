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
import java.awt.geom.PathIterator;

/**
 *
 * @author maxence
 */
public class ShapeHelper {


	/**
	 * Compute the perimeter of the shape
	 * @todo test and move
	 * @param shp
	 * @return
	 */
	public static double getShapePerimeter(Shape shp){
		PathIterator it = shp.getPathIterator(null);

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

}
