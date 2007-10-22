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
package org.gdms.data.types;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;

public class TypeFactory {
	public static Type createType(final int typeCode)
			throws InvalidTypeException {
		return createType(typeCode, DefaultType.typesDescription.get(typeCode));
	}

	public static Type createType(final int typeCode, final String typeName)
			throws InvalidTypeException {
		if (null == typeName) {
			return createType(typeCode);
		} else {
			final TypeDefinition typeDef = new DefaultTypeDefinition(typeName,
					typeCode);
			return typeDef.createType();
		}
	}

	public static Type createType(final int typeCode,
			final Constraint[] constraints) throws InvalidTypeException {
		if (null == constraints) {
			return createType(typeCode);
		} else {
			return createType(typeCode, DefaultType.typesDescription
					.get(typeCode), constraints);
		}
	}

	public static Type createType(final int typeCode, final String typeName,
			final Constraint[] constraints) throws InvalidTypeException {
		if (null == constraints) {
			return createType(typeCode, typeName);
		} else {
			final int fc = constraints.length;
			final ConstraintNames[] constraintNames = new ConstraintNames[fc];
			for (int i = 0; i < fc; i++) {
				constraintNames[i] = constraints[i].getConstraintName();
			}
			final TypeDefinition typeDef = new DefaultTypeDefinition(typeName,
					typeCode, constraintNames);
			return typeDef.createType(constraints);
		}
	}

	public static boolean IsSpatial(DataSource ds) throws DriverException {

		ds.open();
		Metadata m = ds.getMetadata();
		boolean isSpatial = false;
		for (int i = 0; i < m.getFieldCount(); i++) {
			if (m.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
				isSpatial = true;
				break;
			}
		}
		ds.cancel();

		return isSpatial;

	}
}