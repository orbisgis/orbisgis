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

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.parser.SimpleNode;

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

	public String[] getSources() {
		Adapter from = getChilds()[0];
		if (from instanceof CustomFromAdapter) {
			return ((CustomFromAdapter) from).getSources();
		} else {
			return new String[0];
		}
	}
}
