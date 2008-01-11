/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
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

import java.awt.Graphics2D;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.style.LineStyle;
import org.orbisgis.geoview.renderer.style.sld.LineSymbolizer;

import com.vividsolutions.jts.geom.Geometry;
import com.ximpleware.xpath.XPathParseException;

public class LineSymbolizerRenderer {

	
	private MapControl mapControl;

	public LineSymbolizerRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public static void paint(Graphics2D graphics, SpatialDataSourceDecorator sds, LineSymbolizer symbolizer, MapControl mapControl) {
		
		LineStyle lineStyle = null;
		try {
			lineStyle = new LineStyle(symbolizer.getStroke().getStrokeColor(), symbolizer.getStroke().getStrokeWidth());
		} catch (XPathParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			for (int i = 0; i < sds.getRowCount(); i++) {
				
				final Geometry geom =sds.getGeometry(i);
				
				LinePainter.paint(geom, graphics, lineStyle, mapControl);
				
			}
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
