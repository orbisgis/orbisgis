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

import org.gdms.sql.function.table.TableFunction;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.sql.engine.SemanticException;

/**
 * Helper class for validating arguments and functions
 * @author Antoine Gourlay
 */
public final class FunctionValidator {

        /**
         * Fails if any of the values is null
         * @param values an array of values
         * @throws FunctionException if any of the values is null
         */
        public static void failIfNull(Value... values) throws FunctionException {
                for (Value value : values) {
                        if (value.getType() == Type.NULL) {
                                throw new FunctionException("Cannot operate on null values");
                        }
                }
        }

        /**
         * Fails if <code>argumentType.length != i</code>
         * @param function a function
         * @param argumentsTypes an array of argument types
         * @param i the expected number of argument
         * @throws IncompatibleTypesException if <code>argumentType.length != i</code>
         */
        public static void failIfBadNumberOfArguments(Function function,
                Type[] argumentsTypes, int i) {
                if (argumentsTypes.length != i) {
                        throw new IncompatibleTypesException("The function "
                                + function.getName() + " has a wrong number of arguments: "
                                + i + " expected");
                }
        }

        /**
         * Fails if the value does not have the requested type
         * @param value a value
         * @param type the requested type
         * @throws FunctionException if the value does not have the requested type
         */
        public static void failIfNotOfType(Value value, int type)
                throws FunctionException {
                if (type != value.getType()) {
                        throw new FunctionException(value.toString() + " is not of type "
                                + type);
                }
        }

        /**
         * Fails if the given type is not numeric
         * @param function a function
         * @param type the type to test
         * @throws IncompatibleTypesException if the given type is not numeric
         */
        public static void failIfNotNumeric(Function function, Type type) {
                if (!TypeFactory.isNumerical(type.getTypeCode())) {
                        throw new IncompatibleTypesException("Function "
                                + function.getName()
                                + " only operates with numerical types. "
                                + TypeFactory.getTypeName(type.getTypeCode()) + " found");
                }
        }

        public static void failIfFieldDoesNotExist(final TableFunction customQuery,
                final String fieldName, final int fieldIndex) throws DriverException, SemanticException {
                if (-1 == fieldIndex) {
                        throw new SemanticException(customQuery.getName()
                                + ": no fieldname '" + fieldName + "' in your table !");
                }
        }

        /**
         * Fails is the field with index <tt>fieldIndex</tt> of a metadata
         * object is not of the right type
         * @param customQuery
         * @param fieldName
         * @param fieldIndex
         * @param typeCodeOfField
         * @param metadata
         * @throws DriverException
         * @throws SemanticException
         */
        public static void failIfFieldIsNotOfType(final TableFunction customQuery,
                final String fieldName, final int fieldIndex,
                final int typeCodeOfField, final Metadata metadata)
                throws DriverException, SemanticException {
                failIfFieldDoesNotExist(customQuery, fieldName, fieldIndex);

                final Type[] fieldTypes = MetadataUtilities.getFieldTypes(metadata);
                if (typeCodeOfField != fieldTypes[fieldIndex].getTypeCode()) {
                        throw new IncompatibleTypesException(customQuery.getName() + ": "
                                + fieldName + " is not of type "
                                + TypeFactory.getTypeName(typeCodeOfField));
                }
        }

        /**
         * Fails if the number of metadata objects does not match the parameter
         * <tt>numberOfTable</tt>
         * @param customQuery
         * @param metadatas
         * @param numberOfTables
         * @throws SemanticException
         */
        public static void failIfBadNumberOfTables(final TableFunction customQuery,
                final Metadata[] metadatas, final int numberOfTables)
                throws SemanticException {
                if (numberOfTables != metadatas.length) {
                        throw new SemanticException(customQuery.getName()
                                + " has a wrong number of arguments: " + numberOfTables
                                + " expected !");
                }
        }

        /**
         * Fails if the <tt>metadata</tt> does not contain a spatial field.
         * @param customQuery
         * @param metadata
         * @param argNumber
         * @throws SemanticException
         * @throws DriverException
         */
        public static void failIfNotSpatialDataSource(
                final TableFunction customQuery, final Metadata metadata,
                final int argNumber) throws SemanticException, DriverException {
                if (!MetadataUtilities.isGeometry(metadata)) {
                        throw new SemanticException(customQuery.getName()
                                + " requires a spatial table as argument number "
                                + argNumber);
                }
        }

