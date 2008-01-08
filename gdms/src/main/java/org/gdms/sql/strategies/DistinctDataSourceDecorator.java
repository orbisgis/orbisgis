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

import java.util.Comparator;
import java.util.TreeSet;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;
import org.gdms.sql.instruction.IncompatibleTypesException;
import org.gdms.sql.instruction.InstructionContext;
import org.gdms.sql.internalExceptions.InternalException;
import org.gdms.sql.internalExceptions.InternalExceptionCatcher;
import org.gdms.sql.internalExceptions.InternalExceptionEvent;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonzalez Cortes
 */
public class DistinctDataSourceDecorator extends AbstractSecondaryDataSource {
	private DataSource dataSource;

	private int[] indexes;

	private Expression[] expressions;

	/**
	 * Crea un nuevo DistinctDataSourceDecorator.
	 *
	 * @param ds
	 *            DOCUMENT ME!
	 * @param expressions
	 *            DOCUMENT ME!
	 * @throws DriverException
	 */
	public DistinctDataSourceDecorator(DataSource ds, Expression[] expressions) {
		dataSource = ds;
		this.expressions = expressions;
	}

	/**
	 * @see org.gdms.data.DataSource#open()
	 */
	public void open() throws DriverException {
		dataSource.open();
	}

	/**
	 * @see org.gdms.data.DataSource#cancel()
	 */
	public void cancel() throws DriverException {
		dataSource.cancel();
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[] { dataSource
				.getMemento() }, getSQL());
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @throws DriverException
	 *             DOCUMENT ME!
	 * @throws EvaluationException
	 *             DOCUMENT ME!
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 */
	public void filter(InstructionContext ic) throws DriverException,
			EvaluationException {
		int[] idx = new int[(int) dataSource.getRowCount()];
		TreeSet<Value> h = new TreeSet<Value>(new Comparator<Value>() {
			public int compare(Value o1, Value o2) {
				try {
					Value v1 = (Value) o1;
					Value v2 = (Value) o2;

					if (v1.equals(v2).getAsBoolean()) {
						return 0;
					} else {
						return 1;
					}
				} catch (IncompatibleTypesException e) {
					InternalExceptionCatcher
							.callExceptionRaised(new InternalExceptionEvent(
									DistinctDataSourceDecorator.this,
									new InternalException(
											"Internal error calculating distinct clause",
											e)));
					return 0;
				}
			}
		});
		int index = 0;

		int[] idxs = new int[1];
		ic.setNestedForIndexes(idxs);
		for (idxs[0] = 0; idxs[0] < dataSource.getRowCount(); idxs[0]++) {
			Value[] values;
			if (expressions == null) {
				values = new Value[dataSource.getMetadata()
						.getFieldCount()];
				for (int j = 0; j < values.length; j++) {
					values[j] = dataSource.getFieldValue(idxs[0], j);
				}
			} else {
				values = new Value[expressions.length];
				for (int j = 0; j < values.length; j++) {
					values[j] = expressions[j].evaluate();
				}
			}

			ValueCollection vc = ValueFactory.createValue(values);

			if (!h.contains(vc)) {
				idx[index] = idxs[0];
				index++;
				h.add(vc);
			}
		}

		indexes = new int[index];
		System.arraycopy(idx, 0, indexes, 0, index);
	}

	public Metadata getMetadata() throws DriverException {
		return dataSource.getMetadata();
	}

	public boolean isOpen() {
		return dataSource.isOpen();
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return dataSource.getFieldValue(indexes[(int) rowIndex], fieldId);
	}

	public long getRowCount() throws DriverException {
		return indexes.length;
	}

	public void printStack() {
		System.out.println("<" + this.getClass().getName()+">");
		dataSource.printStack();
		System.out.println("</" + this.getClass().getName()+">");
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