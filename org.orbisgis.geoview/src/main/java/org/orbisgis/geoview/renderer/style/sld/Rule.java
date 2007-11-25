package org.orbisgis.geoview.renderer.style.sld;

import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor.STRING;

import org.orbisgis.pluginManager.VTD;

import com.ximpleware.NavException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class Rule {

	private VTD vtd;

	private String rootXpathQuery;

	public String childXpathQuery;
	

	public Rule(VTD vtd, String rootXpathQuery) throws XPathParseException,
			XPathEvalException, NavException {
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
		childXpathQuery = rootXpathQuery + "/sld:PointSymbolizer|"
				+ rootXpathQuery + "/sld:LineSymbolizer|" + rootXpathQuery
				+ "/sld:PolygonSymbolizer";
		 				
	}

	public String getName() throws XPathParseException {
		return vtd.evalToString(rootXpathQuery + "/sld:Name");
	}

	public String getTitle() throws XPathParseException {
		return vtd.evalToString(rootXpathQuery + "/sld:Title");
	}

	public String getAbstract() throws XPathParseException {
		return vtd.evalToString(rootXpathQuery + "/sld:Abstract");
	}

	public Double getMaxScaleDenominator() throws XPathParseException {
		return vtd.evalToNumber(rootXpathQuery + "/sld:MaxScaleDenominator");
	}

	
	
	public Symbolizer getSymbolizer() throws XPathParseException, XPathEvalException, NavException{
		
		 String nodeName =  vtd.getNodeName(childXpathQuery);
		
		 System.out.println(nodeName);
		 
		 if (nodeName.equalsIgnoreCase("sld:PointSymbolizer")) {
				return new PointSymbolizer(vtd, rootXpathQuery
						+ "/sld:PointSymbolizer");
			} 
			else if (nodeName.equalsIgnoreCase("sld:LineSymbolizer")) {
				
				
				return new LineSymbolizer(vtd, rootXpathQuery
						+ "/sld:LineSymbolizer");
			} 
			
			else if (nodeName.equalsIgnoreCase("sld:PolygonSymbolizer")) {
				
				return new PolygonSymbolizer(vtd, rootXpathQuery
						+ "/sld:PolygonSymbolizer");
			} 
			else {
				
			}
		
		return null;
		
	}
	
	public Filter getFilter() throws XPathParseException, XPathEvalException, NavException{
		
		String filterXpathQuery = rootXpathQuery + "/ogc:Filter";
		
		return new Filter(vtd, filterXpathQuery);
		
				
	}
}