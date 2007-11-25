package org.orbisgis.geoview.renderer.style.sld;

import org.orbisgis.pluginManager.VTD;

import com.ximpleware.xpath.XPathParseException;

public class Filter {

	

	private VTD vtd;
	private String rootXpathQuery;

	

	public Filter(VTD vtd, String rootXpathQuery) {
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
	}
	
	
	
	public void buildExpression() throws XPathParseException{
		
		String fieldName = null;
		String query = null;
		
		String value = null;
		
		//logical operators
		String andFilter = rootXpathQuery + "/ogc:And";
		String orFilter = rootXpathQuery + "/ogc:Or";
		
		
		String  propertyIsGreaterThanOrEqualTo = "/ogc:PropertyIsGreaterThanOrEqualTo";
		
		String  propertyIsLessThanOrEqualTo = "/ogc:PropertyIsLessThanOrEqualTo";
		
		String propertyIsEqualTo = "/ogc:PropertyIsEqualTo";
		
		
		if (vtd.evalToString(andFilter)!=null){
			
			if (vtd.evalToString(andFilter + propertyIsGreaterThanOrEqualTo)!=null) {
				
				
			}
			
			
		}
		else if (vtd.evalToString(orFilter)!=null) {
			
			
		}
		
		else if (vtd.evalToString(propertyIsGreaterThanOrEqualTo)!=null) {
			
			
		}
		
		else if (vtd.evalToString(propertyIsLessThanOrEqualTo)!=null) {
			
			
		}
		

		else if (vtd.evalToString(propertyIsEqualTo)!=null) {
			
			
		}
	
		else {
			
		}
		
	}

}
