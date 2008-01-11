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
package org.orbisgis;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLUtils {

	/**
	 * Returns null if the content is valid for the specified schema. If the
	 * content is not valid it returns a description of why it is invalid
	 *
	 * @param schemaFile
	 * @param content
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static String validateXML(File schemaFile, String content)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory spfactory = SAXParserFactory.newInstance();
		spfactory.setNamespaceAware(false);
		spfactory.setValidating(true);
		SAXParser saxparser = spfactory.newSAXParser();

		saxparser.setProperty(
				"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
				"http://www.w3.org/2001/XMLSchema");
		saxparser.setProperty(
				"http://java.sun.com/xml/jaxp/properties/schemaSource",
				schemaFile);

		final ArrayList<String> problems = new ArrayList<String>();
		// handler for processing events and handling error
		DefaultHandler handler = new DefaultHandler() {

			@Override
			public void warning(SAXParseException e) throws SAXException {
				fail(e);
			}

			private void fail(SAXParseException e) {
				problems.add(e.getMessage());
			}

			@Override
			public void fatalError(SAXParseException e) throws SAXException {
				fail(e);
			}

			@Override
			public void error(SAXParseException e) throws SAXException {
				fail(e);
			}

		};

		// parse the XML and report events and errors (if any) to the handler
		saxparser.parse(new ByteArrayInputStream(content.getBytes()), handler);

		if (problems.size() > 0) {
			return CollectionUtils.getCommaSeparated(problems.toArray());
		} else {
			return null;
		}
	}
}
