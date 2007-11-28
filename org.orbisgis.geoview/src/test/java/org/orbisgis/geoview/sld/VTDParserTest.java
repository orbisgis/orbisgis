package org.orbisgis.geoview.sld;

import java.io.File;
import java.io.IOException;

import org.orbisgis.pluginManager.VTD;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.ParseException;
import com.ximpleware.xpath.XPathParseException;

public class VTDParserTest {

	

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws EntityException 
	 * @throws EOFException 
	 * @throws EncodingException 
	 * @throws XPathParseException 
	 */
	public static void main(String[] args) throws EncodingException, EOFException, EntityException, ParseException, IOException, XPathParseException {
		
		File file = new File("..//..//datas2tests//sld//density.sld");
		
		String xpathExpr = "//StyledLayerDescriptor/UserLayer/UserStyle/FeatureTypeStyle"+
		"/Rule/Filter";
		
		VTD vtd = new VTD(file, true);

		// Here you specify the schemas
		vtd.declareXPathNameSpace("sld", "http://www.opengis.net/sld");
		vtd.declareXPathNameSpace("ogc", "http://www.opengis.net/ogc");
		vtd.declareXPathNameSpace("gml", "http://www.opengis.net/gml");
		
		System.out.println(vtd.evalToString(xpathExpr));
		

	}

}
