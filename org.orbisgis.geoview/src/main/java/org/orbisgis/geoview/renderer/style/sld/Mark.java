package org.orbisgis.geoview.renderer.style.sld;

import org.orbisgis.pluginManager.VTD;

import com.ximpleware.xpath.XPathParseException;



public class Mark {

	private VTD vtd;
	private String rootXpathQuery;

	public Mark(VTD vtd, String rootXpathQuery) {
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
	}
	
	public String getWellKnownName() throws XPathParseException{
		
		return vtd.evalToString(rootXpathQuery+"WellKnownName");
	}

}
