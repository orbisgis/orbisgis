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

/**
 * factory to create data type instances
 *
 * @author Fernando Gonzalez Cortes
 */
public class TypeFactory {
	/**
	 * Creates a type with the specified type code. The code must be one of the
	 * constants in Type interface
	 *
	 * @param typeCode
	 * @return
	 */
	public static Type createType(final int typeCode) {
		return createType(typeCode, DefaultType.typesDescription.get(typeCode));
	}

	/**
	 * Creates a type with the specified type code and the specified name. The
	 * code must be one of the constants in Type interface
	 *
	 * @param typeCode
	 * @param typeName
	 * @return
	 */
	public static Type createType(final int typeCode, final String typeName) {
		if (null == typeName) {
			return createType(typeCode);
		} else {
			final TypeDefinition typeDef = new DefaultTypeDefinition(typeName,
					typeCode);
			try {
				return typeDef.createType();
			} catch (InvalidTypeException e) {
				throw new RuntimeException("bug", e);
			}
		}
	}

	/**
	 * Creates a type with the specified type code and the specified
	 * constraints. The code must be one of the constants in Type interface
	 *
	 * @param typeCode
	 * @param constraints
	 * @return
	 * @throws InvalidTypeException
	 *             If the constraints are not valid for this type
	 */
	public static Type createType(final int typeCode,
			final Constraint... constraints) throws InvalidTypeException {
		if (null == constraints) {
			return createType(typeCode);
		} else {
			return createType(typeCode, DefaultType.typesDescription
					.get(typeCode), constraints);
		}
	}

	/**
	 * Creates a type with the specified type code and the specified constraints
	 * and name. The code must be one of the constants in Type interface
	 *
	 * @param typeCode
	 * @param constraints
	 * @return
	 * @throws InvalidTypeException
	 *             If the constraints are not valid for this type
	 */
	public static Type createType(final int typeCode, final String typeName,
			final Constraint[] constraints) throws InvalidTypeException {
		if (null == constraints) {
			return createType(typeCode, typeName);
		} else {
			final int fc = constraints.length;
			final int[] constraintNames = new int[fc];
			for (int i = 0; i < fc; i++) {
				constraintNames[i] = constraints[i].getConstraintCode();
			}
			final TypeDefinition typeDef = new DefaultTypeDefinition(typeName,
					typeCode, constraintNames);
			return typeDef.createType(constraints);
		}
	}

	public static String getTypeName(int typeCode) {
		switch (typeCode) {
		case Type.BINARY:
			return "binary";
		case Type.BOOLEAN:
			return "boolean";
		case Type.BYTE:
			return "byte";
		case Type.COLLECTION:
			return "value collection";
		case Type.DATE:
			return "date";
		case Type.DOUBLE:
			return "double";
		case Type.FLOAT:
			return "float";
		case Type.GEOMETRY:
			return "geometry";
		case Type.INT:
			return "int";
		case Type.LONG:
			return "long";
		case Type.NULL:
			return "null";
		case Type.SHORT:
			return "short";
		case Type.STRING:
			return "string";
		case Type.TIME:
			return "time";
		case Type.TIMESTAMP:
			return "timestamp";
		default:
			throw new IllegalArgumentException("Unknown data type: " + typeCode);
		}
	}

	public static boolean isNumerical(int typeCode) {
		return (typeCode == Type.BYTE) || (typeCode == Type.DOUBLE)
				|| (typeCode == Type.FLOAT) || (typeCode == Type.INT)
				|| (typeCode == Type.LONG) || (typeCode == Type.SHORT);
	}
}