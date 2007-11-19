package org.orbisgis.geoview.renderer.style;

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
	
	
	public String getStroke() throws XPathParseException{
		
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
