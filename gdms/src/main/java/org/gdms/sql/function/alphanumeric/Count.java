/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.function.alphanumeric;

import org.apache.log4j.Logger;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.AbstractAggregateFunction;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;

/**
 * This function computes the SQL count, @see getDescription().
 */
public class Count extends AbstractAggregateFunction {

        private Value v = ValueFactory.createValue(0L);
        private static final Value ONE = ValueFactory.createValue(1);
        private static final Logger LOG = Logger.getLogger(Count.class);

        @Override
        public String getName() {
                return "Count";
        }

        @Override
        public void evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {
                LOG.trace("Evaluating");
                if (args.length == 1 && !args[0].isNull()) {
                        v = v.sum(ONE);
                } else if (args.length == 0) {
                        v = v.sum(ONE);
                }
        }

        @Override
        public int getType(int[] types) {
                return Type.LONG;
        }

        @Override
        public String getDescription() {
                return "Count the number of values that are not null. If "
                        + "'*' is used, it counts the number of rows";
        }

        @Override
        public String getSqlOrder() {
                return "select [Count() | Count(*) | Count(column)] from myTable;";
        }

        @Override
        public Value getAggregateResult() {
                LOG.trace("Returning aggregated result");
                return v;
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(Type.LONG),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.INT),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.LONG),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.SHORT),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.BYTE),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.FLOAT),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.DOUBLE),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.BINARY),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.BOOLEAN),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.DATE),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.GEOMETRY),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.RASTER),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.STRING),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.TIME),
                                new BasicFunctionSignature(Type.LONG, ScalarArgument.TIMESTAMP)
                        };
        }
}
