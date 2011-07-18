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
package org.gdms.sql.function.spatial.geometry.convert;

import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.geometryUtils.GeometryEdit;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * Changes the metadata of the parameter by setting its dimension to 3D.
 */
public final class ST_Force_3D extends AbstractScalarSpatialFunction {

        @Override
        public Value evaluate(SQLDataSourceFactory dsf, Value... args)
                throws FunctionException {
                if (!args[0].isNull()) {
                        if (args.length == 2) {
                                return ValueFactory.createValue(GeometryEdit.force3D(args[0].getAsGeometry(), args[1].getAsDouble(), true));
                        } else {
                                return ValueFactory.createValue(GeometryEdit.force3D(args[0].getAsGeometry(), 0, false));
                        }
                }
                return ValueFactory.createNullValue();
        }

        @Override
        public String getDescription() {
                return "Forces the geometries into XYZ mode. Metadata are also modified. A specific z can be added.";
        }

        @Override
        public String getName() {
                return "ST_Force_3D";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_Force_3D(the_geom[, z]) from myTable";
        }

        @Override
        public Type getType(Type[] argsTypes) {
                if (argsTypes != null) {
                        Type type = argsTypes[0];
                        Constraint[] constrs = type.getConstraints(Constraint.ALL
                                & ~Constraint.GEOMETRY_DIMENSION);
                        Constraint[] result = new Constraint[constrs.length + 1];
                        System.arraycopy(constrs, 0, result, 0, constrs.length);
                        result[result.length - 1] = ConstraintFactory.createConstraint(Constraint.GEOMETRY_DIMENSION, 3);
                        return TypeFactory.createType(Type.GEOMETRY, result);
                } else {
                        return TypeFactory.createType(Type.GEOMETRY);
                }
        }
        
        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[] {
                  new BasicFunctionSignature(getType(null), ScalarArgument.GEOMETRY),
                  new BasicFunctionSignature(getType(null), ScalarArgument.GEOMETRY, ScalarArgument.DOUBLE)
                };
        }
}
