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
package org.gdms.sql.evaluator;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.strategies.DataSourceDriver;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.RowMappedDriver;
import org.gdms.sql.strategies.SemanticException;

public class Evaluator {

	private static final class EvaluatorFieldContext implements FieldContext {
		private final DataSource ds;

		private int index;

		private EvaluatorFieldContext(DataSource ds) {
			this.ds = ds;
		}

		public Value getFieldValue(int fieldId) throws DriverException {
			return ds.getFieldValue(index, fieldId);
		}

		public Type getFieldType(int fieldId) throws DriverException {
			return ds.getMetadata().getFieldType(fieldId);
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

	/**
	 * @param ds
	 * @param node
	 * @return
	 * @throws DriverException
	 *             If there is some error accessing the data source
	 * @throws IncompatibleTypesException
	 *             If there is some semantic error in the tree
	 * @throws SemanticException
	 *             If the field does not exist or the expression contains
	 *             another semantic error
	 * @throws DataSourceCreationException
	 * @throws DriverLoadException
	 */
	public static DataSource filter(final DataSource ds, Expression node)
			throws EvaluationException, DriverException, SemanticException,
			DriverLoadException, DataSourceCreationException {
		ArrayList<Integer> filter = new ArrayList<Integer>();
		Field[] fieldReferences = node.getFieldReferences();
		EvaluatorFieldContext fieldContext = new EvaluatorFieldContext(ds);
		for (Field field : fieldReferences) {
			int fieldIndex = ds.getFieldIndexByName(field.getFieldName());
			if (fieldIndex == -1) {
				throw new SemanticException("The field " + field.getFieldName()
						+ " does not exist.");
			} else {
				field.setFieldContext(fieldContext);
			}
		}
		node.validateTypes();
		for (int i = 0; i < ds.getRowCount(); i++) {
			fieldContext.setIndex(i);
			Value ret = node.evaluate();
			if (ret.getAsBoolean()) {
				filter.add(i);
			}
		}

		return ds.getDataSourceFactory().getDataSource(
				new RowMappedDriver(new DataSourceDriver(ds), filter));

	}
}
