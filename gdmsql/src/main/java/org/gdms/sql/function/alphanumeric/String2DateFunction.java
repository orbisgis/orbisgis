/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 * info@orbisgis.org
 */
package org.gdms.sql.function.alphanumeric;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.AbstractScalarFunction;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;

/**
 * Converts a string into a date.
 */
public class String2DateFunction extends AbstractScalarFunction {

        private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        @Override
        public Value evaluate(SQLDataSourceFactory dsf, Value... args) throws FunctionException {
                DateFormat df;

                if (args[0].isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (args.length == 2) {
                        if (args[1].isNull()) {
                                return ValueFactory.createNullValue();
                        } else {
                                df = new SimpleDateFormat(args[1].getAsString());
                        }
                } else {
                        df = dateFormat;
                }

                try {
                        return ValueFactory.createValue(df.parse(args[0].getAsString()));
                } catch (ParseException e) {
                        throw new FunctionException(
                                "date format must match DateFormat java class requirements",
                                e);
                }
        }

        @Override
        public String getName() {
                return "StringToDate";
        }

        @Override
        public Type getType(Type[] types) {
                return TypeFactory.createType(Type.DATE);
        }

        @Override
        public String getDescription() {
                return "Converts a string into a date";
        }

        @Override
        public String getSqlOrder() {
                return "select StringToDate('date_literal'[ , date_format]) from mytable";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null), ScalarArgument.STRING),
                                new BasicFunctionSignature(getType(null), new ScalarArgument[]{
                                        ScalarArgument.STRING,
                                        ScalarArgument.STRING
                                })
                        };
        }
}