        /**
         * Fails if the <tt>metadata</tt> does not contain a raster.
         * @param customQuery
         * @param metadata
         * @param argNumber
         * @throws SemanticException
         * @throws DriverException
         */
        public static void failIfNotRasterDataSource(
                final TableFunction customQuery, final Metadata metadata,
                final int argNumber) throws SemanticException, DriverException {
                if (!MetadataUtilities.isRaster(metadata)) {
                        throw new SemanticException(customQuery.getName()
                                + " requires a raster table as argument number "
                                + argNumber);
                }
        }

        /**
         * Fails if the given <tt>type</tt> is not numeric.
         * @param customQuery
         * @param type
         * @param argNumber
         */
        public static void failIfNotNumeric(final Function customQuery,
                final Type type, final int argNumber) {
                if (!TypeFactory.isNumerical(type.getTypeCode())) {
                        throw new IncompatibleTypesException(customQuery.getName()
                                + " requires a numerical type as argument number "
                                + argNumber + ". "
                                + TypeFactory.getTypeName(type.getTypeCode()) + " found");
                }
        }

        /**
         * Fails is the array of types does not match any FunctionSignature.
         * @param types
         * @param signs
         */
        public static void failIfTypesDoNotMatchSignature(Type[] types, FunctionSignature[] signs) {
                boolean ok;
                for (int i = 0; i < signs.length; i++) {
                        FunctionSignature sign = signs[i];
                        Argument[] arguments = sign.getArguments();
                        if (sign.isScalarReturn() && types.length != arguments.length) {
                                continue;
                        }
                        if (types.length == 0) {
                                // special case for empty call
                                boolean scalarArg = false;
                                for (int j = 0; j < arguments.length; j++) {
                                        if (arguments[j].isScalar()) {
                                                scalarArg = true;
                                        }
                                }
                                if (!scalarArg) {
                                        return;
                                }
                        } else {
                                ok = true;
                                int typeId = 0;
                                for (int j = 0; j < arguments.length; j++) {
                                        if (arguments[j].isScalar()) {
                                                if (typeId == types.length) {
                                                        ok = false;
                                                        break;
                                                }
                                                ScalarArgument arg = (ScalarArgument) arguments[j];
                                                if (!TypeFactory.canBeCastTo(types[typeId].getTypeCode(), arg.getTypeCode())) {
                                                        ok = false;
                                                        break;
                                                }
                                                typeId++;
                                        }
                                }
                                if (ok && typeId == types.length) {
                                        return;
                                }
                        }
                }
                throw new IncompatibleTypesException("The given parameter types does not match any signature.");
        }

        /**
         * Fails if the array of metadata does not match any FunctionSignature.
         * @param metadata
         * @param signs
         * @throws SemanticException
         * @throws DriverException
         */
        public static void failIfTablesDoNotMatchSignature(Metadata[] metadata, FunctionSignature[] signs) throws SemanticException, DriverException {
                if (metadata == null) {
                        metadata = new Metadata[0];
                }
                boolean ok;
                for (int i = 0; i < signs.length; i++) {
                        FunctionSignature sign = signs[i];
                        Argument[] arguments = sign.getArguments();
                        ok = true;
                        int tableId = 0;
                        for (int j = 0; j < arguments.length; j++) {
                                if (arguments[j].isTable()) {
                                        if (tableId == metadata.length) {
                                                ok = false;
                                                break;
                                        }
                                        TableArgument arg = (TableArgument) arguments[j];
                                        if (!arg.isValid(metadata[tableId])) {
                                                ok = false;
                                                break;
                                        }
                                        tableId++;
                                }
                        }
                        if (ok && tableId == metadata.length) {
                                return;
                        }
                }
                throw new SemanticException("The tables provided do not match the definitions.");
        }

        /**
         * Private constructor for static utility class.
         */
        private FunctionValidator() {
        }
}
