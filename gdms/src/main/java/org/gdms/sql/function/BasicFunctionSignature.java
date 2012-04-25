/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
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
 **/
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
