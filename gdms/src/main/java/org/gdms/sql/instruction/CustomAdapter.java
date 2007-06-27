package org.gdms.sql.instruction;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.SimpleNode;

import com.hardcode.driverManager.DriverLoadException;

/**
 * Adapter node of the CUSTOM syntax node
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class CustomAdapter extends Adapter {
	private String queryName;

	/**
	 * Gets the DataSource's of the 'tables' clause of the custom query
	 *
	 * @param mode
	 *
	 * @return DataSource array
	 *
	 * @throws SemanticException
	 *             If there is any semantic error in the tables clause
	 * @throws NoSuchTableException
	 * @throws CreationException
	 * @throws DriverLoadException
	 * @throws DriverException
	 * @throws DataSourceCreationException
	 */
	public DataSource[] getTables(int mode) throws DriverLoadException,
			NoSuchTableException, DataSourceCreationException {
		Adapter from = getChilds()[0];
		if (from instanceof CustomFromAdapter) {
			return ((CustomFromAdapter) from).getTables();
		} else {
			return new DataSource[0];
		}
	}

	/**
	 * gets the values of the values clause
	 *
	 * @return Expression array
	 * @throws SemanticException
	 * @throws EvaluationException
	 */
	public Value[] getValues() throws EvaluationException, SemanticException {
		Adapter[] childs = getChilds();
		if (childs[0] instanceof CustomArgsAdapter) {
			return ((CustomArgsAdapter) getChilds()[0]).getValues();
		} else {
			return ((CustomArgsAdapter) getChilds()[1]).getValues();
		}
	}

	/**
	 * gets the name of the custom query
	 *
	 * @return Returns the queryName.
	 */
	public String getQueryName() {
		if (queryName == null) {
			queryName = ((SimpleNode) getEntity()).first_token.next.image;
		}

		return queryName;
	}
}
