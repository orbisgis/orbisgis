package org.gdms.sql.strategies;

import java.util.ArrayList;
import java.util.HashSet;

import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.evaluator.Field;

public class ScalarProductOp extends AbstractOperator implements Operator,
		ChangesMetadata {

	private ArrayList<String> tables = new ArrayList<String>();

	private ArrayList<String> aliases = new ArrayList<String>();

	public void addTable(Operator operator, String tableName, String tableAlias) {
		addChild(operator);
		tables.add(tableName);
		aliases.add(tableAlias);
	}

	public ObjectDriver getResultContents() throws ExecutionException {
		ObjectDriver[] dss = new ObjectDriver[getOperatorCount()];
		for (int i = 0; i < dss.length; i++) {
			dss[i] = getOperator(i).getResult();
		}
		try {
			return new ProductDriver(dss, getResultMetadata());
		} catch (DriverException e) {
			throw new ExecutionException("Cannot create scalar product", e);
		}
	}

	public String[] getAliases() {
		return aliases.toArray(new String[0]);
	}

	/**
	 * Checks that the tables exist and their aliases doesn't collide
	 *
	 * @throws NoSuchTableException
	 *             if a table in the product does not exist
	 * @throws SemanticException
	 *             if there is a conflict in the table aliases
	 * @throws DriverException
	 */
	public void validateTableReferences() throws NoSuchTableException,
			SemanticException, DriverException {
		HashSet<String> refs = new HashSet<String>();
		for (int i = 0; i < tables.size(); i++) {
			String tableName = tables.get(i);

			String ref = tableName;
			String alias = aliases.get(i);
			if (alias != null) {
				ref = alias;
			}
			if (refs.contains(ref)) {
				throw new SemanticException("Ambiguous table reference: " + ref);
			} else {
				refs.add(ref);
			}
		}

		super.validateTableReferences();
	}

	/**
	 * The resulting metadata in a scalar product consist of the metadata in the
	 * first child operator plus the metadata in the second child operator and
	 * so on
	 *
	 * @see org.gdms.sql.strategies.Operator#getResultMetadata()
	 */
	public Metadata getResultMetadata() throws DriverException {
		DefaultMetadata ret = new DefaultMetadata();
		for (int i = 0; i < getOperatorCount(); i++) {
			ret.addAll(getOperator(i).getResultMetadata());
		}

		return ret;
	}

	public int getFieldIndex(Field field) throws DriverException,
			SemanticException {
		String tableName = field.getTableName();
		String fieldName = field.getFieldName();
		int fieldIndex = -1;
		// If the field contains a table reference we look just it
		if (tableName != null) {
			Metadata metadata = getMetadata(tableName);
			if (metadata != null) {
				fieldIndex = getFieldIndexInProduct(tableName, fieldName);
			}
		} else {
			// If the field doesn't contain a table reference
			// iterate over the metadata of the tables
			Metadata metadata = getResultMetadata();
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				if (metadata.getFieldName(i).equals(fieldName)) {
					if (fieldIndex != -1) {
						throw new SemanticException(
								"Ambiguous field reference: " + fieldName);
					}
					fieldIndex = i;
				}
			}
		}

		if (fieldIndex == -1) {
			throw new SemanticException("Field not found: " + field.toString());
		} else {
			return fieldIndex;
		}
	}

	Metadata getMetadata(String tableName) throws DriverException,
			SemanticException {
		int tableIndex = tables.indexOf(tableName);
		int aliasIndex = aliases.indexOf(tableName);

		if ((tableIndex == -1) && (aliasIndex == -1)) {
			return null;
		} else if ((tableIndex != -1) && (aliasIndex != -1)) {
			throw new SemanticException("Ambiguous table reference: "
					+ tableName);
		} else {
			if (tableIndex != -1) {
				return getOperator(tableIndex).getResultMetadata();
			} else {
				return getOperator(aliasIndex).getResultMetadata();
			}
		}
	}

	private int getFieldIndexInProduct(String tableName, String fieldName)
			throws DriverException, SemanticException {
		int index = tables.indexOf(tableName);
		if (index == -1) {
			index = aliases.indexOf(tableName);
		}

		if (index == -1) {
			return -1;
		} else {
			int ret = 0;
			for (int i = 0; i < index; i++) {
				ret += getOperator(i).getResultMetadata().getFieldCount();
			}
			Metadata metadata = getMetadata(tableName);
			boolean found = false;
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				if (fieldName.equals(metadata.getFieldName(i))) {
					found = true;
					ret += i;
				}
			}
			if (found) {
				return ret;
			} else {
				return -1;
			}
		}
	}

}
