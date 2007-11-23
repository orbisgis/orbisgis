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

public class PointSymbolizer implements Symbolizer {

	/** 
	 * 
	 * A PointSymbolizer is used to draw “graphic” at a point.
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
	
	
	
	

	

	
	
	
	

}
