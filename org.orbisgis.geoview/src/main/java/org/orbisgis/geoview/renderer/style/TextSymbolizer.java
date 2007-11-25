package org.orbisgis.geoview.renderer.style;

import org.orbisgis.pluginManager.VTD;

public class TextSymbolizer {

	
	private VTD vtd;
	private String rootXpathQuery;

	public TextSymbolizer(VTD vtd, String rootXpathQuery){
		
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
	}
}
