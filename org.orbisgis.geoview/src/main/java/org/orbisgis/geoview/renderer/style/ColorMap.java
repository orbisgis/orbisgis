package org.orbisgis.geoview.renderer.style;

import org.orbisgis.pluginManager.VTD;

import com.ximpleware.xpath.XPathParseException;



public class ColorMap {

	private VTD vtd;
	private String rootXpathQuery;

	public ColorMap(VTD vtd, String rootXpathQuery) {
		
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
	}
	
	public int getColorMapEntryCount() throws XPathParseException{
		return vtd.evalToInt("count(" + rootXpathQuery + "/sld:ColorMapEntry"+")");
		
	}
	
	


}
