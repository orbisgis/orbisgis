/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
