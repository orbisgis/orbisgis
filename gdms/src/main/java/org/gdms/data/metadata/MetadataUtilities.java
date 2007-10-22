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
package org.gdms.data.metadata;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.ReadOnlyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class MetadataUtilities {
	public static String[] getPKNames(final Metadata metadata)
			throws DriverException {
		final int[] pKIndices = getPKIndices(metadata);
		final String[] pKNames = new String[pKIndices.length];

		for (int i = 0; i < pKNames.length; i++) {
			pKNames[i] = metadata.getFieldName(pKIndices[i]);
		}

		return pKNames;
	}

	public static int[] getPKIndices(final Metadata metadata)
			throws DriverException {
		final int fc = metadata.getFieldCount();
		final List<Integer> tmpPKIndices = new ArrayList<Integer>();

		for (int i = 0; i < fc; i++) {
			final Type type = metadata.getFieldType(i);
			final Constraint[] constraints = type.getConstraints();
			for (Constraint c : constraints) {
				if (ConstraintNames.PK == c.getConstraintName()) {
					tmpPKIndices.add(i);
					break;
				}
			}
		}
		final int[] pkIndices = new int[tmpPKIndices.size()];
		int i = 0;
		for (Integer idx : tmpPKIndices) {
			pkIndices[i++] = idx.intValue();
		}

		return pkIndices;
	}

	public static boolean isReadOnly(final Metadata metadata, final int fieldId)
			throws DriverException {
		final Constraint[] constraints = metadata.getFieldType(fieldId)
				.getConstraints();
		for (Constraint c : constraints) {
			if (c instanceof ReadOnlyConstraint) {
				return true;
			}
		}
		return false;
	}

	public static boolean isPrimaryKey(final Metadata metadata,
			final int fieldId) throws DriverException {
		final Constraint[] constraints = metadata.getFieldType(fieldId)
				.getConstraints();
		for (Constraint c : constraints) {
			if (c instanceof PrimaryKeyConstraint) {
				return true;
			}
		}
		return false;
	}

	public static String check(final Metadata metadata, final int fieldId,
			Value value) throws DriverException {
		final Constraint[] constraints = metadata.getFieldType(fieldId)
				.getConstraints();
		for (Constraint c : constraints) {
			if (null != c.check(value)) {
				return c.check(value);
			}
		}
		return null;
	}

	public static Type[] getFieldTypes(Metadata metadata)
			throws DriverException {
		Type[] fieldTypes = new Type[metadata.getFieldCount()];
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			fieldTypes[i] = metadata.getFieldType(i);
		}
		return fieldTypes;
	}

	public static boolean isWritable(Type fieldType) {
		return (fieldType.getConstraint(ConstraintNames.READONLY) == null)
				&& (fieldType.getConstraint(ConstraintNames.AUTO_INCREMENT) == null);
	}
}