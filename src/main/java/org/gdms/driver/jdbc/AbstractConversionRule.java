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
package org.gdms.driver.jdbc;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;

public abstract class AbstractConversionRule implements ConversionRule {

	public String getSQL(String fieldName, Type fieldType) {
		return "\"" + fieldName + "\" " + getTypeName()
				+ getGlobalConstraintExpr(fieldType);
	}

	protected String getGlobalConstraintExpr(Type fieldType) {
		StringBuilder ret = new StringBuilder("");
		boolean notNull = fieldType.getBooleanConstraint(Constraint.NOT_NULL);
		if (notNull) {
			ret.append(" NOT NULL ");
		}

		boolean unique = fieldType.getBooleanConstraint(Constraint.UNIQUE);
		if (unique) {
			ret.append(" UNIQUE ");
		}

		return ret.toString();
	}

	public int[] getValidConstraints() {
		return addGlobalConstraints(new int[0]);
	}

	public int[] addGlobalConstraints(int... constraints) {
		int[] ret = new int[constraints.length + 4];
		System.arraycopy(constraints, 0, ret, 0, constraints.length);
		ret[constraints.length] = Constraint.NOT_NULL;
		ret[constraints.length + 1] = Constraint.PK;
		ret[constraints.length + 2] = Constraint.READONLY;
		ret[constraints.length + 3] = Constraint.UNIQUE;

		return ret;
	}

	public Type createType() throws InvalidTypeException {
		return createType(new Constraint[0]);
	}

	protected abstract int getOutputTypeCode();

	public Type createType(Constraint[] constraints)
			throws InvalidTypeException {
		int[] allowed = getValidConstraints();
		for (Constraint constraint : constraints) {
			if (!contains(allowed, constraint
					.getConstraintCode())) {
				throw new InvalidTypeException("Cannot use "
						+ constraint.getConstraintCode() + " in "
						+ getTypeName() + " type");
			}
		}
		return TypeFactory.createType(getOutputTypeCode(), getTypeName(),
				constraints);
	}

	private boolean contains(int[] allowed, int constraintCode) {
		for (int object : allowed) {
			if (object == constraintCode) {
				return true;
			}
		}

		return false;
	}

}