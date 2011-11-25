/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 * 
 * 
 * This file has been imported from gearscape 
 */
package org.gdms.sql.evaluator;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.Operator;
import org.orbisgis.progress.ProgressMonitor;

public class InOperator extends ComparisonOperator implements Expression {

	private Expression refExpr;
	private org.gdms.sql.strategies.Operator select;

	public InOperator(Expression refExpr,
			org.gdms.sql.strategies.Operator select) {
		this.refExpr = refExpr;
		this.select = select;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	protected Value evaluateExpression(ProgressMonitor pm)
			throws EvaluationException, IncompatibleTypesException {
		try {

			pm.startTask("Executing subquery");
			ObjectDriver res = select.getResult(pm);
						
			if (pm.isCancelled()) {
				return ValueFactory.createNullValue();
			}

			for (int i = 0; i < res.getRowCount(); i++) {
				if (res.getFieldValue(i, 0).equals(refExpr.evaluate(pm))
						.getAsBoolean()) {
					return ValueFactory.createValue(true);
				}
			}
			pm.startTask("End subquery");
			
			return ValueFactory.createValue(false);
		} catch (ExecutionException e) {
			throw new EvaluationException("Cannot evaluate subquery", e);
		} catch (DriverException e) {
			throw new EvaluationException("Cannot evaluate subquery", e);
		}
	}

	@Override
	public Expression cloneExpression() {
		return new InOperator(refExpr.cloneExpression(), select);
	}

	@Override
	public Type getType() throws DriverException {
		return TypeFactory.createType(Type.BOOLEAN);
	}

	@Override
	public void validateExpressionTypes() throws IncompatibleTypesException,
			DriverException {
		Type type1 = refExpr.getType();
		Metadata metadata = select.getResultMetadata();
		if (metadata.getFieldCount() != 1) {
			throw new IncompatibleTypesException("nested query result must "
					+ "have just one field");
		} else {
			Type type2 = metadata.getFieldType(0);
			if (type1.getTypeCode() != type2.getTypeCode()) {
				super.validateExpressionTypes(type1, type2);
			}
		}
	}

	@Override
	public Field[] getFieldReferences() {
		return refExpr.getFieldReferences();
	}

	@Override
	public Operator[] getSubqueries() {
		return new Operator[] { select };
	}

}
