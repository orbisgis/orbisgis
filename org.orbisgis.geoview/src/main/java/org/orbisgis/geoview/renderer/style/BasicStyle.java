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
package org.orbisgis.geoview.renderer.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;


public class BasicStyle implements Style {
	private Color lineColor;

	private Color fillColor;

	private static float lineSize = 1;

	private BasicStroke basicStroke;

	private float alpha = 1;

	
	
	public BasicStyle(Color lineColor, int i,  Color fillColor) {
		this(lineColor,lineSize,fillColor,1 );
	}
	public BasicStyle(final Color lineColor, final Color fillColor, final int alpha) {
		this(lineColor,lineSize , fillColor, alpha);
	}
	
	
	public BasicStyle(final Color lineColor, final float lineSize, final Color fillColor, final int alpha) {
		this.lineColor = lineColor;
		this.lineSize = lineSize;
		this.fillColor = fillColor;
		this.alpha  = alpha;
		basicStroke = new BasicStroke(lineSize);
	}
	
	public BasicStyle(){
		
	}
	

	



	public Color getFillColor() {
		return fillColor;
	}
	
	public float getAlpha() {
		return alpha;
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Color getDefaultLineColor() {
		return Color.BLUE;
	}
	
	
	public void setLineSize(final float lineSize){
		this.lineSize = lineSize;
		basicStroke  = new BasicStroke(lineSize);
	}

	public BasicStroke getBasicStroke() {
		
		return basicStroke;
	}


}