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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.sql.function.AbstractScalarFunction;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.function.SameTypeFunctionSignature;
import org.gdms.sql.function.ScalarArgument;

/**
 * DO NOT USE!
 * @deprecated this function is a duplicate of isUID, and has nothing to do with primary keys...
 */
public class Pk extends AbstractScalarFunction {

        private Set<Value> setOfUniqValues;
        private static final Logger LOG = Logger.getLogger(Pk.class);

        @Override
        public Value evaluate(SQLDataSourceFactory dsf, Value... args)
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
                return args[0];
        }

        @Override
        public String getName() {
                return "Pk";
        }

        @Override
        public boolean isAggregate() {
                return false;
        }

        @Override
        public Type getType(Type[] types) {
                final int typeCode = types[0].getTypeCode();
                final Constraint[] constraints = types[0].getConstraints();
                final List<Constraint> lc = new LinkedList<Constraint>(Arrays.asList(constraints));
                lc.add(ConstraintFactory.createConstraint(Constraint.PK));

                try {
                        return TypeFactory.createType(typeCode, lc.toArray(new Constraint[lc.size()]));
                } catch (InvalidTypeException e) {
                        throw new UnsupportedOperationException(e);
                }
        }

        @Override
        public String getDescription() {
                return "Set a primary key constraint to the corresponding field";
        }

        @Override
        public String getSqlOrder() {
                return "select Pk(\"fieldName\") from myTable;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new SameTypeFunctionSignature(ScalarArgument.DOUBLE),
                                new SameTypeFunctionSignature(ScalarArgument.LONG),
                                new SameTypeFunctionSignature(ScalarArgument.SHORT),
                                new SameTypeFunctionSignature(ScalarArgument.FLOAT),
                                new SameTypeFunctionSignature(ScalarArgument.INT),
                                new SameTypeFunctionSignature(ScalarArgument.BYTE),
                                new SameTypeFunctionSignature(ScalarArgument.BOOLEAN),
                                new SameTypeFunctionSignature(ScalarArgument.BINARY),
                                new SameTypeFunctionSignature(ScalarArgument.STRING),
                                new SameTypeFunctionSignature(ScalarArgument.TIME),
                                new SameTypeFunctionSignature(ScalarArgument.TIMESTAMP)
                        };
        }
}
