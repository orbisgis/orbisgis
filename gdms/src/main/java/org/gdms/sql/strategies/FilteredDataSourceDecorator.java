package org.gdms.sql.strategies;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.values.BooleanValue;
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
	private DataSource source;

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
		this.source = source;
		this.whereExpression = whereExpression;
	}

	public Value[] aggregatedFilter(InstructionContext ic, Expression[] fields)
			throws IncompatibleTypesException, DriverException,
			EvaluationException, IOException {
		Value[] aggregatedValues = new Value[fields.length];
		indexes = IndexFactory.createVariableIndex();
		indexes.open();

		int[] index = new int[1];
		ic.setNestedForIndexes(index);
		for (index[0] = 0; index[0] < source.getRowCount(); index[0]++) {
			try {
				if (((BooleanValue) whereExpression.evaluateExpression())
						.getValue()) {
					indexes.addIndex(index[0]);
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
	public void filtrar(InstructionContext ic) throws DriverException, IOException,
			SemanticException, EvaluationException {
		indexes = IndexFactory.createVariableIndex();
		indexes.open();

		int[] index = new int[1];
		ic.setNestedForIndexes(index);
		for (index[0] = 0; index[0] < source.getRowCount(); index[0]++) {
			try {
				if (((BooleanValue) whereExpression.evaluateExpression())
						.getValue()) {
					indexes.addIndex(index[0]);
				}
			} catch (ClassCastException e) {
				throw new IncompatibleTypesException(
						"where expression is not boolean", e);
			}
		}
	}

	/**
	 * @see org.gdms.data.DataSource#open()
	 */
	public void open() throws DriverException {
		source.open();
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
		source.cancel();

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
		return source.getFieldIndexByName(fieldName);
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
		return new OperationLayerMemento(getName(), new Memento[] { source
				.getMemento() }, getSQL());
	}

	public Metadata getMetadata() throws DriverException {
		return source.getMetadata();
	}

	public boolean isOpen() {
		return source.isOpen();
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		try {
			return source.getFieldValue(indexes.getIndex(rowIndex), fieldId);
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	public long getRowCount() throws DriverException {
		return indexes.getIndexCount();
	}
}