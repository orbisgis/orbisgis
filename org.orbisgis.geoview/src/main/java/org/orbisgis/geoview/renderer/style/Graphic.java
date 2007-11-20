package org.orbisgis.geoview.renderer.style;

import org.orbisgis.pluginManager.VTD;



public class Graphic {

	
	private VTD vtd;
	private String rootXpathQuery;

	public Graphic(VTD vtd, String rootXpathQuery) {
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
	}

	public Mark getMark(){
		return new Mark(vtd, rootXpathQuery+"/Mark");
		
	}
}
