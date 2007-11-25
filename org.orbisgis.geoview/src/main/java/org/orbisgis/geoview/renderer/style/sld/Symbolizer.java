package org.orbisgis.geoview.renderer.style.sld;

import com.ximpleware.xpath.XPathParseException;


/**
 *
 * @author Erwan
 *
 *
 *OCG SLD 1.1
 *
 * A symbolizer can be  : 
 *<xs:element ref="sld:LineSymbolizer"/>
<xs:element ref="sld:PolygonSymbolizer"/>
<xs:element ref="sld:PointSymbolizer"/>
<xs:element ref="sld:TextSymbolizer"/>
<xs:element ref="sld:RasterSymbolizer"/>

 */


public interface Symbolizer {

	String getType();

	
		
	
}