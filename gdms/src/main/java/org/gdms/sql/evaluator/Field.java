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

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

/**
 * Class that evaluates field references. Before evaluating an expression with
 * field references it is necessary to get all the instances of this class in
 * the expression tree and set the FieldContext and the field index in the
 * context
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class Field extends Operand {

	private String fieldName;
	private String tableName;
	private FieldContext fieldContext;
	private int fieldIndex = -1;
	private boolean outerEvaluation = false;

	public Field(String fieldName) {
		this.fieldName = fieldName;
	}

	public Field(String tableName, String fieldName) {
		this.fieldName = fieldName;
		this.tableName = tableName;
	}

	public Value evaluate() throws EvaluationException {
		try {
			Value value = fieldContext.getFieldValue(fieldIndex);
			return value;
		} catch (DriverException e) {
			throw new EvaluationException("Error evaluating " + this, e);
		}
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getTableName() {
		return tableName;
	}

	public Type getType() throws DriverException {
		return fieldContext.getFieldType(fieldIndex);
	}

	@Override
	public String toString() {
		String ret = "";
		if (tableName != null) {
			ret += tableName + ".";
		}

		ret += fieldName;

		return ret;
	}

	public void setFieldContext(FieldContext fieldContext) {
		this.fieldContext = fieldContext;
	}

	public void setFieldIndex(int fieldIndex) {
		this.fieldIndex = fieldIndex;
	}

	public Field[] getFieldReferences() {
		return new Field[] { this };
	}

	public void validateExpressionTypes() throws IncompatibleTypesException,
			DriverException {
		// always valid
	}

	public Expression cloneExpression() {
		Field ret = new Field(this.tableName, this.fieldName);
		ret.fieldContext = this.fieldContext;
		ret.fieldIndex = this.fieldIndex;

		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Field) {
			Field f = (Field) obj;
			if (tableName == null) {
				return (f.getTableName() == null)
						&& f.getFieldName().equals(fieldName);
			} else {
				return tableName.equals(f.getTableName())
						&& f.getFieldName().equals(fieldName);
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return fieldName.hashCode();
	}

	public boolean replace(Expression expression1, Expression expression2) {
		return false;
	}

	public boolean isLiteral() {
		return false;
	}

	public int getFieldIndex() {
		return fieldIndex;
	}

	public boolean isOuterEvaluation() {
		return outerEvaluation;
	}

	/**
	 * outerEvaluation means that this field is evaluated against the outer
	 * datasource in the join nested loop
	 *
	 * @param inLoop
	 */
	public void setOuterEvaluation(boolean outerEvaluation) {
		this.outerEvaluation = outerEvaluation;
	}

}
