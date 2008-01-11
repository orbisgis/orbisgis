/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D.Float;
import java.util.Random;

import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.geoview.renderer.style.PointStyle;
import org.orbisgis.geoview.renderer.style.Style;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class GeometryPainter {
	public static void paint(final Geometry geometry, final Graphics2D g,
			final Style style, final MapControl mapControl) {
		Shape liteShape;
		Shape shapeToDraw = null;
		
		
		
		
		if (null != geometry) {
			liteShape = new LiteShape(geometry, mapControl.getTrans(), true);

			if ((geometry instanceof Point) | (geometry instanceof MultiPoint)) {
				
			
				PointStyle pointStyle = new PointStyle("#FFFF00", "#FFFF00");
				PathIterator pi = liteShape.getPathIterator(null);
				while (!pi.isDone()) {
					double[] point = new double[6];
					pi.currentSegment(point);
					float x = (float) point[0];
					float y = (float) point[1];
					
					
					shapeToDraw = pointStyle.getDefaultShape(x, y, 10);
					g.setPaint(style.getFillColor());
					g.fill(shapeToDraw);
					pi.next();
				}

			} else {

				shapeToDraw = liteShape;

			}

			if ((style.getFillColor() != null) && (geometry instanceof Polygon)
					| (geometry instanceof MultiPolygon)  ) {
				// TODO : we should manage also GeometryCollection...
				g.setPaint(style.getFillColor());
				
				g.fill(shapeToDraw);
			}

			if (style.getLineColor() != null) {
				g.setColor(style.getLineColor());

			} else {
				g.setColor(style.getDefaultLineColor());
			}

			if (null != style.getBasicStroke()) {
				g.setStroke(style.getBasicStroke());
			}			
				
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
			
			g.setComposite(ac);
			g.draw(shapeToDraw);

		}
	}
}