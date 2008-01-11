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

import java.util.ArrayList;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A memento from the operation layer
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class OperationLayerMemento implements Memento {
	private String sql;

	private ArrayList<Memento> mementos = new ArrayList<Memento>();

	private String name;

	private ContentHandler contentHandler;

	/**
	 * Creates a new OperationLayerMemento.
	 * 
	 * @param name
	 *            DataSource name
	 * @param mementos
	 *            mementos of DataSources involved in the operation
	 * @param sql
	 *            sql query of the operation
	 */
	public OperationLayerMemento(String name, Memento[] mementos, String sql) {
		this.sql = sql;
		this.mementos.clear();

		for (int i = 0; i < mementos.length; i++) {
			this.mementos.add(mementos[i]);
		}

		this.name = name;
	}

	/**
	 * Crea un nuevo OperationLayerMemento.
	 * 
	 * @param name
	 *            DataSource name
	 * @param sql
	 *            sql that originated the DataSource
	 */
	OperationLayerMemento(String name, String sql) {
		this.mementos.clear();
		this.name = name;
		this.sql = sql;
	}

	/**
	 * Adds a memento of a DataSource used in the query of this memento
	 * DataSource
	 * 
	 * @param m
	 *            memento
	 */
	void addMemento(Memento m) {
		this.mementos.add(m);
	}

	/**
	 * Return the number of childs of this memento
	 * 
	 * @return int
	 */
	public int getMementoCount() {
		return mementos.size();
	}

	/**
	 * Returns the ith memento
	 * 
	 * @param i
	 *            index of the memento
	 * 
	 * @return Memento
	 */
	public Memento getMemento(int i) {
		return (Memento) mementos.get(i);
	}

	/**
	 * Returns the SQL that created the DataSource
	 * 
	 * @return Returns the sql.
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @see org.gdms.data.persistence.Memento#setContentHandler(org.xml.sax.ContentHandler)
	 */
	public void setContentHandler(ContentHandler saxHandler) {
		contentHandler = saxHandler;
	}

	/**
	 * @see org.gdms.data.persistence.Memento#getXML()
	 */
	public void getXML() throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "dataSourceName", "dataSourceName", "string",
				name);
		atts.addAttribute("", "sql", "sql", "string", sql);
		contentHandler.startElement("", "operation", "operation", atts);

		Memento[] mementos = (Memento[]) this.mementos.toArray(new Memento[0]);

		for (int i = 0; i < mementos.length; i++) {
			mementos[i].setContentHandler(contentHandler);
			mementos[i].getXML();
		}

		contentHandler.endElement("", "operation", "operation");
	}
}
