package org.orbisgis.geoview.renderer.style.sld;

import java.util.ArrayList;
import java.util.List;

import org.orbisgis.pluginManager.VTD;

import com.ximpleware.NavException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class Rule {

	private VTD vtd;

	private String rootXpathQuery;

	public String childXpathQuery;
	
	final List<Symbolizer> symbolizers ;

	public Rule(VTD vtd, String rootXpathQuery) throws XPathParseException,
			XPathEvalException, NavException {
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
		childXpathQuery = rootXpathQuery + "/sld:PointSymbolizer|"
				+ rootXpathQuery + "/sld:LineSymbolizer|" + rootXpathQuery
				+ "/sld:PolygonSymbolizer";

		 symbolizers = new ArrayList<Symbolizer>();
		 
		 String[] nodeNames = vtd.getNodesNames(childXpathQuery);
		 
		 for (int i = 0; i < getSymbolizersCount(); i++) {
			 String nodeName = nodeNames[i];
			 
			 if (nodeName.equalsIgnoreCase("sld:PointSymbolizer")) {
					symbolizers.add(new PointSymbolizer(vtd, rootXpathQuery
							+ "/sld:PointSymbolizer"));
				} 
				else if (nodeName.equalsIgnoreCase("sld:LineSymbolizer")) {
					
					symbolizers.add(new LineSymbolizer(vtd, rootXpathQuery
							+ "/sld:LineSymbolizer"));
				} 
				
				else if (nodeName.equalsIgnoreCase("sld:PolygonSymbolizer")) {
					
					symbolizers.add(new PolygonSymbolizer(vtd, rootXpathQuery
							+ "/sld:PolygonSymbolizer"));
				} 
				else {
					
				}
			
		}
		
		
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

	public int getSymbolizersCount() throws XPathParseException,
			XPathEvalException, NavException {
		return vtd.evalToInt("count(" + childXpathQuery + ")");
	}
	
	public List<Symbolizer> getSymbolizers(){
		return symbolizers;
		
		
	}
	
}