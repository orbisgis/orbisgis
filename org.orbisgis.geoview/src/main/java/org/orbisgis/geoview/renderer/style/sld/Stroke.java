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
package org.orbisgis.geoview.renderer.style.sld;

import java.awt.Color;

import org.orbisgis.pluginManager.VTD;

import com.ximpleware.xpath.XPathParseException;

public class Stroke {

	

	private VTD vtd;
	private String rootXpathQuery;

	/**
	 * <sld:Stroke>
    <sld:CssParameter name="stroke">
        <ogc:Literal>#BFBFBF</ogc:Literal>
    </sld:CssParameter>
    <sld:CssParameter name="stroke-linecap">
        <ogc:Literal>butt</ogc:Literal>
    </sld:CssParameter>
    <sld:CssParameter name="stroke-linejoin">
        <ogc:Literal>miter</ogc:Literal>
    </sld:CssParameter>
    <sld:CssParameter name="stroke-opacity">
        <ogc:Literal>1.0</ogc:Literal>
    </sld:CssParameter>
    <sld:CssParameter name="stroke-width">
        <ogc:Literal>2.0</ogc:Literal>
    </sld:CssParameter>
    <sld:CssParameter name="stroke-dashoffset">
        <ogc:Literal>0.0</ogc:Literal>
    </sld:CssParameter>
	</sld:Stroke>
	 */
	
	
	public Stroke(VTD vtd, String rootXpathQuery){
		
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
		
		
	}
	
	
	public String getStrokeColor() throws XPathParseException{
		
		return vtd.evalToString(rootXpathQuery + "/sld:CssParameter[@name='stroke']/ogc:Literal");
				
		
		
	}
	
	public String getStrokeLinecap() throws XPathParseException{
		return vtd.evalToString(rootXpathQuery + "/sld:Stroke/sld:CssParameter[@name='stroke-linecap']/ogc:Literal");
		
	}
	
	public String getStrokeLinejoin() throws XPathParseException{
		return vtd.evalToString(rootXpathQuery + "/sld:Stroke/sld:CssParameter[@name='stroke-linejoin']/ogc:Literal");
		
	}
	
	
	public int getStrokeOpacity() throws XPathParseException{
		return vtd.evalToInt(rootXpathQuery + "/sld:Stroke/sld:CssParameter[@name='stroke-opacity']/ogc:Literal");
		
	}
	
	public int getStrokeWidth() throws XPathParseException{
		return vtd.evalToInt(rootXpathQuery + "/sld:Stroke/sld:CssParameter[@name='stroke-width']/ogc:Literal");
		
	}
	
	public int getStrokeDashoffset() throws XPathParseException{
		return vtd.evalToInt(rootXpathQuery + "/sld:Stroke/sld:CssParameter[@name='stroke-dashoffset']/ogc:Literal");
		
		
	}	
	
	
}
