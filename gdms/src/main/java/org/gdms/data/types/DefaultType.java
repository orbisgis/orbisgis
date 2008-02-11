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
package org.gdms.data.types;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gdms.data.values.Value;

public class DefaultType implements Type {
	private Constraint[] constraints;

	private String description;

	private int typeCode;

	public static Map<Integer, String> typesDescription = new HashMap<Integer, String>();

	static {
		java.lang.reflect.Field[] fields = Type.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				typesDescription.put((Integer) fields[i].get(null), fields[i]
						.getName());
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
		}
	}

	/**
	 * @param description
	 * @param typeCode
	 * @throws InvalidTypeException
	 */
	DefaultType(final String description, final int typeCode)
			throws InvalidTypeException {
		this(new Constraint[0], description, typeCode);
	}

	/**
	 * @param constraints
	 * @param description
	 * @param typeCode
	 * @throws InvalidTypeException
	 */
	DefaultType(final Constraint[] constraints,
			final String description, final int typeCode) {
		if (null == constraints) {
			this.constraints = new Constraint[0];
		} else {
			this.constraints = constraints;
		}
		this.description = description;
		this.typeCode = typeCode;

		// In case of a geometric type, the GeometryConstraint is mandatory
		if (Type.GEOMETRY == typeCode) {
			if (null == getConstraint(ConstraintNames.GEOMETRY)) {
				final List<Constraint> lc = new LinkedList<Constraint>(Arrays
						.asList(constraints));
				lc.add(new GeometryConstraint());
				this.constraints = (Constraint[]) lc.toArray(new Constraint[lc
						.size()]);
			}
		}
	}

	/**
	 * @see org.gdms.data.types.Type#getConstraints()
	 */
	public Constraint[] getConstraints() {
		return constraints;
	}

	/**
	 * @see org.gdms.data.types.Type#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see org.gdms.data.types.Type#getTypeCode()
	 */
	public int getTypeCode() {
		return typeCode;
	}

	public String check(final Value value) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getConstraintValue(final ConstraintNames constraintNames) {
		final Constraint c = getConstraint(constraintNames);
		return (null == c) ? null : c.getConstraintValue();
	}

	public boolean isRemovable() {
		for (Constraint c : constraints) {
			if (!c.allowsFieldRemoval()) {
				return false;
			}
		}

		return true;
	}

	public Constraint getConstraint(final ConstraintNames constraintNames) {
		for (Constraint c : constraints) {
			if (c.getConstraintName() == constraintNames) {
				return c;
			}
		}
		return null;
	}
}