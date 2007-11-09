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
package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;

public abstract class ScalarProductDataSource extends
		AbstractSecondaryDataSource {

	protected DataSource[] tables;
	protected long tablesArity;

	/**
	 * Dado un �ndice de campo en la tabla producto, devuelve el �ndice en el
	 * array de tablas de la tabla operando que contiene dicho campo
	 *
	 * @param fieldId
	 *            �ndice del campo en la tabla producto
	 *
	 * @return �ndice de la tabla en el array de tablas
	 *
	 * @throws DriverException
	 *             Si se prouce alg�n error accediendo a la tabla operando
	 */
	protected int getTableIndexByFieldId(int fieldId) throws DriverException {
		int table = 0;

		while (fieldId >= tables[table].getMetadata().getFieldCount()) {
			fieldId -= tables[table].getMetadata().getFieldCount();
			table++;
		}

		return table;
	}

	/**
	 * Dado un �ndice de campo en la tabla producto, devuelve el �ndice en la
	 * tabla operando a la cual pertenence el campo
	 *
	 * @param fieldId
	 *            �ndice en la tabla producto
	 *
	 * @return �ndice en la tabla operando
	 *
	 * @throws DriverException
	 *             Si se prouce alg�n error accediendo a la tabla operando
	 */
	protected int getFieldIndex(int fieldId) throws DriverException {
		int table = 0;

		while (fieldId >= tables[table].getMetadata().getFieldCount()) {
			fieldId -= tables[table].getMetadata().getFieldCount();
			table++;
		}

		return fieldId;
	}

	/**
	 * @see org.gdms.data.DataSource#open(java.io.File)
	 */
	public void open() throws DriverException {
		for (int i = 0; i < tables.length; i++) {
			try {
				tables[i].open();
			} catch (DriverException e) {
				for (int j = 0; j < i; j++) {
					try {
						tables[i].cancel();
					} catch (Exception e1) {
					}
				}

				throw e;
			}
		}

		tablesArity = 1;

		for (int i = 0; i < tables.length; i++) {
			tablesArity *= tables[i].getRowCount();
		}
	}

	public void cancel() throws DriverException {
		for (int i = 0; i < tables.length; i++) {
			tables[i].cancel();
		}
	}

	public void printStack() {
		System.out.println("<" + this.getClass().getName()+">");
		for (DataSource dataSource : tables) {
			dataSource.printStack();
		}
		System.out.println("</" + this.getClass().getName()+">");
	}

	@Override
	protected String[] getRelatedSourcesDelegating() {
		ArrayList<String> ret = new ArrayList<String>();
		for (DataSource ds : tables) {
			String[] relatedSources = ds.getReferencedSources();
			for (String source : relatedSources) {
				ret.add(source);
			}
		}
		return ret.toArray(new String[0]);
	}

	@Override
	protected DataSourceFactory getDataSourceFactoryFromDecorated() {
		return tables[0].getDataSourceFactory();
	}

}
