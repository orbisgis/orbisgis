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

import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.AbstractScalarFunction;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.FunctionValidator;

/**
 * This function checks if all values in a column are unique, i.e. no values are equal.
 * 
 * WARNING: this function always return true! If duplicated elements are found, it crashes...
 * Fortunately for him I don't know who write this...
 */
public class IsUID  extends AbstractScalarFunction {

        private Set<Value> setOfUniqValues;
        private static final Logger LOG = Logger.getLogger(IsUID.class);

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... args)
                throws FunctionException {
                LOG.trace("Evaluating");
                FunctionValidator.failIfNull(args[0]);
                if (null == setOfUniqValues) {
                        setOfUniqValues = new HashSet<Value>();
                }

                if (setOfUniqValues.contains(args[0])) {
                        throw new FunctionException("Value " + args[0]
                                + " already exists : redundancy is forbidden !");
                } else {
                        setOfUniqValues.add(args[0]);
                }
                return ValueFactory.createValue(true);
        }

        @Override
        public String getName() {
                return "IsUID";
        }

        @Override
        public String getDescription() {
                return "Check if the column is an unique identifier";
        }

        @Override
        public String getSqlOrder() {
                return "select * from myTable where isUID(column);";
        }

        @Override
        public Type getType(Type[] argsTypes) {
                return TypeFactory.createType(Type.BOOLEAN);
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                Type type = getType(null);
                return new FunctionSignature[] {
                new BasicFunctionSignature(type, ScalarArgument.LONG)
                };
        }
}
