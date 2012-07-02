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
package org.gdms.sql.function.spatial.geometry.edit;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.GeometryDimensionConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.geometryUtils.GeometryEdit;
import org.gdms.sql.function.AbstractScalarFunction;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;

public class ST_SetZToExtremities extends AbstractScalarFunction {

        @Override
        public final Value evaluate(DataSourceFactory dsf, Value[] args) throws FunctionException {
                Geometry geom = args[0].getAsGeometry();
                double startZ = args[1].getAsDouble();
                double endZ = args[2].getAsDouble();
                if (geom instanceof MultiLineString) {
                        int nbGeom = geom.getNumGeometries();
                        LineString[] lines = new LineString[nbGeom];
                        for (int i = 0; i < nbGeom; i++) {
                                Geometry subGeom = geom.getGeometryN(i);
                                lines[i] = (LineString) GeometryEdit.force3DStartEnd(subGeom, startZ, endZ);
                        }

                } else if (geom instanceof LineString) {
                        geom = GeometryEdit.force3DStartEnd(geom, startZ, endZ);
                }


                return ValueFactory.createValue(geom, args[0].getCRS());

        }

        @Override
        public final String getDescription() {
                return "This function modify (or set) the z component of each vertex extremities lines"
                        + " given by a two fields.";
        }

        @Override
        public final String getName() {
                return "ST_SetZToExtremities";
        }

        @Override
        public final String getSqlOrder() {
                return "select ST_SetZToExtremities(the_geom, startz, endz) from lines;";
        }

        @Override
        public final int getType(int[] argsTypes) {
                return argsTypes[0];
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(TypeFactory.createType(Type.GEOMETRY,
                                new GeometryDimensionConstraint(GeometryDimensionConstraint.DIMENSION_CURVE)),
                                ScalarArgument.LINESTRING,
                                ScalarArgument.DOUBLE,
                                ScalarArgument.DOUBLE),
                                new BasicFunctionSignature(TypeFactory.createType(Type.GEOMETRY,
                                new GeometryDimensionConstraint(GeometryDimensionConstraint.DIMENSION_CURVE)),
                                ScalarArgument.MULTILINESTRING,
                                ScalarArgument.DOUBLE,
                                ScalarArgument.DOUBLE)};
        }
}
