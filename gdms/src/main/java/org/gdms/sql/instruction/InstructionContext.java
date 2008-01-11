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
package org.gdms.sql.instruction;

import java.util.HashMap;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * Context information of the executing instruction. The execution of a
 * instruction consists of several nested loops that will transform all the
 * tables into a single one result of the scalar product and a possible filter
 * by the where expression. After that nested loops there will be some aditional
 * loops that will perform some operation on the table (distinct, order, etc)
 */
public class InstructionContext {
	/** query executing */
	private String sql;

	/**
	 * DataSource which data will be used to read field values at expression
	 * evaluation
	 */
	private DataSource ds;

	/** DataSourceFactory involved in the execution */
	private DataSourceFactory dsFactory;

	private DataSource[] fromTables;

	private boolean scalarProductDone = false;

	private HashMap<String, DataSource> namesDataSources;

	private HashMap<String, Integer> tableRefPositionInFrom;

	private int[] nestedForIndexes;

	/**
	 * Gets the datasource of the select instruction without taking into account
	 * the where clause
	 *
	 * @return Returns the ds.
	 */
	public DataSource getDs() {
		return ds;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param ds
	 *            The ds to set.
	 */
	public void setDs(DataSource ds) {
		this.ds = ds;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return Returns the dsFActory.
	 */
	public DataSourceFactory getDSFactory() {
		return dsFactory;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param dsFActory
	 *            The dsFActory to set.
	 */
	public void setDSFActory(DataSourceFactory dsFActory) {
		this.dsFactory = dsFActory;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return Returns the sql.
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param sql
	 *            The sql to set.
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setFromTables(DataSource[] fromTables) throws SemanticException {
		this.fromTables = fromTables;
		namesDataSources = new HashMap<String, DataSource>();
		tableRefPositionInFrom = new HashMap<String, Integer>();
		for (int i = 0; i < fromTables.length; i++) {
			String name = fromTables[i].getName();
			String alias = fromTables[i].getAlias();
			if (name.equals(alias)) {
				alias = null;
			}
			if (namesDataSources.containsKey(name)) {
				throw new SemanticException("Duplicated table reference: "
						+ name);
			}
			namesDataSources.put(name, fromTables[i]);
			tableRefPositionInFrom.put(name, i);
			if (alias != null) {
				if (namesDataSources.containsKey(alias)) {
					throw new SemanticException("Duplicated table reference: "
							+ name);
				}
				namesDataSources.put(alias, fromTables[i]);
				tableRefPositionInFrom.put(alias, i);
			}
		}
	}

	public void scalarProductDone() {
		scalarProductDone = true;
	}

	private int getFieldIndexByName(String tableName, String fieldName)
			throws DriverException, AmbiguousFieldNameException,
			FieldNotFoundException {
		int fieldIndex = -1;
		int tableIndex = -1;
		if (tableName == null) {

			for (int i = 0; i < fromTables.length; i++) {
				int index = fromTables[i].getFieldIndexByName(fieldName);

				if (index != -1) {
					// If there already is one match
					if (fieldIndex != -1) {
						throw new AmbiguousFieldNameException(fieldName);
					} else {
						fieldIndex = index;
						tableIndex = i;
					}
				}
			}
		} else {
			for (int i = 0; i < fromTables.length; i++) {
				if (tableName.equals(fromTables[i].getName())
						|| tableName.equals(fromTables[i].getAlias())) {
					tableIndex = i;
					fieldIndex = fromTables[i].getFieldIndexByName(fieldName);
				}
			}
		}

		if (fieldIndex == -1) {
			throw new FieldNotFoundException(fieldName);
		}

		int ret = 0;
		for (int i = 0; i < tableIndex; i++) {
			ret = ret + fromTables[i].getFieldCount();
		}
		ret = ret + fieldIndex;

		return ret;
	}

	private DataSource getDataSource(String tableName, String fieldName)
			throws DriverException, AmbiguousFieldNameException,
			FieldNotFoundException {
		if (tableName == null) {
			int fieldIndex = -1;
			int tableIndex = -1;

			for (int i = 0; i < fromTables.length; i++) {
				int index = fromTables[i].getFieldIndexByName(fieldName);

				if (index != -1) {
					// If there already is one match
					if (fieldIndex != -1) {
						throw new AmbiguousFieldNameException(fieldName);
					} else {
						fieldIndex = index;
						tableIndex = i;
					}
				}
			}

			if (fieldIndex == -1) {
				throw new FieldNotFoundException(fieldName);
			}

			return fromTables[tableIndex];
		} else {
			return namesDataSources.get(tableName);
		}
	}

	public Value getFieldValue(String tableName, String fieldName)
			throws DriverException, AmbiguousFieldNameException,
			FieldNotFoundException {
		int index;
		int fieldId;
		DataSource dataSource;
		if (scalarProductDone) {
			index = nestedForIndexes[0];
			dataSource = this.ds;
			fieldId = getFieldIndexByName(tableName, fieldName);
		} else {
			index = nestedForIndexes[tableRefPositionInFrom.get(tableName)];
			dataSource = getDataSource(tableName, fieldName);
			fieldId = dataSource.getFieldIndexByName(fieldName);
		}

		if (fieldId == -1) {
			throw new FieldNotFoundException(fieldName);
		}
		return dataSource.getFieldValue(index, fieldId);
	}

	public String getTableName(String tableName, String fieldName)
			throws AmbiguousFieldNameException, FieldNotFoundException,
			DriverException {
		return getDataSource(tableName, fieldName).getName();
	}

	public void setDsFactory(DataSourceFactory dsFactory) {
		this.dsFactory = dsFactory;
	}

	public void setNamesDataSources(HashMap<String, DataSource> namesDataSources) {
		this.namesDataSources = namesDataSources;
	}

	public void setNestedForIndexes(int[] nestedForIndexes) {
		this.nestedForIndexes = nestedForIndexes;
	}

	public void setScalarProductDone(boolean scalarProductDone) {
		this.scalarProductDone = scalarProductDone;
	}

	public boolean isBeingIterated(String tableName) {
		if (tableName == null) {
			return false;
		} else {
			return nestedForIndexes[tableRefPositionInFrom.get(tableName)] != -1;
		}
	}

}