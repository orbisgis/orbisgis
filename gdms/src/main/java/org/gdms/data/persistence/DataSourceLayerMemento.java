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
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Memento of the DataSource layer
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class DataSourceLayerMemento implements Memento {
	private String tableName;

	private String tableAlias;

	private ContentHandler contentHandler;

	/**
	 * Creates a new DataSourceLayerMemento.
	 * 
	 * @param tableName
	 *            table name
	 * @param tableAlias
	 *            table alias
	 */
	public DataSourceLayerMemento(String tableName, String tableAlias) {
		this.tableName = tableName;
		this.tableAlias = tableAlias;
	}

	/**
	 * Returns the tableAlias.
	 * 
	 * @return Returns the tableAlias.
	 */
	public String getTableAlias() {
		return tableAlias;
	}

	/**
	 * Returns the table name.
	 * 
	 * @return Returns the tableName.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @see org.gdms.data.persistence.Memento#setContentHandler(org.xml.sax.ContentHandler)
	 */
	public void setContentHandler(ContentHandler saxHandler) {
		this.contentHandler = saxHandler;
	}

	/**
	 * @see org.gdms.data.persistence.Memento#getXML()
	 */
	public void getXML() throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "table-name", "table-name", "string", tableName);
		atts.addAttribute("", "table-alias", "table-alias", "string",
				tableAlias);
		contentHandler.startElement("", "table", "table", atts);
		contentHandler.endElement("", "table", "table");
	}
}
