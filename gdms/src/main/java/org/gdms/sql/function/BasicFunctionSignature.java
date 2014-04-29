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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
package org.gdms.sql.function;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;

/**
 * A BasicFunctionSignature is a simple implementation of {@code FunctionSignature}
 * that is composed of a single return {@code Type} and an array of {@code ScalarArgument}.
 * It is mainly used to represent the signature of {@code ScalarFunction}, that will return
 * a {@code Value} computed using input as defined in the arguments, and compatible
 * with the specified return {@code Type}
 * @author Antoine Gourlay
 */
public class BasicFunctionSignature implements FunctionSignature {

        private Type type;
        private ScalarArgument[] args;

        public BasicFunctionSignature(Type type, ScalarArgument... args) {
                this.type = type;
                ScalarArgument[] sargs = args;
                if (sargs == null) {
                        sargs = new ScalarArgument[0];
                }
                this.args = sargs;
        }

        public BasicFunctionSignature(int typeCode, ScalarArgument... args) {
                this(TypeFactory.createType(typeCode), args);
        }

        public BasicFunctionSignature(Type type) {
                this(type, new ScalarArgument[0]);
        }

        public Type getReturnType() {
                return type;
        }

        @Override
        public boolean isScalarReturn() {
                return true;
        }

        @Override
        public boolean isTableReturn() {
                return false;
        }

        @Override
        public ScalarArgument[] getArguments() {
                return args;
        }
}
