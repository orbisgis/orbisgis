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

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.CustomQuery;

/**
 */
public class SumQuery implements CustomQuery {

	/**
	 * @throws QueryException
	 * @see org.gdms.sql.customQuery.CustomQuery#evaluate(DataSourceFactory,
	 *      org.gdms.data.DataSource[], Value[])
	 */
	public DataSource evaluate(DataSourceFactory dsf,
			DataSource[] tables, Value[] values) throws ExecutionException {
		if (tables.length != 1)
			throw new ExecutionException("SUM only operates on one table");
		if (values.length != 1)
			throw new ExecutionException("SUM only operates with one value");

		String fieldName = values[0].toString();
		double res = 0;
		try {

			tables[0].open();

			int fieldIndex = tables[0].getFieldIndexByName(fieldName);
			if (fieldIndex == -1)
				throw new RuntimeException(
						"we found the field name of the expression but could not find the field index?");

			for (int i = 0; i < tables[0].getRowCount(); i++) {
				Value v = tables[0].getFieldValue(i, fieldIndex);
				if (v instanceof NumericValue) {
					res += ((NumericValue) v).doubleValue();
				} else {
					throw new ExecutionException(
							"SUM only operates with numeric fields");
				}
			}

			tables[0].cancel();
		} catch (DriverException e) {
			throw new ExecutionException("Error reading data", e);
		}

		return new SumDataSourceDecorator(dsf, res);
	}

	/**
	 * @see org.gdms.sql.customQuery.CustomQuery#getName()
	 */
	public String getName() {
		return "SUMQUERY";
	}

	public String getDescription() {
		
		return "";
	}
}
