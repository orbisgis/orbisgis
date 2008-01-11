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
package org.orbisgis.geoview.renderer.style;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.RectangularShape;

import org.orbisgis.geoview.renderer.liteShape.LiteShape;

public class LineStyle implements Style{

	
	
	
	private String stringLineColor;
	private int fillOpacity;
	private BasicStroke stroke;
	static String defaultShape = "circle";
	static int size = 1;
	
	
	public  LineStyle (String stringLineColor) {
		this(stringLineColor,size );
		
	}
	
	
		
	
	public  LineStyle (String stringLineColor, int size) {
		
		this.stringLineColor = stringLineColor;
		this.size =size;
		stroke = new BasicStroke(size,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	}
		
	
		
	public Color getLineColor() {
			
			if (stringLineColor.length()>0){
				return Color.decode(stringLineColor);
			}
		
		
		return null;
	}
	
	
	public Color getDefaultLineColor() {
		
		return Color.GREEN;
	}
	
	public BasicStroke getBasicStroke() {
		return stroke;
	}

	
	 public void setSize(int size) {
	        this.size = size;
	    }

	    public int getSize() {
	        return size;
	    }


		public Color getFillColor() {
			// TODO Auto-generated method stub
			return null;
		}

	
}
