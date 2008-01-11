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
package org.gdms.sql.strategies.algebraic;

import java.util.ArrayList;
import java.util.HashSet;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.instruction.SemanticException;
import org.gdms.sql.strategies.PDataSourceDecorator;

public class ScalarProductOp extends DefaultOperator implements Operator {

	private ArrayList<String> tables = new ArrayList<String>();

	private ArrayList<String> aliases = new ArrayList<String>();

	private ArrayList<Metadata> metadatas = new ArrayList<Metadata>();

	private DataSourceFactory dsf;

	public void addTable(DataSourceFactory dsf, String tableRef, String alias)
			throws DriverLoadException, NoSuchTableException,
			DataSourceCreationException {
		this.dsf = dsf;
		tables.add(tableRef);
		aliases.add(alias);
		metadatas.add(null);
	}

	public DataSource getDataSource() throws ExecutionException {
		DataSource[] dss = new DataSource[tables.size()];
		try {
			for (int i = 0; i < dss.length; i++) {
				dss[i] = dsf.getDataSource(tables.get(i));
			}
			return new PDataSourceDecorator(dss);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(e);
		}
	}

	public Metadata getMetadata(String tableName)
			throws AlreadyClosedException, DriverException,
			DriverLoadException, NoSuchTableException,
			DataSourceCreationException, SemanticException {
		int tableIndex = tables.indexOf(tableName);
		int aliasIndex = aliases.indexOf(tableName);

		if ((tableIndex == -1) && (aliasIndex == -1)) {
			return null;
		} else if ((tableIndex != -1) && (aliasIndex != -1)) {
			throw new SemanticException("Ambiguous table reference: "
					+ tableName);
		} else {
			if (tableIndex != -1) {
				return getMetadata(tableIndex);
			} else {
				return getMetadata(aliasIndex);
			}
		}
	}

	private Metadata getMetadata(int index) throws NoSuchTableException,
			DataSourceCreationException, DriverException {
		if (metadatas.get(index) == null) {
			DataSource ds = dsf.getDataSource(tables.get(index));
			ds.open();
			Metadata metadata = ds.getMetadata();
			DefaultMetadata ret = new DefaultMetadata(metadata);
			ds.cancel();
			metadatas.set(index, ret);
		}

		return metadatas.get(index);
	}

	public int getFieldIndexInProduct(String tableName, String fieldName)
			throws AlreadyClosedException, DriverLoadException,
			DriverException, NoSuchTableException, DataSourceCreationException,
			SemanticException {
		int index = tables.indexOf(tableName);
		if (index == -1) {
			index = aliases.indexOf(tableName);
		}

		if (index == -1) {
			return -1;
		} else {
			int ret = 0;
			for (int i = 0; i < index; i++) {
				ret += getMetadata(index).getFieldCount();
			}
			Metadata metadata = getMetadata(tableName);
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				if (fieldName.equals(metadata.getFieldName(i))) {
					ret += i;
				}
			}
			return ret;
		}
	}

	public String[] getTables() {
		return tables.toArray(new String[0]);
	}

	public String[] getAliases() {
		return aliases.toArray(new String[0]);
	}

	/**
	 * Checks that the tables exist, and their aliases doesn't collide
	 *
	 * @throws NoSuchTableException
	 *             if a table in the product does not exist
	 * @throws SemanticException
	 *             if there is a conflict in the table aliases
	 */
	public void validateTableReferences() throws NoSuchTableException,
			SemanticException {
		HashSet<String> refs = new HashSet<String>();
		for (int i = 0; i < tables.size(); i++) {
			String tableName = tables.get(i);
			if (!dsf.exists(tableName)) {
				throw new NoSuchTableException(tableName);
			}

			String ref = tableName;
			String alias = aliases.get(i);
			if (alias != null) {
				ref = alias;
			}
			if (refs.contains(ref)) {
				throw new SemanticException("Ambiguous table reference: "
						+ ref);
			}
		}

	}

}
