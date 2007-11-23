package org.orbisgis.geoview.renderer.style.sld;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
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
import org.opengis.go.display.style.event.GraphicStyleListener;
import org.orbisgis.pluginManager.VTD;

import com.ximpleware.xpath.XPathParseException;

public class PolygonSymbolizer implements Symbolizer {

	/** to be complete
	 * 
	 *
	 */
	
	private VTD vtd;
	private String rootXpathQuery;
	private ArrayList<Stroke> strokes;

	/** SLD tags
	 * 
	 * 
	 * <sld:PolygonSymbolizer>
                        <sld:Fill>
                            <sld:CssParameter name="fill">
                                <ogc:Literal>#4A4A4A</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="fill-opacity">
                                <ogc:Literal>1.0</ogc:Literal>
                            </sld:CssParameter>
                        </sld:Fill>
                        <sld:Stroke>
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
                    </sld:PolygonSymbolizer>
	 * 
	 */
	
	public PolygonSymbolizer(VTD vtd, String rootXpathQuery){
		
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
		
		strokes = new ArrayList<Stroke>();
		
		strokes.add(new Stroke(vtd, rootXpathQuery
				+ "/sld:Stroke"));
		
	}

	
	public List<Stroke> getStrokes(){
		return strokes;
		
	}
	
	public String getFillColor() throws XPathParseException{
			
		
		 return  vtd.evalToString(rootXpathQuery + "/sld:Fill/sld:cssParameter[@name='fill']/ogc:Literal");
		
		
	}
	
	public int getFillOpacity() throws XPathParseException{
			
		int fillOpacity = vtd.evalToInt(rootXpathQuery + "/sld:Fill/sld:cssParameter[@name='fill-opacity']/ogc:Literal");
		
		
		if (fillOpacity==0) {
			return 1;
			
		} 
		return fillOpacity;
	}
	
	
	

}
