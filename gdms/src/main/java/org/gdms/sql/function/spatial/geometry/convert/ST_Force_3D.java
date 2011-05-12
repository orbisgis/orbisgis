/*
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
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.sql.function.spatial.geometry.convert;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.geometryUtils.GeometryEdit;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class ST_Force_3D implements Function {

        public Value evaluate(DataSourceFactory dsf, Value[] args)
                throws FunctionException {
                if (args.length == 2) {
                        return ValueFactory.createValue(GeometryEdit.force_3D(args[0].getAsGeometry(), args[1].getAsDouble(), true));
                } else {
                        return ValueFactory.createValue(GeometryEdit.force_3D(args[0].getAsGeometry(), 0, false));
                }
        }

        public String getDescription() {
                return "Forces the geometries into XYZ mode. Metadata are also modified. A specific z can be added.";
        }

        public String getName() {
                return "ST_Force_3D";
        }

        public String getSqlOrder() {
                return "select ST_Force_3D(the_geom [, z]) from myTable";
        }

        public boolean isAggregate() {
                return false;
        }

        public Arguments[] getFunctionArguments() {
                return new Arguments[]{new Arguments(Argument.GEOMETRY), new Arguments(Argument.GEOMETRY, Argument.NUMERIC)};
        }

        public Type getType(Type[] argsTypes) {
                Type type = argsTypes[0];
                Constraint[] constrs = type.getConstraints(Constraint.ALL
                        & ~Constraint.GEOMETRY_DIMENSION);
                Constraint[] result = new Constraint[constrs.length + 1];
                System.arraycopy(constrs, 0, result, 0, constrs.length);
                result[result.length - 1] = new DimensionConstraint(3);

                return TypeFactory.createType(type.getTypeCode(), result);
        }

        @Override
        public Value getAggregateResult() {
                return null;
        }
}
