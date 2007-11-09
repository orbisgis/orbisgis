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

import java.sql.Connection;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * DataSource que hace la union de dos datasources
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class UnionDataSourceDecorator extends AbstractSecondaryDataSource {
	private DataSource dataSource1;

	private DataSource dataSource2;

	/**
	 * Creates a new UnionDataSourceDecorator object.
	 *
	 * @param ds1
	 *            Primera tabla de la union
	 * @param ds2
	 *            Segunda tabla de la union
	 */
	public UnionDataSourceDecorator(DataSource ds1, DataSource ds2) {
		dataSource1 = ds1;
		dataSource2 = ds2;
	}

	/**
	 * @see org.gdms.data.DataSource#open()
	 */
	public void open() throws DriverException {
		dataSource1.open();

		try {
			dataSource2.open();
		} catch (DriverException e) {
			dataSource1.cancel();

			throw e;
		}
	}

	/**
	 * @see org.gdms.data.DataSource#close(Connection)
	 */
	public void cancel() throws DriverException {
		dataSource1.cancel();
		dataSource2.cancel();
	}

	/**
	 * @see org.gdms.data.FieldNameAccess#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) throws DriverException {
		return dataSource1.getFieldIndexByName(fieldName);
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[] {
				dataSource1.getMemento(), dataSource2.getMemento() }, getSQL());
	}

	public Metadata getMetadata() throws DriverException {
		return dataSource1.getMetadata();
	}

	public boolean isOpen() {
		return dataSource1.isOpen();
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		long tamTabla1 = dataSource1.getRowCount();

		if (rowIndex < tamTabla1) {
			return dataSource1.getFieldValue(rowIndex, fieldId);
		} else {
			return dataSource2.getFieldValue(rowIndex - tamTabla1, fieldId);
		}
	}

	public long getRowCount() throws DriverException {
		return dataSource1.getRowCount() + dataSource2.getRowCount();
	}

	public void printStack() {
		System.out.println("<" + this.getClass().getName() + ">");
		dataSource1.printStack();
		dataSource2.printStack();
		System.out.println("</" + this.getClass().getName() + ">");
	}

	@Override
	protected String[] getRelatedSourcesDelegating() {
		String[] rs1 = dataSource1.getReferencedSources();
		String[] rs2 = dataSource2.getReferencedSources();
		String[] ret = new String[rs1.length + rs2.length];
		System.arraycopy(rs1, 0, ret, 0, rs1.length);
		System.arraycopy(rs2, 0, ret, rs1.length, rs2.length);

		return ret;
	}

	@Override
	protected DataSourceFactory getDataSourceFactoryFromDecorated() {
		return dataSource1.getDataSourceFactory();
	}

}