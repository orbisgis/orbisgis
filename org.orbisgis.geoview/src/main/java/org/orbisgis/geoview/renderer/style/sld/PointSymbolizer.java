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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.List;

import org.opengis.go.display.style.ArrowStyle;
import org.opengis.go.display.style.DashArray;
import org.opengis.go.display.style.FillPattern;
import org.opengis.go.display.style.FillStyle;
import org.opengis.go.display.style.GraphicStyle;
import org.opengis.go.display.style.LineCap;
import org.opengis.go.display.style.LineJoin;
import org.opengis.go.display.style.LinePattern;
import org.opengis.go.display.style.LineStyle;
import org.opengis.go.display.style.Mark;
import org.opengis.go.display.style.event.GraphicStyleListener;
import org.orbisgis.pluginManager.VTD;

import com.ximpleware.NavException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class PointSymbolizer implements Symbolizer {

	/** 
	 * 
	 * A PointSymbolizer is used to draw âa graphic at a point.
	 * 
	 * It has the following simple definition:
    <xs:element name="PointSymbolizer">
       <xs:complexType>
         <xs:sequence>
            <xs:element ref="sld:Geometry" minOccurs="0"/>
            <xs:element ref="sld:Graphic" minOccurs="0"/>
         </xs:sequence>
       </xs:complexType>
    </xs:element>

	 * 
	 *
	 */
	
	private VTD vtd;
	private String rootXpathQuery;
	private String type ="sld:PointSymbolizer";

	/** SLD tags
	 * 
	 * Simple exemple 
	 * 
	 * <PointSymbolizer>
   <Geometry>
      <ogc:PropertyName>locatedAt</ogc:PropertyName>
   </Geometry>
   <Graphic>
      <Mark>
         <WellKnownName>star</WellKnownName>
         <Fill>
             <CssParameter name="fill">#ff0000</CssParameter>
         </Fill>
      </Mark>
      <Size>8.0</Size>
   </Graphic>
</PointSymbolizer>

	 * 
	 * More complexe
	 * 
	 * 
	 * <PointSymbolizer>
	      <Graphic>
	        <Mark>
	          <WellKnownName>circle</WellKnownName>
	          <Fill>
                  <CssParameter name="fill">#6688aa</CssParameter>
                </Fill>
                <Stroke>
                  <CssParameter name="stroke">#000000</CssParameter>
                </Stroke>                          
	        </Mark>
	        <Size>
	          <ogc:Div>	            			    
	            <ogc:PropertyName>OUI_EEE92</ogc:PropertyName>
		      <ogc:Literal>5</ogc:Literal>
	          </ogc:Div>
	        </Size>
	      </Graphic>
	    </PointSymbolizer>
	 * 
	 */
	public PointSymbolizer(VTD vtd, String rootXpathQuery){
		
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
		
	}
	
	
	public Graphic getGraphic(){
		return new Graphic(vtd, rootXpathQuery+"/Graphic");
		
	}


	public String getType() {
		
		return type ;
	}
	
	
public String toString(){
		
		try {
			return vtd.getContent(rootXpathQuery);
		} catch (XPathParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathEvalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NavException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
