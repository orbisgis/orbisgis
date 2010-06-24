/*
 * GearScape is a Geographic Information System focused on geo-processing. 
 * It is able to retrieve, process and display spatial data of both vector 
 * and raster type. GearScape is distributed under GPL 3 license.
 *
 * Copyright (C) 2009 the GearScape team
 *
 * This file is part of the GearScape's project source code.
 *
 * GearScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GearScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GearScape. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://gearscape.forge.osor.eu/>
 */
package org.gdms.sql.evaluator;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.Operator;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.utils.I18N;

public class SelectResultOperator extends Operand implements Expression {

	private Operator operator;

	public SelectResultOperator(Operator op) {
		this.operator = op;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public Expression cloneExpression() {
		return new SelectResultOperator(operator);
	}

	@Override
	public Type getType() throws DriverException {
		return operator.getResultMetadata().getFieldType(0);
	}

	@Override
	protected void validateExpressionTypes() throws IncompatibleTypesException,
			DriverException {
		Metadata metadata = operator.getResultMetadata();
		if (metadata.getFieldCount() != 1) {
			throw new IncompatibleTypesException(I18N
					.getText("nested query result must "
							+ "have just one field"));
		}
	}

	@Override
	public Value evaluate() throws EvaluationException {
		try {
			ObjectDriver res = operator.getResult(new NullProgressMonitor());
			return res.getFieldValue(0, 0);
		} catch (ExecutionException e) {
			throw new EvaluationException(I18N
					.getText("Cannot evaluate subquery"), e);
		} catch (DriverException e) {
			throw new EvaluationException(I18N
					.getText("Cannot evaluate subquery"), e);
		}
	}

	@Override
	public Field[] getFieldReferences() {
		return new Field[0];
	}

	@Override
	public Operator[] getSubqueries() {
		return new Operator[] { operator };
	}

	public Operator getSQLOperator() {
		return operator;
	}

	@Override
	public boolean replace(Expression expression1, Expression expression2) {
		return false;
	}

}
