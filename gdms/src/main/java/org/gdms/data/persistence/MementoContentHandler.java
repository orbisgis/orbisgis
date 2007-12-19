/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
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
package org.gdms.data.persistence;

import java.util.Stack;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * ContentHandler that receives SAXEvents and generates a DataSource
 *
 * @author Fernando Gonzalez Cortes
 */
public class MementoContentHandler implements ContentHandler {
	private Stack<Memento> mementos = new Stack<Memento>();

	private Memento root;

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	public void endPrefixMapping(String prefix) throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	public void skippedEntity(String name) throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator locator) {
	}

	/**
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
	 *      java.lang.String)
	 */
	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
	 *      java.lang.String)
	 */
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (("operation".equals(localName)) || ("table".equals(localName))) {
			root = (Memento) mementos.pop();
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if ("operation".equals(localName)) {
			OperationLayerMemento memento = new OperationLayerMemento(atts
					.getValue("dataSourceName"), atts.getValue("sql"));

			if (mementos.size() > 0) {
				Memento m = (Memento) mementos.peek();

				if (m instanceof OperationLayerMemento) {
					OperationLayerMemento mem = (OperationLayerMemento) m;
					mem.addMemento(memento);
				} else {
					throw new RuntimeException(
							"No table inside table is allowed");
				}
			}

			mementos.push(memento);
		} else if ("table".equals(localName)) {
			DataSourceLayerMemento memento = new DataSourceLayerMemento(atts
					.getValue("table-name"), atts.getValue("table-alias"));
			mementos.push(memento);
		}
	}

	/**
	 * Get's the root memento of the XML parsed. Null if no parse has been done
	 *
	 * @return The memento
	 */
	public Memento getRoot() {
		return root;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param m
	 *            DOCUMENT ME!
	 * @param dsf
	 *            DOCUMENT ME!
	 * @param mode
	 *            DOCUMENT ME!
	 */
	private DataSource createDataSource(Memento m, DataSourceFactory dsf)
			throws SyntaxException, DriverLoadException, NoSuchTableException,
			ExecutionException, DataSourceCreationException {
		if (m instanceof OperationLayerMemento) {
			OperationLayerMemento olm = (OperationLayerMemento) m;

			for (int i = 0; i < olm.getMementoCount(); i++) {
				createDataSource(olm.getMemento(i), dsf);
			}

			return dsf.executeSQL(olm.getSql());
		} else if (m instanceof DataSourceLayerMemento) {
			DataSourceLayerMemento dslm = (DataSourceLayerMemento) m;

			return dsf.getDataSource(dslm.getTableName(), dslm.getTableAlias());
		}

		throw new RuntimeException("unrecognized data source type");
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param dsf
	 *            DOCUMENT ME!
	 * @param mode
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public DataSource getDataSource(DataSourceFactory dsf)
			throws SyntaxException, DriverLoadException, NoSuchTableException,
			ExecutionException, DataSourceCreationException {
		return createDataSource(root, dsf);
	}
}
