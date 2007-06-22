package org.gdms.sql.instruction;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;

import com.hardcode.driverManager.DriverLoadException;

/**
 * Adapta el nodo que representa una instrucci�n select en el �rbol sint�ctico
 * de entrada
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class SelectAdapter extends Adapter {
	public final static int ORDER_ASC = 0;

	public final static int ORDER_DESC = 1;

	public final static int ORDER_NONE = 2;

	private DataSource dataSource;

	/**
	 * Obtiene las tablas de la cl�usula FROM de la instrucci�n
	 *
	 * @return Tablas de la select
	 *
	 * @throws SemanticException
	 *             Si se produce un error sem�ntico
	 * @throws NoSuchTableException
	 * @throws CreationException
	 * @throws DriverLoadException
	 * @throws DriverException
	 * @throws DataSourceCreationException
	 */
	public DataSource[] getTables() throws DriverLoadException,
			NoSuchTableException, DataSourceCreationException {
		return ((TableListAdapter) getChilds()[1]).getTables(DataSourceFactory.NORMAL);
	}

	/**
	 * Obtiene las expresiones de los campos de la cl�usula SELECT o null si hay
	 * un ''.
	 *
	 * @return Expresiones de los campos
	 */
	public Expression[] getFieldsExpression() {
		return ((SelectColsAdapter) getChilds()[0]).getFieldsExpression();
	}

	/**
	 * Obtiene el alias de los campos. Al igual que getFieldsExpression,
	 * devuelve null si se selecciona ''
	 *
	 * @return Array de strings con los alias
	 */
	public String[] getFieldsAlias() {
		return ((SelectColsAdapter) getChilds()[0]).getFieldsAlias();
	}

	/**
	 * Devuelve true si la palabra clave DISTINCT se us� y false en caso
	 * contrario
	 *
	 * @return Devuelve true si se utiliz� la palabra clave DISTINCT
	 */
	public boolean isDistinct() {
		return ((SelectColsAdapter) getChilds()[0]).isDistinct();
	}

	/**
	 * Gets the OrderBy adapter of the instruction if there is any
	 *
	 * @return OrderByAdapter
	 */
	private OrderByAdapter getOrderByAdapter() {
		Adapter[] hijos = getChilds();

		if (hijos.length < 3) {
			return null;
		}

		for (int i = 2; i < hijos.length; i++) {
			if (hijos[i] instanceof OrderByAdapter) {
				return (OrderByAdapter) hijos[i];
			}
		}

		return null;
	}

	/**
	 * Gets the number of fields specified in the orderby clause or 0 if there
	 * is no such clause
	 *
	 * @return int
	 */
	public int getOrderCriterionCount() {
		OrderByAdapter adapter = getOrderByAdapter();

		if (adapter == null) {
			return 0;
		} else {
			return adapter.getFieldCount();
		}
	}

	/**
	 * Gets the name of the order field in the index-th criterion. Will return
	 * null if there is no orderby clause
	 *
	 * @param index
	 *            index of the order criterion to be guessed
	 *
	 * @return int
	 */
	public String getFieldName(int index) {
		OrderByAdapter adapter = getOrderByAdapter();

		if (adapter == null) {
			return null;
		}

		return adapter.getFieldName(index);
	}

	/**
	 * Gets a constant indicating ascendent or descendent for the index-th
	 * criterion. Will return ORDER_NONE if there is no order by clause,
	 * ORDER_ASC if the index-th criterion is ascending and ORDER_DESC if the
	 * index-th criterion is descending
	 *
	 * @param index
	 *            index of the order criterion to be guessed
	 *
	 * @return int
	 */
	public int getOrder(int index) {
		OrderByAdapter adapter = getOrderByAdapter();

		if (adapter == null) {
			return ORDER_NONE;
		}

		return adapter.getOrder(index);
	}

	/**
	 * Obtiene el origen de datos para los campos a la hora de evaluar las
	 * expresiones
	 *
	 * @return
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Establece el origen de datos para los campos a la hora de evaluar las
	 * expresiones
	 *
	 * @param source
	 */
	public void setDataSource(DataSource source) {
		dataSource = source;
	}

	/**
	 * @see org.gdms.sql.instruction.SelectInstruction#getWhereExpression()
	 */
	public Expression getWhereExpression() {
		Adapter[] hijos = getChilds();

		if (hijos.length < 3) {
			return null;
		}

		if (hijos[2] instanceof WhereAdapter) {
			return ((WhereAdapter) hijos[2]).getExpression();
		} else {
			return null;
		}
	}
}
