package org.gdms.sql.instruction;

import org.gdms.data.InternalDataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
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
	private Expression[] values;

	/**
	 * Gets the InternalDataSource's of the 'tables' clause of the custom query
	 *
	 * @return InternalDataSource array
	 *
	 * @throws SemanticException If there is any semantic error in the tables
	 * 		   clause
	 * @throws NoSuchTableException 
	 * @throws CreationException 
	 * @throws DriverLoadException 
	 * @throws DriverException 
	 * @throws DataSourceCreationException 
	 */
	public InternalDataSource[] getTables() throws DriverLoadException, NoSuchTableException, DataSourceCreationException {
		return ((TableListAdapter) getChilds()[0]).getTables();
	}

	/**
	 * gets the values of the values clause
	 *
	 * @return Expression array
	 */
	public Expression[] getValues() {
		if (values == null) {
			FunctionArgsAdapter fArgs = ((FunctionArgsAdapter) getChilds()[1]);
			Adapter[] exprs = fArgs.getChilds();
			values = new Expression[exprs.length];

			for (int i = 0; i < exprs.length; i++) {
				values[i] = (Expression) exprs[i];
			}
		}

		return values;
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
