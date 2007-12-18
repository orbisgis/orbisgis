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

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.Adapter;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;

/**
 * DataSource que a�ade caracter�sticas de proyecci�n sobre campos al DataSource
 * subyacente.
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class ProjectionDataSourceDecorator extends AbstractSecondaryDataSource {
	private DataSource dataSource;

	private Expression[] fields;

	private String[] aliases;

	/**
	 * Creates a new ProjectionDataSourceDecorator object.
	 *
	 * @param source
	 *            DataSource origen de la informaci�n
	 * @param fields
	 *            Con los �ndices de los campos proyectados
	 * @param aliases
	 *            Nombres asignados en la instrucci�n a los campos
	 */
	public ProjectionDataSourceDecorator(DataSource source,
			Expression[] fields, String[] aliases) {
		this.dataSource = source;
		this.fields = fields;
		this.aliases = aliases;
	}

	/**
	 * Dado el �ndice de un campo en la tabla proyecci�n, se devuelve el �ndice
	 * real en el DataSource subyacente
	 *
	 * @param index
	 *            �ndice del campo cuyo �ndice en el DataSource subyacente se
	 *            quiere obtener
	 *
	 * @return �ndice del campo en el DataSource subyacente
	 */
	private Expression getFieldByIndex(int index) {
		return fields[index];
	}

	/**
	 * @see org.gdms.data.DataSource#
	 */
	public void cancel() throws DriverException {
		dataSource.cancel();
	}

	/**
	 * @see org.gdms.data.DataSource#
	 */
	public int getFieldCount() throws DriverException {
		return fields.length;
	}

	/**
	 * @see org.gdms.data.DataSource#
	 */
	public int getFieldIndexByName(String fieldName) throws DriverException {
		/*
		 * Se comprueba si dicho �ndice est� mapeado o la
		 * ProjectionDataSourceDecorator no lo tiene
		 */
		for (int i = 0; i < fields.length; i++) {
			if (fieldName.compareTo(fields[i].getFieldName()) == 0) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * @see org.gdms.data.DataSource#
	 */
	public void open() throws DriverException {
		dataSource.open();
	}

	/**
	 * @see org.gdms.driver.ObjectDriver#getFieldType(int)
	 */
	public Type getFieldType(int i) throws DriverException {
		throw new UnsupportedOperationException(
				"cannot get the field type of an expression");
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[] { dataSource
				.getMemento() }, getSQL());
	}

	public Metadata getMetadata() throws DriverException {
		return new Metadata() {

			public String getFieldName(int fieldId) throws DriverException {
				if (aliases[fieldId] != null) {
					return aliases[fieldId];
				} else {
					String name = fields[fieldId].getFieldName();

					if (name == null) {
						return "unknown" + fieldId;
					} else {
						return name;
					}
				}
			}

			public Type getFieldType(int fieldId) throws DriverException {
				return TypeFactory.createType(fields[fieldId].getType());
			}

			public int getFieldCount() throws DriverException {
				return fields.length;
			}

		};
	}

	public boolean isOpen() {
		return dataSource.isOpen();
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		try {
			Expression exp = getFieldByIndex(fieldId);
			Adapter adapter = (Adapter) exp;
			adapter.getInstructionContext().setNestedForIndexes(
					new int[] { (int) rowIndex });
			return exp.evaluate();
		} catch (EvaluationException e) {
			throw new DriverException(e);
		}
	}

	public long getRowCount() throws DriverException {
		return dataSource.getRowCount();
	}

	public void printStack() {
		System.out.println("<" + this.getClass().getName() + ">");
		dataSource.printStack();
		System.out.println("</" + this.getClass().getName() + ">");
	}

	@Override
	protected String[] getRelatedSourcesDelegating() {
		return dataSource.getReferencedSources();
	}

	@Override
	protected DataSourceFactory getDataSourceFactoryFromDecorated() {
		return dataSource.getDataSourceFactory();
	}

}