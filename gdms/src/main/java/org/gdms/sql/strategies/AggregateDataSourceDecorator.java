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
package org.gdms.sql.strategies;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * @author Fernando Gonzalez Cortes
 */
public class AggregateDataSourceDecorator extends AbstractSecondaryDataSource {

	private Value[] values;

	private String[] names;

	private DataSource ds;

	/**
	 * @param aggregateds
	 */
	public AggregateDataSourceDecorator(DataSource ds, Value[] aggregateds) {
		this.ds = ds;
		this.values = aggregateds;
		names = new String[aggregateds.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = "expr" + i;
		}
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		return values.length;
	}

	public Metadata getMetadata() throws DriverException {
		return new Metadata() {

			public String getFieldName(int fieldId) throws DriverException {
				return names[fieldId];
			}

			public Type getFieldType(int fieldId) throws DriverException {
				return TypeFactory.createType(values[fieldId].getType());
			}

			public int getFieldCount() throws DriverException {
				return names.length;
			}

		};
	}

	public boolean isOpen() {
		return true;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return values[fieldId];
	}

	public long getRowCount() throws DriverException {
		return 1;
	}

	public void cancel() throws DriverException, AlreadyClosedException {
	}

	public void open() throws DriverException {
	}

	@Override
	protected String[] getRelatedSourcesDelegating() {
		return ds.getReferencedSources();
	}

	@Override
	protected DataSourceFactory getDataSourceFactoryFromDecorated() {
		return ds.getDataSourceFactory();
	}
}