package org.orbisgis.plugin.view.ui.utility.style;

import org.geotools.styling.SLDParser;

import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryFinder;

/**
 * 
 * @author Erwan Bocher Style utilities.
 * 
 * 
 */

public class UtilStyle {

	public static Style loadStyleFromXml(String url) throws Exception {

		StyleFactory factory = StyleFactoryFinder.createStyleFactory();

		SLDParser parser = new SLDParser(factory, url);

		Style style = parser.readXML()[0];

		return style;
	}
}