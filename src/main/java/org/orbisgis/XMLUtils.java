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
