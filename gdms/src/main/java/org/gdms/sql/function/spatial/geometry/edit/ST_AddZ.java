/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.sql.function.spatial.geometry.edit;

import com.vividsolutions.jts.geom.Geometry;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Dimension3DConstraint;
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
 * This function modify (or set) the z component of (each vertex of) the
 * geometric parameter to the corresponding value given by a field.
 */
public final class ST_AddZ extends AbstractScalarSpatialFunction {

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {
                if ((args[0].isNull()) || (args[1].isNull())) {
                        return ValueFactory.createNullValue();
                }

                Geometry geometry = args[0].getAsGeometry();

                double zFieldValue = args[1].getAsDouble();

                Geometry geom = GeometryEdit.force3D(geometry, zFieldValue, true);

                return ValueFactory.createValue(geom, args[0].getCRS());

        }

        @Override
        public String getDescription() {
                return "This function modify (or set) the z component of (each vertex of) the "
                        + "geometric parameter to the corresponding value.";
        }

        @Override
        public String getName() {
                return "ST_AddZ";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_AddZ(the_geom, z_value) from contourlines b;";
        }

        @Override
        public Type getType(Type[] argsTypes) {

                Type type = argsTypes[0];
                Constraint[] constrs = type.getConstraints(Constraint.ALL
                        & ~Constraint.DIMENSION_3D_GEOMETRY);
                Constraint[] result = new Constraint[constrs.length + 1];
                System.arraycopy(constrs, 0, result, 0, constrs.length);
                result[result.length - 1] = new Dimension3DConstraint(3);

                return TypeFactory.createType(Type.GEOMETRY, result);

        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(new Type[]{TypeFactory.createType(Type.GEOMETRY)}),
                                ScalarArgument.GEOMETRY,
                                ScalarArgument.DOUBLE)
                        };
        }
}
