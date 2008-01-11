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


import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.Shape;


import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.geoview.renderer.style.LineStyle;
import org.orbisgis.geoview.renderer.style.PolygonStyle;

import org.orbisgis.geoview.renderer.style.sld.PolygonSymbolizer;

import com.vividsolutions.jts.geom.Geometry;


public class LinePainter {
	public static void paint(final Geometry geometry, final Graphics2D g,
			final  LineStyle lineStyle, final MapControl mapControl) {
		Shape liteShape;
	
	
		if (null != geometry) {
			liteShape = new LiteShape(geometry, mapControl.getTrans(), true);
			
			if (lineStyle.getLineColor() != null) {
				g.setColor(lineStyle.getLineColor());

			} else {
				g.setColor(lineStyle.getDefaultLineColor());
			}

			if (null != lineStyle.getBasicStroke()) {
				g.setStroke(lineStyle.getBasicStroke());
			}
			
			
			g.draw(liteShape);

		}
	}
}