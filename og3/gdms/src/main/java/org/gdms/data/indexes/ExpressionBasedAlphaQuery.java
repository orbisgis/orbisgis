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
package org.gdms.data.indexes;

import java.util.ArrayList;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.Literal;

public class ExpressionBasedAlphaQuery implements ExpressionBasedIndexQuery,
		AlphaQuery {

	private Expression min;
	private boolean minIncluded;
	private boolean maxIncluded;
	private Expression max;
	private String fieldName;

	public ExpressionBasedAlphaQuery(String fieldName, Expression exp) {
		this(fieldName, exp, true, exp, true);
	}

	public ExpressionBasedAlphaQuery(String fieldName, Expression min,
			boolean minIncluded, Expression max, boolean maxIncluded) {
		this.min = min;
		this.minIncluded = minIncluded;
		this.max = max;
		this.maxIncluded = maxIncluded;
		this.fieldName = fieldName;

		if (this.min == null) {
			this.min = new Literal(ValueFactory.createNullValue());
		}

		if (this.max == null) {
			this.max = new Literal(ValueFactory.createNullValue());
		}
	}

	public String getFieldName() {
		return fieldName;
	}

	public boolean isStrict() {
		return true;
	}

	public Value getMin() throws EvaluationException {
		return min.evaluate();
	}

	public boolean isMinIncluded() {
		return minIncluded;
	}

	public boolean isMaxIncluded() {
		return maxIncluded;
	}

	public Value getMax() throws EvaluationException {
		return max.evaluate();
	}

	public Field[] getFields() {
		ArrayList<Field> ret = new ArrayList<Field>();
		addFields(ret, min);
		addFields(ret, max);

		return ret.toArray(new Field[0]);
	}

	private void addFields(ArrayList<Field> ret, Expression expression) {
		Field[] fields = expression.getFieldReferences();
		for (Field field : fields) {
			ret.add(field);
		}
	}

}
