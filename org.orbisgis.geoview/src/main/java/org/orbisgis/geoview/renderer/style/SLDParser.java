package org.orbisgis.geoview.renderer.style;

import com.ximpleware.*;
import com.ximpleware.EOFException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.orbisgis.pluginManager.VTD;

public class SLDParser {

 public VTD vtd;

	private File f;

	private String path;

	private String rootXpathQuery = "//sld:StyledLayerDescriptor/sld:UserLayer/sld:UserStyle/sld:FeatureTypeStyle";

	private List<FeatureTypeStyle> featureTypeStyles;
	
	

	public SLDParser(String path) {
		this.path = path;
		f = new File(path);
		

	}

	public void read() throws EncodingException, EOFException,
			EntityException, ParseException, IOException, XPathParseException, XPathEvalException, NavException {

		
		if (f != null) {
			featureTypeStyles = new ArrayList<FeatureTypeStyle>();
			vtd = new VTD(f, true);

			// Here you specify the schemas
			vtd.declareXPathNameSpace("sld", "http://www.opengis.net/sld");
			vtd.declareXPathNameSpace("ogc", "http://www.opengis.net/ogc");
			vtd.declareXPathNameSpace("gml", "http://www.opengis.net/gml");
			final int n = getFeatureTypeStyleCount();
			for (int i = 0; i < n; i++) {
				featureTypeStyles
						.add(new FeatureTypeStyle(vtd, rootXpathQuery));
								
			}
				
			 
		} else {
			throw new IOException(path + " is not readable !");
		}

	}

	public int getFeatureTypeStyleCount() throws XPathParseException {
		return vtd.evalToInt("count(" + rootXpathQuery + ")");

	}

	public List<FeatureTypeStyle> getFeatureTypeStyles() {
		return featureTypeStyles;
	}
}
