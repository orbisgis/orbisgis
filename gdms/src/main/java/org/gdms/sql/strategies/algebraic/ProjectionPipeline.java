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
package org.gdms.sql.strategies.algebraic;

import org.gdms.data.AbstractDataSourceDecorator;
import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class ProjectionPipeline extends AbstractDataSourceDecorator {

	private Expression[] fields;

	private String[] aliases;

	/**
	 * Builds a ProjectionPipeline that will have the same number of rows and in
	 * the same order as the specified DataSource. The name of those fields are
	 * the alias if it is specified. Otherwise, the name of the field in the
	 * expression is used only if the expression consists only of a field
	 * reference. If it doesn't the string 'expr' and the number of the field is
	 * used
	 *
	 * @param source
	 * @param fields
	 * @param aliases
	 */
	public ProjectionPipeline(DataSource source, Expression[] fields,
			String[] aliases) {
		super(source);
		this.fields = fields;
		this.aliases = aliases;
	}

	private Expression getFieldByIndex(int index) {
		return fields[index];
	}

	/**
	 * @see org.gdms.data.DataSource#
	 */
	public int getFieldCount() throws DriverException {
		return fields.length;
	}

	public Metadata getMetadata() throws DriverException {
		return new Metadata() {

			public String getFieldName(int fieldId) throws DriverException {
				if (aliases[fieldId] != null) {
					return aliases[fieldId];
				} else {
					if (fields[fieldId] instanceof Field) {
						return ((Field)fields[fieldId]).getFieldName();
					} else {
						return "expr" + fieldId;
					}
				}
			}

			public Type getFieldType(int fieldId) throws DriverException {
				return TypeFactory.createType(fields[fieldId].getType());
			}

			public int getFieldCount() throws DriverException {
				return fields.length;
			}

		};
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		try {
			Expression exp = getFieldByIndex(fieldId);
			exp.getEvaluationContext().setRowIndex(rowIndex);
			exp.getEvaluationContext().setDs(getDataSource());
			return exp.evaluate();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public void printStack() {
		System.out.println("<" + this.getClass().getName() + ">");
		getDataSource().printStack();
		System.out.println("</" + this.getClass().getName() + ">");
	}
}