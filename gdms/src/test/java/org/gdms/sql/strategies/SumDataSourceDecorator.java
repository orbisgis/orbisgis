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
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

class SumDataSourceDecorator extends AbstractSecondaryDataSource {

	private double sum;

	public SumDataSourceDecorator(DataSourceFactory dsf, double n) {
		sum = n;
		this.dsf = dsf;
	}

	/**
	 * @see org.gdms.data.FieldNameAccess#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) {
		if (fieldName.equals("sum"))
			return 0;
		else
			return -1;
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[0], getSQL());
	}

	public Metadata getMetadata() throws DriverException {
		return new Metadata() {

			public String getFieldName(int fieldId) throws DriverException {
				return "sum";
			}

			public Type getFieldType(int fieldId) throws DriverException {
				return TypeFactory.createType(Type.INT);
			}

			public int getFieldCount() throws DriverException {
				return 1;
			}

		};
	}

	public boolean isOpen() {
		return true;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return ValueFactory.createValue(sum);
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
		return new String[0];
	}

	@Override
	protected DataSourceFactory getDataSourceFactoryFromDecorated() {
		return dsf;
	}

}