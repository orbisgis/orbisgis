package org.orbisgis.geoview.renderer.style;

import java.io.IOException;
import java.util.List;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDNav;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class Main {
	public static void main(String argv[]) throws EncodingException,
			EOFException, EntityException, ParseException, IOException,
			XPathParseException, XPathEvalException, NavException {

		//String path = "..//..//datas2tests//sld//densityBySymbols.sld";

		String path = "..//..//datas2tests//sld//yellowparcels.sld";

		
		SLDParser parser = new SLDParser(path);

		parser.read();

		List<FeatureTypeStyle> featureTypeStyles = parser.getFeatureTypeStyles();

		Symbolizer symbolizer = featureTypeStyles.get(0).getRules().get(0).getSymbolizer();
		
		PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) symbolizer;
		
		System.out.println("Couleur " + polygonSymbolizer.getFillColor());
		
		
		 //System.out.print(vtd.getContent("sld:StyledLayerDescriptor/sld:UserLayer/sld:UserStyle/sld:FeatureTypeStyle/sld:Rule/sld:LineSymbolizer");
		
		/*
		 * System.out.print(vtd.getContent("//sld:StyledLayerDescriptor/" +
		 * "sld:UserLayer/sld:UserStyle/" +
		 * "sld:FeatureTypeStyle/sld:Rule/sld:Title"));
		 * 
		 * int n = vtd.evalToInt("count(//sld:StyledLayerDescriptor)");
		 * 
		 * 
		 * 
		 * System.out.println(parser.getFeatureTypeStyleCount());
		 * 
		 * /*System.out.println(vtd.evalToString("//sld:StyledLayerDescriptor/" +
		 * "sld:UserLayer/sld:UserStyle/" +
		 * "sld:FeatureTypeStyle/sld:Rule/sld:Title"));
		 */

	}
}