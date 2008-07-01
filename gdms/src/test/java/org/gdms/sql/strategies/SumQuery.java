/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
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
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

/**
 */

public class SumQuery implements CustomQuery {

	/**
	 * @throws QueryException
	 * @see org.gdms.sql.customQuery.CustomQuery#evaluate(DataSourceFactory,
	 *      org.gdms.data.DataSource[], Value[])
	 */
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
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
				res += v.getAsDouble();
			}

			tables[0].close();
		} catch (DriverException e) {
			throw new ExecutionException("Error reading data", e);
		}

		return new SumDriver(res);
	}

	/**
	 * @see org.gdms.sql.customQuery.CustomQuery#getName()
	 */
	public String getName() {
		return "SUMQUERY";
	}

	public String getSqlOrder() {
		return "select SumQuery(myNumericField) from myTable;";
	}

	public String getDescription() {
		return "";
	}

	public Metadata getMetadata(Metadata[] tables) {
		return new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.DOUBLE) }, new String[] { "sum" });
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.ANY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(),
				new Arguments(Argument.STRING) };
	}

}