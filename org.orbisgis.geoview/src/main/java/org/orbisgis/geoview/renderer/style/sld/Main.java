package org.orbisgis.geoview.renderer.style.sld;

import java.io.IOException;
import java.util.List;

import org.orbisgis.geoview.renderer.style.sld.FeatureTypeStyle;

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

		String path = "..//..//datas2tests//sld//densityBySymbols.sld";

		//String path = "..//..//datas2tests//sld//blueRivers.sld";

		
		SLDParser parser = new SLDParser(path);

		parser.read();

		List<FeatureTypeStyle> featureTypeStyles = parser.getFeatureTypeStyles();

		List<Symbolizer> symbolizers = featureTypeStyles.get(0).getRules().get(1).getSymbolizers();
		
		if(symbolizers.get(0) instanceof PointSymbolizer){
			
			PointSymbolizer pointSymbolizer= (PointSymbolizer) symbolizers.get(0);
			System.out.println(pointSymbolizer.getGraphic().getMark().getWellKnownName());
			
		}
		
	
		
		
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