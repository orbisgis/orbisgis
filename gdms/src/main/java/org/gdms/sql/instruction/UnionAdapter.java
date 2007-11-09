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
package org.gdms.sql.instruction;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.parser.Token;

/**
 * Adaptador de la instrucci�n UNION
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class UnionAdapter extends Adapter {
	/**
	 * DOCUMENT ME!
	 *
	 * @param table
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	private DataSource getTable(int table) throws NoSuchTableException,
			DataSourceCreationException, ExecutionException,
			DriverLoadException {
		Adapter hijo = getChilds()[table];

		if (hijo instanceof TableRefAdapter) {
			String name = Utilities.getText(hijo.getEntity());

			return getTableByName(name);
		} else if (hijo instanceof SelectAdapter) {
			return getTableBySelect((SelectAdapter) hijo);
		} else {
			throw new IllegalStateException("Cannot create the DataSource");
		}
	}

	/**
	 * @throws ExecutionException
	 * @throws DataSourceCreationException
	 * @see org.gdms.sql.instruction.UnionInstruction#getFirstTable()
	 */
	public DataSource getFirstTable() throws NoSuchTableException,
			DataSourceCreationException, ExecutionException,
			DriverLoadException {
		return getTable(0);
	}

	/**
	 * Obtiene el data source a partir de una select
	 *
	 * @param select
	 *
	 * @return
	 */
	private DataSource getTableBySelect(SelectAdapter select)
			throws DriverLoadException, NoSuchTableException,
			ExecutionException {
		SimpleNode node = select.getEntity();
		Token t = node.first_token;
		StringBuilder sql = new StringBuilder("");
		while (t != node.last_token) {
			sql.append(t.image).append(" ");
			t = t.next;
		}
		sql.append(t.image).append(" ");

		return getInstructionContext().getDSFactory().executeSQL(
				sql.toString(), DataSourceFactory.NORMAL);
	}

	/**
	 * Obtiene un data source por el nombre
	 *
	 * @param name
	 *
	 * @return
	 *
	 * @throws TableNotFoundException
	 *             Si nop hay ninguna tabla con el nombre 'name'
	 * @throws CreationException
	 * @throws NoSuchTableException
	 * @throws DriverLoadException
	 * @throws DriverException
	 * @throws DataSourceCreationException
	 * @throws RuntimeException
	 */
	private DataSource getTableByName(String name) throws DriverLoadException,
			NoSuchTableException, DataSourceCreationException {
		String[] tabla = name.split(" ");

		if (tabla.length == 1) {
			return getInstructionContext().getDSFactory().getDataSource(name,
					DataSourceFactory.NORMAL);
		} else {
			return getInstructionContext().getDSFactory().getDataSource(
					tabla[0], tabla[1], DataSourceFactory.NORMAL);
		}
	}

	/**
	 * @throws ExecutionException
	 * @throws DataSourceCreationException
	 * @see org.gdms.sql.instruction.UnionInstruction#getSecondTable()
	 */
	public DataSource getSecondTable() throws DriverLoadException,
			NoSuchTableException, DataSourceCreationException,
			ExecutionException {
		return getTable(1);
	}

	public String[] getSources() {
		ArrayList<String> ret = new ArrayList<String>();

		for (int i = 0; i < 2; i++) {
			Adapter child = getChilds()[i];

			if (child instanceof TableRefAdapter) {
				String name = Utilities.getText(child.getEntity());
				String[] nameAndAlias = name.split(" ");
				ret.add(nameAndAlias[0]);
			}
		}

		return ret.toArray(new String[0]);
	}
}
