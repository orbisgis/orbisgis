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
package org.gdms.sql.function;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;

public class FunctionValidator {

	public static void failIfNull(Value... values) throws FunctionException {
		for (Value value : values) {
			if (value.getType() == Type.NULL) {
				throw new FunctionException("Cannot operate in null values");
			}
		}
	}

	public static void failIfBadNumberOfArguments(Function function,
			Type[] argumentsTypes, int i) throws IncompatibleTypesException {
		if (argumentsTypes.length != i) {
			throw new IncompatibleTypesException("The function "
					+ function.getName() + " has a wrong number of arguments: "
					+ i + " expected");
		}
	}

	public static void failIfBadNumberOfArguments(Function function,
			Type[] argumentsTypes, int... numbers)
			throws IncompatibleTypesException {
		for (int j : numbers) {
			if (j == argumentsTypes.length) {
				return;
			}
		}
		throw new IncompatibleTypesException("The function "
				+ function.getName() + " has a wrong number of arguments");
	}

	public static void failIfNumberOfArguments(Function function,
			Type[] argumentsTypes, int i) throws IncompatibleTypesException {
		if (argumentsTypes.length == i) {
			throw new IncompatibleTypesException("The function "
					+ function.getName() + " cannot have " + i + " arguments.");
		}
	}

	public static void failIfNotOfType(Value value, int type)
			throws FunctionException {
		if (type != value.getType()) {
			throw new FunctionException(value.toString() + " is not of type "
					+ type);
		}
	}

	public static void failIfNotNumeric(Function function, Type type)
			throws IncompatibleTypesException {
		if (!TypeFactory.isNumerical(type.getTypeCode())) {
			throw new IncompatibleTypesException("Function "
					+ function.getName()
					+ " only operates with numerical types. "
					+ TypeFactory.getTypeName(type.getTypeCode()) + " found");
		}
	}

	public static void failIfNotOfTypes(Function function, Type type,
			int... typesCodes) throws IncompatibleTypesException {
		for (int typeCode : typesCodes) {
			if (type.getTypeCode() == typeCode) {
				return;
			}
		}
		throw new IncompatibleTypesException(TypeFactory.getTypeName(type
				.getTypeCode())
				+ " is not allowed with function " + function.getName());
	}

	public static void failIfNotOfType(Function function, Type type,
			int typeCode) throws IncompatibleTypesException {
		if (type.getTypeCode() != typeCode) {
			throw new IncompatibleTypesException("Function "
					+ function.getName() + " only operates with "
					+ TypeFactory.getTypeName(typeCode) + " types. "
					+ TypeFactory.getTypeName(type.getTypeCode()) + " found");
		}
	}

	public static void failIfNotOfType(CustomQuery customQuery, Type type,
			int typeCode) {
		if (type.getTypeCode() != typeCode) {
			throw new IncompatibleTypesException("Function "
					+ customQuery.getName() + " only operates with "
					+ TypeFactory.getTypeName(typeCode) + " types. "
					+ TypeFactory.getTypeName(type.getTypeCode()) + " found");
		}
	}

	public static void failIfNotOfTypes(CustomQuery customQuery, Type type,
			int... typesCodes) {
		for (int typeCode : typesCodes) {
			if (type.getTypeCode() == typeCode) {
				return;
			}
		}
		throw new IncompatibleTypesException(TypeFactory.getTypeName(type
				.getTypeCode())
				+ " is not allowed with custom query " + customQuery.getName());
	}

	public static void failIfBadNumberOfArguments(CustomQuery customQuery,
			Type[] argumentsTypes, int... numbers) {
		for (int j : numbers) {
			if (j == argumentsTypes.length) {
				return;
			}
		}
		throw new IncompatibleTypesException("The function "
				+ customQuery.getName()
				+ " has a wrong number of arguments. Usage:"
				+ customQuery.getSqlOrder());
	}

	public static void failIfFieldDoesNotExist(final CustomQuery customQuery,
			final String fieldName, final int fieldIndex,
			final Metadata metadata) throws DriverException, SemanticException {
		if (-1 == fieldIndex) {
			throw new SemanticException(customQuery.getName()
					+ ": no fieldname '" + fieldName + "' in your table !");
		}
	}

	public static void failIfFieldIsNotOfType(final CustomQuery customQuery,
			final String fieldName, final int fieldIndex,
			final int typeCodeOfField, final Metadata metadata)
			throws DriverException, SemanticException {
		failIfFieldDoesNotExist(customQuery, fieldName, fieldIndex, metadata);

		final Type[] fieldTypes = MetadataUtilities.getFieldTypes(metadata);
		if (typeCodeOfField != fieldTypes[fieldIndex].getTypeCode()) {
			throw new IncompatibleTypesException(customQuery.getName() + ": "
					+ fieldName + " is not of type "
					+ TypeFactory.getTypeName(typeCodeOfField));
		}
	}

	public static void failIfBadNumberOfTables(final CustomQuery customQuery,
			final Metadata[] metadatas, final int numberOfTables)
			throws SemanticException {
		if (numberOfTables != metadatas.length) {
			throw new SemanticException(customQuery.getName()
					+ " has a wrong number of arguments: " + numberOfTables
					+ " expected !");
		}
	}

	public static void failIfNotSpatialDataSource(
			final CustomQuery customQuery, final Metadata metadata,
			final int argNumber) throws SemanticException, DriverException {
		if (!MetadataUtilities.isGeometry(metadata)) {
			throw new SemanticException(customQuery.getName()
					+ " requires a spatial table as argument number "
					+ argNumber);
		}
	}

	public static void failIfNotRasterDataSource(
			final CustomQuery customQuery, final Metadata metadata,
			final int argNumber) throws SemanticException, DriverException {
		if (!MetadataUtilities.isRaster(metadata)) {
			throw new SemanticException(customQuery.getName()
					+ " requires a raster table as argument number "
					+ argNumber);
		}
	}

	public static void failIfNotNumeric(final CustomQuery customQuery,
			final Type type, final int argNumber)
			throws IncompatibleTypesException {
		if (!TypeFactory.isNumerical(type.getTypeCode())) {
			throw new IncompatibleTypesException(customQuery.getName()
					+ " requires a numerical type as argument number "
					+ argNumber + ". "
					+ TypeFactory.getTypeName(type.getTypeCode()) + " found");
		}
	}

}