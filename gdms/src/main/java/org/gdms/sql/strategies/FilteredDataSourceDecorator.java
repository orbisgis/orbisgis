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

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.indexes.IndexFactory;
import org.gdms.sql.indexes.VariableIndexSet;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;
import org.gdms.sql.instruction.IncompatibleTypesException;
import org.gdms.sql.instruction.InstructionContext;
import org.gdms.sql.instruction.SemanticException;

/**
 * Representa una fuente de datos que contiene una cl�usula where mediante la
 * cual se filtran los campos
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class FilteredDataSourceDecorator extends AbstractSecondaryDataSource {

	private static final Logger logger = Logger
			.getLogger(FilteredDataSourceDecorator.class);

	private DataSource dataSource;

	private Expression whereExpression;

	private VariableIndexSet indexes;

	/**
	 * Creates a new FilteredDataSourceDecorator object.
	 *
	 * @param source
	 *            DataSource que se va a filtrar
	 * @param whereExpression
	 *            Expresi�n de la cl�usula where
	 */
	public FilteredDataSourceDecorator(DataSource source,
			Expression whereExpression) {
		this.dataSource = source;
		this.whereExpression = whereExpression;
	}

	public Value[] aggregatedFilter(InstructionContext ic, Expression[] fields)
			throws IncompatibleTypesException, DriverException,
			EvaluationException, IOException {
		Value[] aggregatedValues = new Value[fields.length];

		int[] index = new int[1];
		ic.setNestedForIndexes(index);
		for (index[0] = 0; index[0] < dataSource.getRowCount(); index[0]++) {
			try {
				if (whereExpression.evaluateExpression().getAsBoolean()) {
					for (int j = 0; j < aggregatedValues.length; j++) {
						aggregatedValues[j] = fields[j].evaluate();
					}
				}
			} catch (ClassCastException e) {
				throw new IncompatibleTypesException(
						"where expression is not boolean", e);
			}
		}

		return aggregatedValues;
	}

	/**
	 * M�todo que construye el array de �ndices de las posiciones que las filas
	 * filtradas ocupan en el DataSource origen
	 *
	 * @throws DriverException
	 *             Si se produce un fallo en el driver al acceder a los datos
	 * @throws IOException
	 *             Si se produce un error usando las estructuras de datos
	 *             internas
	 * @throws SemanticException
	 *             Si se produce alg�n error sem�ntico al evaluar la expresi�n
	 * @throws IncompatibleTypesException
	 *             Si la expresi�n where no evalua a booleano
	 * @throws EvaluationException
	 *             If the expression evaluation fails
	 */
	public void filtrar(InstructionContext ic) throws DriverException,
			IOException, SemanticException, EvaluationException {
		indexes = IndexFactory.createVariableIndex();
		indexes.open();

		int[] index = new int[1];
		ic.setNestedForIndexes(index);
		logger.debug("filtering...");
		for (index[0] = 0; index[0] < dataSource.getRowCount(); index[0]++) {
			try {
				Value whereValue = whereExpression.evaluateExpression();
				if (whereValue.getType() != Type.NULL) {
					if (whereValue.getAsBoolean()) {
						indexes.addIndex(index[0]);
					}
				}
			} catch (ClassCastException e) {
				throw new IncompatibleTypesException(
						"where expression is not boolean", e);
			}
		}
		logger.debug("filter done");
	}

	/**
	 * @see org.gdms.data.DataSource#open()
	 */
	public void open() throws DriverException {
		dataSource.open();
		try {
			indexes.open();
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * @see org.gdms.data.DataSource#close(Connection)
	 */
	public void cancel() throws DriverException {
		dataSource.cancel();

		try {
			indexes.close();
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * @see org.gdms.data.FieldNameAccess#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) throws DriverException {
		return dataSource.getFieldIndexByName(fieldName);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IOException
	 *
	 * @see org.gdms.data.DataSource#getWhereFilter()
	 */
	public long[] getWhereFilter() throws IOException {
		ArrayList<Long> list = indexes.getIndexes();
		long[] ret = new long[list.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = list.get(i);
		}

		return ret;
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[] { dataSource
				.getMemento() }, getSQL());
	}

	public Metadata getMetadata() throws DriverException {
		return dataSource.getMetadata();
	}

	public boolean isOpen() {
		return dataSource.isOpen();
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		try {
			return dataSource
					.getFieldValue(indexes.getIndex(rowIndex), fieldId);
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	public long getRowCount() throws DriverException {
		return indexes.getIndexCount();
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